package com.bike.computer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Heart-rate distribution graph: time-in-bpm histogram, smoothed, with HR-zone
 * bands and a live-HR marker.
 *
 * NOTE: jadx flagged the original onDraw() "decompiled incorrectly". Two decompiler
 * artifacts are corrected here to the original intent: the X mapping used integer
 * division (which would collapse the plot to a vertical line) and the 5-tap smoothing
 * window's in-bounds test was inverted.
 */
class HrGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private var hist: LongArray = LongArray(0)
    private var maxHr = 185
    private var liveHr = 0

    private val bandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33FFFFFF")
        style = Paint.Style.FILL
    }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = -1
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeJoin = Paint.Join.ROUND
    }
    private val axis = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3A3A3A")
        strokeWidth = 2f
    }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9E9E9E")
        textSize = 28f
    }
    private val marker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = -1
        strokeWidth = 3f
    }

    fun setData(hist: LongArray, maxHr: Int, liveHr: Int) {
        this.hist = hist
        this.maxHr = maxHr.coerceAtLeast(1)
        this.liveHr = liveHr
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val padL = 20f
        val padR = 20f
        val padT = 22f
        val padB = 46f
        val w = width.toFloat()
        val h = height.toFloat()
        val gw = w - padL - padR
        val gh = h - padT - padB

        var lo = maxHr / 2
        var hi = maxHr
        for (bpm in hist.indices) {
            if (hist[bpm] > 0) {
                if (bpm < lo) lo = bpm
                if (bpm > hi) hi = bpm
            }
        }
        if (hi - lo < 10) hi = lo + 10

        fun x(bpm: Int): Float = (bpm - lo).toFloat() / (hi - lo) * gw + padL
        fun y(v: Float, peak: Float): Float = (1f - v / peak) * gh + padT

        // HR-zone background bands.
        val zones = HrZone.entries
        for (i in zones.indices) {
            val zLo = maxHr * zones[i].lowerPct / 100
            val zHi = if (i < 4) maxHr * zones[i + 1].lowerPct / 100 else hi + 1
            val x0 = x(zLo.coerceIn(lo, hi))
            val x1 = x(zHi.coerceIn(lo, hi))
            if (x1 > x0) {
                bandPaint.color = ((zones[i].color and 0xFFFFFF) or 0x40000000).toInt()
                canvas.drawRect(x0, padT, x1, h - padB, bandPaint)
            }
        }

        canvas.drawLine(padL, h - padB, w - padR, h - padB, axis)

        val total = hist.sum()
        if (total <= 0) {
            canvas.drawText("No HR data yet", padL + 20f, h / 2f, label)
            drawAxisLabels(canvas, lo, hi, h, w, padR)
            return
        }

        // Smoothed time-in-bpm curve (5-tap moving average).
        var peak = 1f
        val sm = FloatArray(hi - lo + 1)
        for (bpm in lo..hi) {
            var s = 0L
            var n = 0
            for (k in -2..2) {
                val b = bpm + k
                if (b in hist.indices) {
                    s += hist[b]
                    n++
                }
            }
            val v = if (n > 0) (s / n).toFloat() else 0f
            sm[bpm - lo] = v
            if (v > peak) peak = v
        }

        val area = Path()
        area.moveTo(x(lo), h - padB)
        for (bpm in lo..hi) area.lineTo(x(bpm), y(sm[bpm - lo], peak))
        area.lineTo(x(hi), h - padB)
        area.close()
        canvas.drawPath(area, fill)

        val stroke = Path()
        stroke.moveTo(x(lo), y(sm[0], peak))
        for (bpm in lo..hi) stroke.lineTo(x(bpm), y(sm[bpm - lo], peak))
        canvas.drawPath(stroke, line)

        if (liveHr in lo..hi) {
            marker.color = -1
            val mx = x(liveHr)
            canvas.drawLine(mx, padT, mx, h - padB, marker)
        }
        drawAxisLabels(canvas, lo, hi, h, w, padR)
    }

    private fun drawAxisLabels(canvas: Canvas, lo: Int, hi: Int, h: Float, w: Float, padR: Float) {
        label.textAlign = Paint.Align.LEFT
        canvas.drawText(lo.toString(), 20f, h - 12f, label)
        label.textAlign = Paint.Align.CENTER
        canvas.drawText("bpm", w / 2f, h - 12f, label)
        label.textAlign = Paint.Align.RIGHT
        canvas.drawText(hi.toString(), w - padR, h - 12f, label)
        label.textAlign = Paint.Align.LEFT
    }
}
