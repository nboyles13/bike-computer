package com.bike.computer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/** Draws the ride elevation profile (distance on X, elevation on Y). */
class ElevationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private var samples: List<FloatArray> = emptyList()

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3D7BC0")
        style = Paint.Style.FILL
    }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8FC7FF")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val axis = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3A3A3A")
        strokeWidth = 2f
    }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9E9E9E")
        textSize = 30f
    }

    fun setData(s: List<FloatArray>) {
        samples = s
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawLine(90f, h - 50f, w - 30f, h - 50f, axis)
        canvas.drawLine(90f, 30f, 90f, h - 50f, axis)
        if (samples.size < 2) {
            canvas.drawText("No elevation yet", 30f + 90f, h / 2f, label)
            return
        }
        val maxD = samples.last()[0].coerceAtLeast(1f)
        var minE = Float.MAX_VALUE
        var maxE = -Float.MAX_VALUE
        for (s in samples) {
            minE = minOf(minE, s[1])
            maxE = maxOf(maxE, s[1])
        }
        if (maxE - minE < 5f) {
            maxE += 5f
            minE -= 5f
        }
        val gw = (w - 90f) - 30f
        val gh = (h - 30f) - 50f

        fun x(d: Float): Float = (d / maxD) * gw + 90f
        fun y(e: Float): Float = (1f - (e - minE) / (maxE - minE)) * gh + 30f

        val path = Path()
        path.moveTo(x(samples[0][0]), h - 50f)
        for (s in samples) {
            path.lineTo(x(s[0]), y(s[1]))
        }
        path.lineTo(x(samples.last()[0]), h - 50f)
        path.close()
        canvas.drawPath(path, fill)

        val stroke = Path()
        stroke.moveTo(x(samples[0][0]), y(samples[0][1]))
        for (s in samples) {
            stroke.lineTo(x(s[0]), y(s[1]))
        }
        canvas.drawPath(stroke, line)

        canvas.drawText("${Units.feet(maxE.toDouble()).toInt()} ft", 8f, 24f + 30f, label)
        canvas.drawText("${Units.feet(minE.toDouble()).toInt()} ft", 8f, h - 50f, label)
        canvas.drawText(
            String.format("%.1f mi", Units.miles(maxD.toDouble())),
            (w - 30f) - 90f,
            h - 12f,
            label,
        )
    }
}
