package com.huawen.baselibrary.adapter

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.huawen.baselibrary.R
import com.huawen.baselibrary.adapter.callback.ItemDragAndSwipeCallback
import com.huawen.baselibrary.adapter.listener.OnItemDragListener
import com.huawen.baselibrary.adapter.listener.OnItemSwipeListener
import java.util.*

/**
 * Created by luoxw on 2016/7/13.
 */
abstract class BaseItemDraggableAdapter<T, K : BaseViewHolder> : BaseQuickAdapter<T, K> {
    protected var mToggleViewId = NO_TOGGLE_VIEW
    protected var mItemTouchHelper: ItemTouchHelper? = null
    var isItemDraggable = false
        protected set
    var isItemSwipeEnable = false
        protected set
    protected var mOnItemDragListener: OnItemDragListener? = null
    protected var mOnItemSwipeListener: OnItemSwipeListener? = null
    protected var mDragOnLongPress = true

    protected var mOnToggleViewTouchListener: View.OnTouchListener? = null
    protected var mOnToggleViewLongClickListener: View.OnLongClickListener? = null


    constructor(data: MutableList<T>) : super(data) {}

    constructor(layoutResId: Int, data: MutableList<T>) : super(layoutResId, data) {}


    /**
     * To bind different types of holder and solve different the bind events
     *
     * @param holder
     * @param position
     * @see .getDefItemViewType
     */
    override fun onBindViewHolder(holder: K, position: Int) {
        super.onBindViewHolder(holder, position)
        val viewType = holder.itemViewType

        if (mItemTouchHelper != null && isItemDraggable && viewType != BaseQuickAdapter.LOADING_VIEW && viewType != BaseQuickAdapter.HEADER_VIEW
            && viewType != BaseQuickAdapter.EMPTY_VIEW && viewType != BaseQuickAdapter.FOOTER_VIEW
        ) {
            if (mToggleViewId != NO_TOGGLE_VIEW) {
                val toggleView = holder.getView<View>(mToggleViewId)
                if (toggleView != null) {
                    toggleView.setTag(R.id.BaseQuickAdapter_viewholder_support, holder)
                    if (mDragOnLongPress) {
                        toggleView.setOnLongClickListener(mOnToggleViewLongClickListener)
                    } else {
                        toggleView.setOnTouchListener(mOnToggleViewTouchListener)
                    }
                }
            } else {
                holder.itemView.setTag(R.id.BaseQuickAdapter_viewholder_support, holder)
                holder.itemView.setOnLongClickListener(mOnToggleViewLongClickListener)
            }
        }
    }


    /**
     * Set the toggle view's id which will trigger drag event.
     * If the toggle view id is not set, drag event will be triggered when the item is long pressed.
     *
     * @param toggleViewId the toggle view's id
     */
    fun setToggleViewId(toggleViewId: Int) {
        mToggleViewId = toggleViewId
    }

