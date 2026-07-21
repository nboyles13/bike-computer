package com.bike.computer

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.roundToInt

/** A 4-column dashboard of metric tiles with drag-to-move / drag-to-resize editing. */
class DashboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {

    companion object {
        const val COLS = 4
    }

    private val medium: Typeface = Typeface.create("sans-serif-medium", 0)
    private var tileList: MutableList<DashTile> = ArrayList()
    private val holders = HashMap<DashTile, Holder>()

    private var editingState = false
    val editing: Boolean get() = editingState
    private var editInsetTop = 0

    var valueProvider: ((Metric) -> String)? = null
    var onChanged: (() -> Unit)? = null
    var onPickMetric: ((DashTile) -> Unit)? = null
    var onEditModeChanged: ((Boolean) -> Unit)? = null

    // Drag state.
    private var dragTile: DashTile? = null
    private var draggingCard: FrameLayout? = null
    private var origL = 0
    private var origT = 0
    private var origR = 0
    private var origB = 0
    private var dragColW = 1f
    private var dragRowH = 1f
    private var dragGridRows = 1
    private var downX = 0f
    private var downY = 0f
    private var moved = false
    private var mode = 0

    private class Holder(
        val card: FrameLayout,
        val icon: ImageView,
        val label: TextView,
        val value: TextView,
        val remove: TextView,
        val handle: View,
    )

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun gridRows(): Int = (tileList.maxOfOrNull { it.row + it.h } ?: 1).coerceAtLeast(1)

    fun setTiles(list: List<DashTile>) {
        tileList = list.map { it.copy(it.metric, it.col, it.row, it.w, it.h) }.toMutableList()
        pack()
        rebuild()
    }

    fun tiles(): List<DashTile> = tileList.map { it.copy(it.metric, it.col, it.row, it.w, it.h) }

    fun setEditing(on: Boolean) {
        editingState = on
        editInsetTop = if (on) dp(52) else 0
        for (h in holders.values) {
            h.remove.visibility = if (on) VISIBLE else GONE
            h.handle.visibility = if (on) VISIBLE else GONE
            h.card.alpha = 1f
        }
        requestLayout()
        onEditModeChanged?.invoke(on)
    }

    fun refresh() {
        val fn = valueProvider ?: return
        for ((t, h) in holders) {
            h.value.text = fn(t.metric)
        }
    }

    fun addTile(metric: Metric) {
        tileList.add(DashTile(metric, 0, gridRows(), 2, 1))
        pack()
        rebuild()
        onChanged?.invoke()
    }

    fun applyMetric(tile: DashTile, m: Metric) {
        tile.metric = m
        rebuild()
        onChanged?.invoke()
    }

    private fun pack(priority: DashTile? = null) {
        val occ = HashSet<Int>()
        if (priority != null) {
            priority.w = priority.w.coerceIn(1, 4)
            priority.h = priority.h.coerceAtLeast(1)
            priority.col = priority.col.coerceIn(0, 4 - priority.w)
            priority.row = priority.row.coerceAtLeast(0)
            reserve(occ, priority.col, priority.row, priority.w, priority.h)
        }
        for (t in tileList.sortedWith(compareBy({ it.row }, { it.col }))) {
            if (t === priority) continue
            val w = t.w.coerceIn(1, 4)
            val h = t.h.coerceAtLeast(1)
            t.w = w
            t.h = h
            var r = 0
            var col = 0
            var placed = false
            while (!placed) {
                var cc = 0
                while (cc <= 4 - w) {
                    if (fits(occ, cc, r, w, h)) {
                        col = cc
                        placed = true
                        break
                    }
                    cc++
                }
                if (!placed) r++
            }
            t.col = col
            t.row = r
            reserve(occ, col, r, w, h)
        }
    }

    private fun reserve(occ: HashSet<Int>, c: Int, r: Int, w: Int, h: Int) {
        for (dr in 0 until h) {
            for (dc in 0 until w) {
                occ.add((r + dr) * 4 + c + dc)
            }
        }
    }

    private fun fits(occ: HashSet<Int>, c: Int, r: Int, w: Int, h: Int): Boolean {
        if (c + w > 4) return false
        for (dr in 0 until h) {
            for (dc in 0 until w) {
                if (occ.contains((r + dr) * 4 + c + dc)) return false
            }
        }
        return true
    }

