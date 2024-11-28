package com.example.places.carappservice.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Alert
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.places.carappservice.R

class FavoriteScreen(carContext: CarContext) : Screen(carContext) {


    fun createTextAsIcon(context: Context, text: String): CarIcon {
        // Erstelle Paint-Objekt, um den Text zu rendern
        val paint = Paint().apply {
            color = Color.WHITE  // Textfarbe
            textSize = 100f       // Schriftgröße
            textAlign = Paint.Align.CENTER
        }

        val width = (paint.measureText(text)).toInt()  // +20 für Rand
        val height = 150   // Höhe des Bildes

        // Erstelle ein Bitmap, um den Text zu zeichnen
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)  // Hintergrundfarbe (weiß)
        canvas.drawText(text, width / 2f, height / 2f + 40f, paint)  // Text zeichnen

        // Konvertiere das Bitmap in ein IconCompat und dann in CarIcon
        val iconCompat = IconCompat.createWithBitmap(bitmap)
        return CarIcon.Builder(iconCompat).build()
    }

    /*
    GridItems  können nur einen Titel und Icons anzeigen. Da allerdings mindestens der Gerätename
    und der dazugehörige Wert / Status angezeigt werden muss. Wird ein Bild generiert der diesen Wert
    enhält. Dieses Bild wird in der Methode createTextAsIcon erstellt
     */

    fun getGridItems(): ItemList {
        val itemList = ItemList.Builder()
        for (i in 1..10)
        {
            val firstItem = GridItem.Builder().setTitle("Bad").setImage(
                createTextAsIcon(carContext, "21°C")
            ).setOnClickListener {
                val toast = CarToast.makeText(carContext, "Status wurde aktualisiert", CarToast.LENGTH_SHORT)
                toast.show()
            }.build()

            itemList.addItem(firstItem)
        }

        return itemList.build()
    }

    override fun onGetTemplate(): Template {
        val gridTemplate = GridTemplate.Builder().setTitle("Hello").setLoading(false)
        return gridTemplate.setSingleList(getGridItems()).build()
    }
}