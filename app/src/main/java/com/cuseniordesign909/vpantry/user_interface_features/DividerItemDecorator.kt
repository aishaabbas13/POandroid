package com.cuseniordesign909.vpantry.user_interface_features

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class DividerItemDecorator(private val dividerDrawable : Drawable) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var left = parent.paddingLeft
        var right = parent.width - parent.paddingRight
        var childCount = parent.childCount
        for (i in 0..childCount) {
            var child = parent.getChildAt(i)
            if(child != null) {
                var params = child?.layoutParams as RecyclerView.LayoutParams?
                var top = child?.bottom + params?.bottomMargin as Int
                var bottom = top + dividerDrawable.intrinsicHeight
                dividerDrawable.setBounds(left, top, right, bottom)
                if ((parent.getChildAdapterPosition(child) == parent.adapter?.itemCount as Int - 1) && parent.bottom < bottom) { // this prevent a parent to hide the last item's divider
                    parent.setPadding(
                        parent.paddingLeft,
                        parent.paddingTop,
                        parent.paddingRight,
                        bottom - parent.bottom
                    )
                }
            }
            dividerDrawable.draw(c)
        }
    }
}