    private fun metricIcon(m: Metric): Pair<Int, Int> = when (m) {
        Metric.SPEED, Metric.AVG_SPEED, Metric.MAX_SPEED ->
            R.drawable.ic_speed to Color.parseColor("#FF4C8DFF")
        Metric.HR, Metric.AVG_HR, Metric.MAX_HR ->
            R.drawable.ic_heart to Color.parseColor("#FFFF453A")
        Metric.DISTANCE -> R.drawable.ic_distance to Color.parseColor("#FF30D158")
        Metric.RIDE_TIME -> R.drawable.ic_time to Color.parseColor("#FFFF9F0A")
        Metric.ELEVATION, Metric.ASCENT ->
            R.drawable.ic_elevation to Color.parseColor("#FFBF5AF2")
        Metric.GRADE -> R.drawable.ic_incline to Color.parseColor("#FF64D2FF")
        Metric.CADENCE -> R.drawable.ic_cadence to Color.parseColor("#FF5AC8FA")
        Metric.POWER, Metric.AVG_POWER, Metric.MAX_POWER ->
            R.drawable.ic_power to Color.parseColor("#FFFFD60A")
        Metric.CLOCK -> R.drawable.ic_time to Color.parseColor("#FFB8B8BD")
        Metric.BATTERY -> R.drawable.ic_battery to Color.parseColor("#FF30D158")
    }

