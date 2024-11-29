package com.example.places.carappservice.screen

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Tab
import androidx.car.app.model.TabContents
import androidx.car.app.model.TabTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.places.carappservice.R
import com.example.places.carappservice.components.TabInfo

/*
    Generates the user Inferfaces
    User Interface is representent by Template Classes
    Each Session manage a Stack of Screen instances
 */
class MainScreen(carContext: CarContext) : Screen(carContext) {

    private val firstTab = TabInfo("first_tab", R.string.first_tab, R.drawable.home_tab)
    private val secondTab = TabInfo("second_tab", R.string.second_tab, R.drawable.favorite_foreground)
    private val favoriteScreen = FavoriteScreen(carContext)
    private var homeScreen = HomeScreen(carContext)


    private var activeContentId: String = firstTab.tabId

    private fun getFirstTabTemplate() : Template {
        return homeScreen.onGetTemplate()

    }


    private fun getFavoriteTabTemplate() : Template {
        return favoriteScreen.onGetTemplate()
    }

    @OptIn(ExperimentalCarApi::class)
    private fun getActiveTabContent(): TabContents {
        return if (activeContentId == firstTab.tabId) {
            TabContents.Builder(getFirstTabTemplate()).build()
        } else {
            TabContents.Builder(getFavoriteTabTemplate()).build()
        }
    }

    @OptIn(ExperimentalCarApi::class)
    private fun getTab(tabInfo: TabInfo) = Tab.Builder()
        .setTitle(carContext.getString(tabInfo.tabTitle))
        .setIcon(
            CarIcon.Builder(IconCompat.createWithResource(carContext, tabInfo.tabIcon)).build()
        ).setContentId(tabInfo.tabId).build()

    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): Template {
        val tabTemplate = TabTemplate.Builder(object : TabTemplate.TabCallback {
            override fun onTabSelected(tabContentId: String) {
                activeContentId = tabContentId
                invalidate() //call invalidate() to get the new template to display
            }
        })
            .setHeaderAction(Action.APP_ICON)
        tabTemplate.addTab(getTab(firstTab))
        tabTemplate.addTab(getTab(secondTab))
        tabTemplate.setTabContents(getActiveTabContent())

        return tabTemplate.setActiveTabContentId(activeContentId).build()
    }
}
