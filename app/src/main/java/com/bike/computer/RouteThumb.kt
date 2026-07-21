package com.bike.computer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import kotlin.math.cos

/** Small preview view that draws a route's polyline with start/end dots. */
class RouteThumb(context: Context) : View(context) {
    private var pts: List<DoubleArray> = emptyList()
    private val path = Path()

    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = -37632
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val startDot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = -13577896 }
    private val endDot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = -47814 }

    fun setPoints(p: List<DoubleArray>) {
        pts = p
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val n = pts.size
        if (n < 2 || width == 0 || height == 0) return

        var minLon = Double.MAX_VALUE
        var maxLon = -Double.MAX_VALUE
        var minLat = Double.MAX_VALUE
        var maxLat = -Double.MAX_VALUE
        for (p in pts) {
            if (p[0] < minLon) minLon = p[0]
            if (p[0] > maxLon) maxLon = p[0]
            if (p[1] < minLat) minLat = p[1]
            if (p[1] > maxLat) maxLat = p[1]
        }
        val latMid = Math.toRadians((minLat + maxLat) / 2)
        val cosLat = cos(latMid)
        val spanX = ((maxLon - minLon) * cosLat).coerceAtLeast(1.0e-9)
        val spanY = (maxLat - minLat).coerceAtLeast(1.0e-9)
        val w = width - 2 * 24f
        val h = height - 2 * 24f
        val scale = minOf(w / spanX, h / spanY)
        val offX = 24f + (w - spanX * scale) / 2
        val offY = 24f + (h - spanY * scale) / 2

        fun px(p: DoubleArray): Float = (((p[0] - minLon) * cosLat * scale) + offX).toFloat()
        fun py(p: DoubleArray): Float = (((maxLat - p[1]) * scale) + offY).toFloat()

        path.reset()
        path.moveTo(px(pts[0]), py(pts[0]))
        for (i in 1 until n) {
            path.lineTo(px(pts[i]), py(pts[i]))
        }
        canvas.drawPath(path, line)
        canvas.drawCircle(px(pts[0]), py(pts[0]), 7f, startDot)
        canvas.drawCircle(px(pts[n - 1]), py(pts[n - 1]), 7f, endDot)
    }
}
