package com.lib.cache_cleaner.ui.adapter

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class DividerItemDecoratorWithFooter(private val dividerDrawable: Drawable?, private val lastItemMargin: Int) :
    RecyclerView.ItemDecoration() {

    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {

            // left margin for the divider
            val dividerLeft = 32

            // right margin for the divider with reference to the parent width
            val dividerRight: Int = parent.width - 32

            for (i in 0 until parent.childCount) {

                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                // calculating the distance of the divider to be drawn from the top
                val dividerTop: Int = child.bottom + params.bottomMargin
                val dividerBottom: Int = dividerTop + (dividerDrawable?.intrinsicHeight ?: 0)
                if (i != parent.childCount - 1) {
                    dividerDrawable?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    dividerDrawable?.draw(canvas)
                }
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position == state.itemCount - 1)
            outRect.bottom = lastItemMargin.dp
    }
}
