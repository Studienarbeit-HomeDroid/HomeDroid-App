package com.homedroidv2.carappservice

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat

class BitMapGenerator{

    private val iconHeight: Int = 150
    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }

    /**
     * GridItems können lediglich einen Titel und ein Icon darstellen.
     * Da jedoch mindestens der Gerätename sowie der zugehörige Wert bzw. Status angezeigt werden müssen,
     * wird ein Bild erzeugt, das diesen Text enthält.
     * Dieses Bild wird in der Methode `createTextAsIcon` generiert.
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