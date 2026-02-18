package dam.pmdm.spyrothedragon.ui

import android.content.Context
import android.graphics.*
import android.view.View



class SpotlightView(context: Context) : View(context) {

    var centerX = 0f
    var centerY = 0f
    var radius = 0f

    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }
}
