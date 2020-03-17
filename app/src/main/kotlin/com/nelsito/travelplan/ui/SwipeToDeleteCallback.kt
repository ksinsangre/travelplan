package com.nelsito.travelplan.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nelsito.travelplan.trips.domain.Trip
import com.nelsito.travelplan.trips.view.TripsListAdapter

class SwipeToDeleteCallback(
    private val icon: Drawable?,
    private val adapter: TripsListAdapter,
    private val onDeleteListener: OnDeleteListener
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val background: ColorDrawable = ColorDrawable(Color.parseColor("#00000000"))
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset =
            20 //so background is behind the rounded corners of itemView
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        if (dX > 0) { // Swiping to the right
            val iconLeft = itemView.left + iconMargin - icon.intrinsicWidth
            val iconRight = itemView.left + iconMargin
            Log.d("icon", "iconMargin: $iconMargin iconLeft: $iconLeft iconRight: $iconRight")
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
            )
        } else if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            Log.d("icon", "iconMargin: $iconMargin iconLeft: $iconLeft iconRight: $iconRight")
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top, itemView.right, itemView.bottom
            )
        } else { // view is unSwiped
            Log.d("icon", "view is unSwiped")
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
        icon.draw(c)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val item = adapter.currentList[position]
        adapter.deleteItem(position)
        onDeleteListener.onDeleteTrip(item.trip)
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if(viewHolder is TripsListAdapter.TripViewHolder) {
            return 0
        }
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    interface OnDeleteListener {
        fun onDeleteTrip(trip: Trip)
    }

}