package com.fashionism.fashionismuserapp.tools

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GridSpacingItemDecoration(private val spacing: Int, private val includeBottom: Boolean) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = layoutParams.spanIndex

        outRect.left = if (spanIndex == 0) 0 else spacing / 2
        outRect.right = if (spanIndex == 0) spacing / 2 else 0

        if (includeBottom) {
            outRect.bottom = spacing
        }
    }
}