    /**
     * Set the drag event should be trigger on long press.
     * Work when the toggleViewId has been set.
     *
     * @param longPress by default is true.
     */
    fun setToggleDragOnLongPress(longPress: Boolean) {
        mDragOnLongPress = longPress
        if (mDragOnLongPress) {
            mOnToggleViewTouchListener = null
            mOnToggleViewLongClickListener = View.OnLongClickListener { v ->
                if (mItemTouchHelper != null && isItemDraggable) {
                    mItemTouchHelper!!.startDrag(v.getTag(R.id.BaseQuickAdapter_viewholder_support) as RecyclerView.ViewHolder)
                }
                true
            }
        } else {
            mOnToggleViewTouchListener = View.OnTouchListener { v, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && !mDragOnLongPress) {
                    if (mItemTouchHelper != null && isItemDraggable) {
                        mItemTouchHelper!!.startDrag(v.getTag(R.id.BaseQuickAdapter_viewholder_support) as RecyclerView.ViewHolder)
                    }
                    true
                } else {
                    false
                }
            }
            mOnToggleViewLongClickListener = null
        }
    }

    /**
     * Enable drag items. Use the specified view as toggle.
     *
     * @param itemTouchHelper [ItemTouchHelper]
     * @param toggleViewId    The toggle view's id.
     * @param dragOnLongPress If true the drag event will be trigger on long press, otherwise on touch down.
     */
    @JvmOverloads
    fun enableDragItem(
        itemTouchHelper: ItemTouchHelper,
        toggleViewId: Int = NO_TOGGLE_VIEW,
        dragOnLongPress: Boolean = true
    ) {
        isItemDraggable = true
        mItemTouchHelper = itemTouchHelper
        setToggleViewId(toggleViewId)
        setToggleDragOnLongPress(dragOnLongPress)
    }

    /**
     * Disable drag items.
     */
    fun disableDragItem() {
        isItemDraggable = false
        mItemTouchHelper = null
    }

    /**
     *
     * Enable swipe items.
     * You should attach [ItemTouchHelper] which construct with [ItemDragAndSwipeCallback] to the Recycler when you enable this.
     */
    fun enableSwipeItem() {
        isItemSwipeEnable = true
    }

    fun disableSwipeItem() {
        isItemSwipeEnable = false
    }

    /**
     * @param onItemDragListener Register a callback to be invoked when drag event happen.
     */
    fun setOnItemDragListener(onItemDragListener: OnItemDragListener) {
        mOnItemDragListener = onItemDragListener
    }

    fun getViewHolderPosition(viewHolder: RecyclerView.ViewHolder): Int {
        return viewHolder.adapterPosition - headerLayoutCount
    }

    fun onItemDragStart(viewHolder: RecyclerView.ViewHolder) {
        if (mOnItemDragListener != null && isItemDraggable) {
            mOnItemDragListener!!.onItemDragStart(viewHolder, getViewHolderPosition(viewHolder))
        }
    }

    fun onItemDragMoving(source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val from = getViewHolderPosition(source)
        val to = getViewHolderPosition(target)

        if (inRange(from) && inRange(to)) {
            if (from < to) {
                for (i in from until to) {
                    Collections.swap(mData!!, i, i + 1)
                }
            } else {
                for (i in from downTo to + 1) {
                    Collections.swap(mData!!, i, i - 1)
                }
            }
            notifyItemMoved(source.adapterPosition, target.adapterPosition)
        }

        if (mOnItemDragListener != null && isItemDraggable) {
            mOnItemDragListener!!.onItemDragMoving(source, from, target, to)
        }
    }

    fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder) {
        if (mOnItemDragListener != null && isItemDraggable) {
            mOnItemDragListener!!.onItemDragEnd(viewHolder, getViewHolderPosition(viewHolder))
        }
    }

    fun setOnItemSwipeListener(listener: OnItemSwipeListener) {
        mOnItemSwipeListener = listener
    }

    fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder) {
        if (mOnItemSwipeListener != null && isItemSwipeEnable) {
            mOnItemSwipeListener!!.onItemSwipeStart(viewHolder, getViewHolderPosition(viewHolder))
        }
    }

    fun onItemSwipeClear(viewHolder: RecyclerView.ViewHolder) {
        if (mOnItemSwipeListener != null && isItemSwipeEnable) {
            mOnItemSwipeListener!!.clearView(viewHolder, getViewHolderPosition(viewHolder))
        }
    }

    fun onItemSwiped(viewHolder: RecyclerView.ViewHolder) {
        val pos = getViewHolderPosition(viewHolder)
        if (inRange(pos)) {
            mData!!.removeAt(pos)
            notifyItemRemoved(viewHolder.adapterPosition)
        }


        if (mOnItemSwipeListener != null && isItemSwipeEnable) {
            mOnItemSwipeListener!!.onItemSwiped(viewHolder, getViewHolderPosition(viewHolder))
        }
    }

    fun onItemSwiping(
        canvas: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        isCurrentlyActive: Boolean
    ) {
        if (mOnItemSwipeListener != null && isItemSwipeEnable) {
            mOnItemSwipeListener!!.onItemSwipeMoving(canvas, viewHolder, dX, dY, isCurrentlyActive)
        }
    }

    private fun inRange(position: Int): Boolean {
        return position >= 0 && position < mData!!.size
    }

    companion object {

        private val NO_TOGGLE_VIEW = 0

        private val ERROR_NOT_SAME_ITEMTOUCHHELPER = "Item drag and item swipe should pass the same ItemTouchHelper"
    }
}
/**
 * Enable drag items.
 * Use itemView as the toggleView when long pressed.
 *
 * @param itemTouchHelper [ItemTouchHelper]
 */
