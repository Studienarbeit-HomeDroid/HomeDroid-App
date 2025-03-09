package com.homedroid.data.parser

import android.content.Context
import android.util.Log
import com.homedroid.data.model.Device
import com.homedroid.data.model.Group
import com.homedroid.data.network.HtmlClient
import com.homedroid.data.repositories.GroupRepository
import com.fleeksoft.ksoup.Ksoup
import java.security.MessageDigest

class HtmlParser(context: Context) {

    private lateinit var html: String
    private val groupRepository = GroupRepository()
    private val  client = HtmlClient()
    private var index: Int = 0
    private val sharedPreferences = context.getSharedPreferences("HtmlParserPrefs", Context.MODE_PRIVATE)

    /*

     */
    suspend fun checkHtmlChanges():Boolean
    {
        val newHtml = client.getHtml()
        Log.i("DATA FROM SERVER", "checkHtmlChanges: $newHtml")
        if(newHtml != null)
        {
            val currentHash = hashHtml(newHtml)
            val savedHash = sharedPreferences.getString("htmlHash", null)

            if (savedHash != currentHash) {
                sharedPreferences.edit().putString("htmlHash", currentHash).apply()
                parseHtml(newHtml)
            }
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
        Log.i("DATA FROM SERVER", this.html)
        val doc: com.fleeksoft.ksoup.nodes.Document = Ksoup.parse(html = html)
        groupRepository.deleteGroupTabel()
        parseGroups(doc)
    }

    suspend fun parseGroups(doc: com.fleeksoft.ksoup.nodes.Document)
    {
        val groupNames = doc.select("[data-annotation='group']")
        var newGroupItem: Group
        groupNames.mapIndexed{ index, element ->
            if(element.firstElementChild()?.tagName() == "a")
            {
                val queryForGroupIcon = element.attr("data-info")
                val name = element.children().text()
                val listOfDevices = parseDevices(element, name )
                newGroupItem = Group( id = index, name = name, iconUrl = client.getIcon(queryForGroupIcon), devices = listOfDevices )
                Log.i("DATA GROUPS","Tag: ${element.children().text()}, Inhalt: ${element.ownText()}")
            }else
            {
                val queryForGroupIcon = element.attr("data-info")
                val name = element.ownText()
                val listOfDevices = parseDevices(element,name)
                newGroupItem = Group( id = index, name = name, iconUrl = client.getIcon(queryForGroupIcon), devices = listOfDevices)
            }
            groupRepository.addGroupToList(newGroupItem)
            Log.i("DATA GROUPS","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
        }
    }

    fun parseDevices(element: com.fleeksoft.ksoup.nodes.Element, groupname: String): MutableList<Device>
    {
        val mutableDevices: MutableList<Device> = mutableListOf()

        val actiondevices = element.select("[data-annotation='actiondevice']")
        actiondevices.map {element ->
            Log.i("DATA DEVICES","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
            val newActionDevice = Device.ActionDevice( id= index.toString(), name = element.ownText(), group = groupname )
            mutableDevices.add(newActionDevice)
            index++
        }

        val statusdevices = element.select("[data-annotation='statusdevice']")
        statusdevices.map {element ->
            Log.i("DATA DEVICES","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
            Device.StatusDevice(index.toString(), element.ownText(), )
            val newStatusDevice = Device.StatusDevice( id = index.toString(), name = element.ownText(), group = groupname )
            mutableDevices.add(newStatusDevice)
            index++
        }

        val temperatureDevice = element.select("[data-annotation='tempdevice']")
        temperatureDevice.map {element ->
            Log.i("DATA DEVICES","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
            Device.TemperatureDevice(index.toString(), element.ownText(), )
            val newTemperatureDevice = Device.TemperatureDevice(id = index.toString(), name = element.ownText(), group = groupname )
            mutableDevices.add(newTemperatureDevice)
            index++
        }
        return mutableDevices

    }


}