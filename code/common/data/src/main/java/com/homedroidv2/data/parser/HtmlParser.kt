package com.homedroidv2.data.parser

import android.content.Context
import android.util.Log
import com.homedroidv2.data.model.Device
import com.homedroidv2.data.model.Group
import com.homedroidv2.data.network.HtmlClient
import com.homedroidv2.data.repositories.GroupRepository
import com.fleeksoft.ksoup.Ksoup
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

class HtmlParser @Inject constructor(
    private val context: Context,
    private val groupRepository: GroupRepository,  // GroupRepository wird nun injiziert
    private val client: HtmlClient                 // HtmlClient wird injiziert
) {

    private lateinit var html: String
    private var index: Int = 0
    private val sharedPreferences = context.getSharedPreferences("HtmlParserPrefs", Context.MODE_PRIVATE)

    /*

     */
    suspend fun checkHtmlChanges(newHtml:String):Boolean
    {
        Log.i("DATA FROM SERVER", "checkHtmlChanges: $newHtml")
        if(newHtml != null)
        {
            Log.i("Parser", "HTML ist not Null")
            Log.i("Parser", "HTML: ${newHtml}")

            val currentHash = hashHtml(newHtml)
            val savedHash = sharedPreferences.getString("htmlHash", null)

            val file = File(context.filesDir, "html_snapshot.txt")
            file.writeText(newHtml)
            Log.i("Parser", "Datei gespeichert: ${file.exists()}, Pfad: ${file.absolutePath}")

             //if (savedHash != currentHash) {
                Log.i("Parser", "Parser Started")
                sharedPreferences.edit().putString("htmlHash", currentHash).apply()
                this.html = newHtml
                parseHtml(this.html)
            //}
            return true
        }
        return false
    }

    private fun hashHtml(html: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(html.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun parseHtml(html: String) {
        Log.i("PARSER", "In Parse HTML")

        val doc: com.fleeksoft.ksoup.nodes.Document = Ksoup.parse(html = html)
        groupRepository.deleteGroupTabel()
        Log.i("DATA STRUCTURE","Tag: ${html}")

        parseDeviceList(doc)
        //parseGroups(doc)
    }

    suspend fun parseDeviceList(doc: com.fleeksoft.ksoup.nodes.Document) {
        val scriptTag = doc.getElementsByTag("script").find { it.data().contains("var DeviceList") }
        val scriptContent = scriptTag?.data()
        val regex = Regex("""var\s+DeviceList\s*=\s*(\[[\s\S]*?]);""")
        val match = scriptContent?.let { regex.find(it) }
        Log.i("PARSER", "In Parse Device List ${match?.value}")
        val deviceListArray = match?.groups?.get(1)?.value ?: return
        val rawDevices = deviceListArray.split("],").map { it.trim().removePrefix("[").removeSuffix("]") }

        val devices = rawDevices.mapIndexed { index, deviceStr ->
            val fields = deviceStr.split(",").map { it.trim().trim('"') }
            ParsedDevices(
                deviceType = fields.getOrNull(0) ?: "",
                raum = fields.getOrNull(1) ?: "",
                name = fields.getOrNull(2) ?: "",
                adresse = fields.getOrNull(3) ?: "",
                parameter1 = fields.getOrNull(4) ?: "",
                htmlId = fields.getOrNull(5) ?: "",
                messwertTyp = fields.getOrNull(6) ?: "",
                maxAge = fields.getOrNull(7) ?: "",
                bfname = fields.getOrNull(8) ?: "",
                writeAdress = fields.getOrNull(9) ?: "",
                actionCondition = fields.getOrNull(10) ?: "",
                action = fields.getOrNull(11) ?: "",
                queryInterval = fields.getOrNull(12) ?: "",
                storeChanges = fields.getOrNull(13) ?: "",
                map2DECT = fields.getOrNull(14) ?: "",
                id = fields.getOrNull(15) ?: "",
                value = fields.getOrNull(16) ?: "10",
                status = fields.getOrNull(17)?.toBoolean() ?: false,
                favorite = fields.getOrNull(18)?.toBoolean() ?: false,
                deviceId = index.toString()
            )
        }
        groupDevicesByRoom(devices).forEach{ group ->
            Log.i("PARSER", "In Parse Device List ${group}")
            groupRepository.saveParsedGroups(group)
        }

        Log.i("PARSER", "In Parse Device List ${devices}")
        Log.i("PARSER", "In Parse Device List ${devices.size}")
    }

    suspend fun groupDevicesByRoom(devices: List<ParsedDevices>): List<ParsedGroup> {
        return devices
            .groupBy { it.raum }
            .entries
            .mapIndexed { index, entry ->
                ParsedGroup(
                    id = index,
                    name = entry.key,
                    iconUrl = client.getIcon(entry.key),
                    devices = entry.value.toMutableList()
                )
            }
    }

    suspend fun parseGroups(doc: com.fleeksoft.ksoup.nodes.Document)
    {
        Log.i("PARSER", "In Parse Groups")

        val groupNames = doc.select("[data-annotation='group']")
        var newGroupItem: Group
        groupNames.mapIndexed{ index, element ->
            if(element.firstElementChild()?.tagName() == "a")
            {
                val queryForGroupIcon = element.attr("data-info")
                val name = element.children().text()
                val listOfDevices = parseDevices(index, element, name )
                newGroupItem = Group( id = index, name = name, iconUrl = client.getIcon(queryForGroupIcon), devices = listOfDevices )
                Log.i("DATA GROUPS","Tag: ${element.children().text()}, Inhalt: ${element.ownText()}")
            }else
            {
                val queryForGroupIcon = element.attr("data-info")
                val name = element.ownText()
                val listOfDevices = parseDevices(index, element,name)
                newGroupItem = Group( id = index, name = name, iconUrl = client.getIcon(queryForGroupIcon), devices = listOfDevices)
            }
            groupRepository.addGroupToList(newGroupItem)
            Log.i("DATA GROUPS","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
        }
    }

    fun parseDevices(groupId: Int, element: com.fleeksoft.ksoup.nodes.Element, groupname: String): MutableList<Device>
    {
        val mutableDevices: MutableList<Device> = mutableListOf()

        val actiondevices = element.select("[data-annotation='actiondevice']")
        actiondevices.map {element ->
            Log.i("DATA DEVICES","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
            val newActionDevice = Device.ActionDevice( id= index.toString(), name = element.ownText(), group = groupname, groupid = groupId.toString(), type = "ActionDevice" )
            mutableDevices.add(newActionDevice)
            index++
        }

        val statusdevices = element.select("[data-annotation='statusdevice']")
        statusdevices.map {element ->
            Log.i("DATA DEVICES","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
            Device.StatusDevice(index.toString(), element.ownText(), )
            val newStatusDevice = Device.StatusDevice( id = index.toString(), name = element.ownText(), group = groupname, groupid = groupId.toString(), type = "StatusDevice" )
            mutableDevices.add(newStatusDevice)
            index++
        }

        val temperatureDevice = element.select("[data-annotation='tempdevice']")
        temperatureDevice.map {element ->
            Log.i("DATA DEVICES","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
            Device.TemperatureDevice(index.toString(), element.ownText(), )
            val newTemperatureDevice = Device.TemperatureDevice(id = index.toString(), name = element.ownText(), group = groupname, groupid = groupId.toString(), type = "TemperatureDevice" )
            mutableDevices.add(newTemperatureDevice)
            index++
        }
        return mutableDevices

    }


}