    private fun rebuild() {
        removeAllViews()
        holders.clear()
        for (t in tileList) {
            val card = FrameLayout(context)
            card.setBackgroundResource(R.drawable.card_solid)
            val body = LinearLayout(context)
            body.orientation = LinearLayout.VERTICAL
            body.gravity = 1
            body.setPadding(dp(4), dp(4), dp(4), dp(5))
            body.layoutParams = FrameLayout.LayoutParams(-1, -1)
            val (iconRes, accent) = metricIcon(t.metric)

            val row = LinearLayout(context)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = 17
            row.layoutParams = LinearLayout.LayoutParams(-1, -2)
            val icon = ImageView(context)
            icon.setImageResource(iconRes)
            icon.setColorFilter(accent)
            val iconLp = LinearLayout.LayoutParams(dp(15), dp(15))
            iconLp.rightMargin = dp(5)
            icon.layoutParams = iconLp
            val label = TextView(context)
            label.text = t.metric.label
            label.setTextColor(Color.parseColor("#FF9E9E9E"))
            label.textSize = 13f
            label.maxLines = 1
            label.includeFontPadding = false
            row.addView(icon)
            row.addView(label)

            val value = TextView(context)
            value.text = "--"
            value.setTextColor(-1)
            value.typeface = medium
            value.includeFontPadding = false
            value.maxLines = 1
            value.gravity = 17
            value.setAutoSizeTextTypeUniformWithConfiguration(24, 160, 1, 2)
            value.layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
            body.addView(row)
            body.addView(value)
            card.addView(body)

            val remove = TextView(context)
            remove.text = "✕"
            remove.setTextColor(-1)
            remove.textSize = 13f
            remove.gravity = 17
            remove.setBackgroundColor(Color.parseColor("#CCE53935"))
            remove.layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), 8388661)
            remove.visibility = GONE
            remove.setOnClickListener {
                tileList.remove(t)
                pack()
                rebuild()
                onChanged?.invoke()
            }
            val handle = View(context)
            handle.setBackgroundResource(R.drawable.resize_handle)
            handle.layoutParams = FrameLayout.LayoutParams(dp(26), dp(26), 8388693)
            handle.visibility = GONE
            card.addView(remove)
            card.addView(handle)
            addView(card)
            holders[t] = Holder(card, icon, label, value, remove, handle)
        }
        setEditing(editingState)
        refresh()
        requestLayout()
    }

    override fun onMeasure(w: Int, h: Int) {
        setMeasuredDimension(MeasureSpec.getSize(w), MeasureSpec.getSize(h))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val colW = width / 4f
        val rowH = (height - editInsetTop) / gridRows().toFloat()
        for (tile in tileList) {
            if (holders[tile]?.card !== draggingCard) layoutTile(tile, colW, rowH)
        }
    }

    private fun layoutTile(tile: DashTile, colW: Float, rowH: Float) {
        val h = holders[tile] ?: return
        val m = dp(2)
        val left = (tile.col * colW).toInt() + m
        val top = editInsetTop + (tile.row * rowH).toInt() + m
        val right = ((tile.col * colW) + (tile.w * colW)).toInt() - m
        val bottom = (editInsetTop + ((tile.row * rowH) + (tile.h * rowH)).toInt()) - m
        h.card.measure(
            MeasureSpec.makeMeasureSpec((right - left).coerceAtLeast(0), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((bottom - top).coerceAtLeast(0), MeasureSpec.EXACTLY),
        )
        h.card.layout(left, top, right, bottom)
    }

    private fun reflowExceptDragged() {
        for (tile in tileList) {
            if (holders[tile]?.card !== draggingCard) layoutTile(tile, dragColW, dragRowH)
        }
        invalidate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!editingState || ev.actionMasked != MotionEvent.ACTION_DOWN) return false
        val tile = tileAt(ev.x, ev.y) ?: return false
        val h = holders[tile]!!
        if (ev.x > h.card.right - dp(34) && ev.y < h.card.top + dp(34)) return false
        dragTile = tile
        draggingCard = h.card
        origL = h.card.left
        origT = h.card.top
        origR = h.card.right
        origB = h.card.bottom
        dragColW = width / 4f
        dragGridRows = gridRows()
        dragRowH = (height - editInsetTop) / dragGridRows.toFloat()
        downX = ev.x
        downY = ev.y
        moved = false
        mode = if (ev.x <= (origR - dp(30)) || ev.y <= (origB - dp(30))) 1 else 2
        parent?.requestDisallowInterceptTouchEvent(true)
        return true
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val card = draggingCard
        if (editingState && card != null) {
            val tile = dragTile ?: return false
            when (ev.actionMasked) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (moved) {
                        if (mode == 1) {
                            tile.col = (card.left / dragColW).roundToInt().coerceIn(0, 4 - tile.w)
                            tile.row = ((card.top - editInsetTop) / dragRowH).roundToInt().coerceAtLeast(0)
                        } else {
                            tile.w = ((card.right - card.left) / dragColW).roundToInt().coerceIn(1, 4 - tile.col)
                            tile.h = ((card.bottom - card.top) / dragRowH).roundToInt().coerceAtLeast(1)
                        }
                        pack(tile)
                        onChanged?.invoke()
                    } else {
                        onPickMetric?.invoke(tile)
                    }
                    draggingCard = null
                    dragTile = null
                    parent?.requestDisallowInterceptTouchEvent(false)
                    if (moved) rebuild() else requestLayout()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = ev.x - downX
                    val dy = ev.y - downY
                    if (abs(dx) + abs(dy) > dp(8)) moved = true
                    if (mode == 1) {
                        card.layout(
                            (origL + dx).toInt(), (origT + dy).toInt(),
                            (origR + dx).toInt(), (origB + dy).toInt(),
                        )
                        val hc = (card.left / dragColW).roundToInt().coerceIn(0, 4 - tile.w)
                        val hr = ((card.top - editInsetTop) / dragRowH).roundToInt()
                            .coerceIn(0, (dragGridRows - tile.h).coerceAtLeast(0))
                        if (hc != tile.col || hr != tile.row) {
                            tile.col = hc
                            tile.row = hr
                            pack(tile)
                            reflowExceptDragged()
                        }
                    } else {
                        card.layout(
                            origL, origT,
                            (origR + dx).toInt().coerceAtLeast(origL + dp(48)),
                            (origB + dy).toInt().coerceAtLeast(origT + dp(48)),
                        )
                        val nw = ((card.right - card.left) / dragColW).roundToInt().coerceIn(1, 4 - tile.col)
                        val nh = ((card.bottom - card.top) / dragRowH).roundToInt().coerceAtLeast(1)
                        if (nw != tile.w || nh != tile.h) {
                            tile.w = nw
                            tile.h = nh
                            pack(tile)
                            reflowExceptDragged()
                        }
                    }
                    return true
                }
                else -> return true
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun tileAt(x: Float, y: Float): DashTile? {
        for (tile in tileList) {
            val h = holders[tile] ?: continue
            if (x >= h.card.left && x <= h.card.right && y >= h.card.top && y <= h.card.bottom) {
                return tile
            }
        }
        return null
    }
}
