package com.example.mapchat.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MessageDecoration(private val left: Int, private val right: Int, private val top: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = left
        outRect.right = right
        outRect.top = top
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = 15

        }

    }
}