package com.example.places.carappservice

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.car.app.CarContext
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat

class BitMapGenerator(_carContext: Context) {
    val carContext: Context = _carContext
    val iconHeight: Int = 150
    val paint = Paint().apply {
        color = Color.WHITE
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }

    /*
    GridItems  können nur einen Titel und Icons anzeigen. Da allerdings mindestens der Gerätename
    und der dazugehörige Wert / Status angezeigt werden muss. Wird ein Bild generiert der diesen Wert
    enhält. Dieses Bild wird in der Methode createTextAsIcon erstellt
     */
    fun createTextAsIcon(text: String): CarIcon {
        val width = (paint.measureText(text)).toInt()
        val bitmap = Bitmap.createBitmap(width, iconHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawText(text, width / 2f, iconHeight / 2f + 40f, paint)
        val iconCompat = IconCompat.createWithBitmap(bitmap)
        return CarIcon.Builder(iconCompat).build()
    }

}