package com.example.places.data.parser

import android.util.Log
import com.example.places.data.model.Group
import com.example.places.data.network.HtmlClient
import com.example.places.data.repositories.GroupRepository
import com.fleeksoft.ksoup.Ksoup

class HtmlParser {

    private lateinit var html: String
    private val groupRepository = GroupRepository()
    private val  client = HtmlClient()

    suspend fun parseHtml() {
        this.html = client.getHtml().toString()
        Log.i("DATA FROM SERVER", this.html)
        val doc: com.fleeksoft.ksoup.nodes.Document = Ksoup.parse(html = html)
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
                newGroupItem = Group( index, element.children().text(),client.getIcon(queryForGroupIcon), groupRepository.getGroupDevices())
                Log.i("DATA GROUPS","Tag: ${element.children().text()}, Inhalt: ${element.ownText()}")


            }else
            {
                val queryForGroupIcon = element.attr("data-info")
                newGroupItem = Group( index, element.ownText(), client.getIcon(queryForGroupIcon), groupRepository.getGroupDevices())

            }
            groupRepository.addGroupToList(newGroupItem)
            Log.i("DATA GROUPS","Tag: ${element.tagName()}, Inhalt: ${element.ownText()}")
        }
    }


}