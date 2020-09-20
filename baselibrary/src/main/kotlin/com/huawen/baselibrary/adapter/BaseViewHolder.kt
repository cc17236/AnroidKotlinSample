/**
 * Copyright 2013 Joan Zapata
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawen.baselibrary.adapter

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.util.Linkify
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
open class BaseViewHolder(
    /**
     * use itemView instead
     */
    @Deprecated("")
    /**
     * use itemView instead
     *
     * @return the ViewHolder root view
     */
    @get:Deprecated("")
    var convertView: View
) : RecyclerView.ViewHolder(convertView) {

    /**
     * Views indexed with their IDs
     */
    private val views: SparseArray<View>

    internal val nestViews: HashSet<Int>?

    internal val childClickViewIds: LinkedHashSet<Int>?

    internal val itemChildLongClickViewIds: LinkedHashSet<Int>?
    private var adapter: BaseQuickAdapter<*, *>? = null

    /**
     * Package private field to retain the associated user object and detect a change
     */
    /**
     * Retrieves the last converted object on this view.
     */
    /**
     * Should be called during convert
     */
    var associatedObject: Any? = null

    private val clickPosition: Int
        get() = if (layoutPosition >= adapter!!.headerLayoutCount) {
            layoutPosition - adapter!!.headerLayoutCount
        } else 0

    fun getNestViews(): Set<Int> {
        return nestViews ?: hashSetOf()
    }


    init {
        this.views = SparseArray()
        this.childClickViewIds = LinkedHashSet()
        this.itemChildLongClickViewIds = LinkedHashSet()
        this.nestViews = HashSet()


    }

    fun getItemChildLongClickViewIds(): HashSet<Int> {
        return itemChildLongClickViewIds ?: hashSetOf()
    }

    fun getChildClickViewIds(): HashSet<Int> {
        return childClickViewIds ?: hashSetOf()
    }

    /**
     * Will set the text of a TextView.
     *
     * @param viewId The view id.
     * @param value  The text to put in the text view.
     * @return The BaseViewHolder for chaining.
     */
    fun setText(@IdRes viewId: Int, value: CharSequence?): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view?.text = value
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view!!.setText(strId)
        return this
    }

    /**
     * Will set the image of an ImageView from a resource id.
     *
     * @param viewId     The view id.
     * @param imageResId The image resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseViewHolder {
        val view = getView<ImageView>(viewId)
        view!!.setImageResource(imageResId)
        return this
    }

    /**
     * Will set background color of a view.
     *
     * @param viewId The view id.
     * @param color  A color, not a resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.setBackgroundColor(color)
        return this
    }

    /**
     * Will set background of a view.
     *
     * @param viewId        The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The BaseViewHolder for chaining.
     */
    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * Will set text color of a TextView.
     *
     * @param viewId    The view id.
     * @param textColor The text color (not a resource id).
     * @return The BaseViewHolder for chaining.
     */
    fun setTextColor(@IdRes viewId: Int, @ColorInt textColor: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view?.setTextColor(textColor)
        return this
    }

    fun setTextColorList(@IdRes viewId: Int, @ColorRes textColor: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view?.setTextColor(ContextCompat.getColorStateList(view.context, textColor))
        return this
    }

    /**
     * Will set the image of an ImageView from a drawable.
     *
     * @param viewId   The view id.
     * @param drawable The image drawable.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable): BaseViewHolder {
        val view = getView<ImageView>(viewId)
        view?.setImageDrawable(drawable)
        return this
    }

    /**
     * Add an action to set the image of an image view. Can be called multiple times.
     */
    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap): BaseViewHolder {
        val view = getView<ImageView>(viewId)
        view!!.setImageBitmap(bitmap)
        return this
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times.
     * Alpha between 0-1.
     */
    fun setAlpha(@IdRes viewId: Int, value: Float): BaseViewHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId)!!.alpha = value
        } else {
            // Pre-honeycomb hack to set Alpha value
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId)!!.startAnimation(alpha)
        }
        return this
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseViewHolder for chaining.
     */
    fun setGone(@IdRes viewId: Int, visible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun isVisible(@IdRes viewId: Int): Boolean {
        val view = getView<View>(viewId)
        return view!!.visibility == View.VISIBLE
    }

    /**
     * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for INVISIBLE.
     * @return The BaseViewHolder for chaining.
     */
    fun setVisible(@IdRes viewId: Int, visible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }

    /**
     * Add links into a TextView.
     *
     * @param viewId The id of the TextView to linkify.
     * @return The BaseViewHolder for chaining.
     */
    fun linkify(@IdRes viewId: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        Linkify.addLinks(view!!, Linkify.ALL)
        return this
    }

    /**
     * Apply the typeface to the given viewId, and enable subpixel rendering.
     */
    fun setTypeface(@IdRes viewId: Int, typeface: Typeface): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view!!.typeface = typeface
        view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        return this
    }

    /**
     * Apply the typeface to all the given viewIds, and enable subpixel rendering.
     */
    fun setTypeface(typeface: Typeface, vararg viewIds: Int): BaseViewHolder {
        for (viewId in viewIds) {
            val view = getView<TextView>(viewId)
            view!!.typeface = typeface
            view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    /**
     * Sets the progress of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @return The BaseViewHolder for chaining.
     */
    fun setProgress(@IdRes viewId: Int, progress: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view!!.progress = progress
        return this
    }

    /**
     * Sets the progress and max of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @param max      The max value of a ProgressBar.
     * @return The BaseViewHolder for chaining.
     */
    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view!!.max = max
        view.progress = progress
        return this
    }

    /**
     * Sets the range of a ProgressBar to 0...max.
     *
     * @param viewId The view id.
     * @param max    The max value of a ProgressBar.
     * @return The BaseViewHolder for chaining.
     */
    fun setMax(@IdRes viewId: Int, max: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view!!.max = max
        return this
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @return The BaseViewHolder for chaining.
     */
    fun setRating(@IdRes viewId: Int, rating: Float): BaseViewHolder {
        val view = getView<RatingBar>(viewId)
        view!!.rating = rating
        return this
    }

    /**
     * Sets the rating (the number of stars filled) and max of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @param max    The range of the RatingBar to 0...max.
     * @return The BaseViewHolder for chaining.
     */
    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): BaseViewHolder {
        val view = getView<RatingBar>(viewId)
        view!!.max = max
        view.rating = rating
        return this
    }

    /**
     * Sets the on click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on click listener;
     * @return The BaseViewHolder for chaining.
     */
    @Deprecated("")
    fun setOnClickListener(@IdRes viewId: Int, listener: View.OnClickListener): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.setOnClickListener(listener)
        return this
    }

    /**
     * add childView id
     *
     * @param viewId add the child view id   can support childview click
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildClickListener(listener))}
     *
     *
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
     */
    fun addOnClickListener(@IdRes viewId: Int): BaseViewHolder {
        childClickViewIds?.add(viewId)
        val view = getView<View>(viewId)
        if (view != null) {
            if (!view.isClickable) {
                view.isClickable = true
            }
            view.setOnClickListener { v ->
                if (adapter?.onItemChildClickListener != null) {
                    adapter!!.onItemChildClickListener!!.onItemChildClick(adapter!!, v, clickPosition)
                }
            }
        }

        return this
    }

    fun addOnTargetClickListener(
        @IdRes targetId: Int, @IdRes vararg ids: Int, clickInvoke: (() -> Unit)? = null,
        childVisibleForEffect: Boolean = false
    ): BaseViewHolder {
        childClickViewIds?.add(targetId)
        val targetView = getView<View>(targetId)

        val views = arrayOfNulls<View>(ids.size)

        ids.forEachIndexed { index, id ->
            childClickViewIds?.add(id)
            val view = getView<View>(id)
            views[index] = view
        }

        val clickAgent = booleanArrayOf(false, false)
        if (targetView != null) {
            if (!targetView.isClickable) {
                targetView.isClickable = true
            }
            targetView.setOnTouchListener(View.OnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (clickAgent[0]) return@OnTouchListener false
                        clickAgent[0] = true
                        clickAgent[1] = true
                        for (view in views) {
                            if (view != null) {
                                view.requestFocus()
                                view.performClick()
                                view.isPressed = true
                            }
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (clickAgent[0]) {
                            for (view in views) {
                                if (view != null) {
                                    view.requestFocus()
                                    view.isPressed = false
                                }
                            }
                        }
                        clickAgent[0] = false
                        clickAgent[1] = false
                    }
                }
                false
            })
            targetView.setOnClickListener(View.OnClickListener {
                if (clickAgent[1]) return@OnClickListener
                clickAgent[1] = true
                if (clickInvoke != null) {
                    clickInvoke.invoke()
                } else
                    if (adapter?.onItemChildClickListener != null) {
                        adapter!!.onItemChildClickListener!!.onItemChildClick(adapter!!, targetView, clickPosition)
                    }
            })
        }
        val total = View.OnClickListener {
            if (!clickAgent[1]) {
                clickAgent[1] = true
                targetView?.performClick()
            }


            if (!childVisibleForEffect) {
                if (clickInvoke != null) {
                    clickInvoke.invoke()
                } else {
                    if (adapter?.onItemChildClickListener != null) {
                        if (targetView != null)
                            adapter!!.onItemChildClickListener!!.onItemChildClick(adapter!!, targetView, clickPosition)
                    }
                }
            }

        }

        val touch = View.OnTouchListener { v, event ->
            if (targetView != null) {
                if (targetView is View.OnTouchListener) {
                    (targetView as View.OnTouchListener).onTouch(targetView, event)
                } else {
                    targetView.onTouchEvent(event)
                }
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (clickAgent[0]) return@OnTouchListener false
                    clickAgent[0] = true
                    clickAgent[1] = true
                    if (targetView != null) {
                        targetView.requestFocus()
                        targetView.isPressed = true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (clickAgent[0]) {
                        if (targetView != null) {
                            targetView.requestFocus()
                            targetView.onTouchEvent(event)
                        }
                    }
                    clickAgent[0] = false
                    clickAgent[1] = false
                }
            }

            false
        }
        for (view in views) {
            if (view != null) {
                if (!view.isClickable) {
                    view.isClickable = true
                }
                view.setOnClickListener(total)
                view.setOnTouchListener(touch)
            }
        }
        return this
    }


    /**
     * set nestview id
     *
     * @param viewId add the child view id   can support childview click
     * @return
     */
    fun setNestView(@IdRes viewId: Int): BaseViewHolder {
        addOnClickListener(viewId)
        addOnLongClickListener(viewId)
        nestViews?.add(viewId)
        return this
    }

    /**
     * add long click view id
     *
     * @param viewIds
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildLongClickListener(listener))}
     *
     *
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this method
     */
    fun addOnLongClickListener(@IdRes vararg viewIds: Int): BaseViewHolder {
        for (viewId in viewIds) {
            itemChildLongClickViewIds?.add(viewId)
            val view = getView<View>(viewId)
            if (view != null) {
                if (!view.isLongClickable) {
                    view.isLongClickable = true
                }
                view.setOnLongClickListener { v ->
                    adapter?.onItemChildLongClickListener != null &&
                            (adapter!!.onItemChildLongClickListener?.onItemChildLongClick(
                                adapter!!,
                                v,
                                clickPosition
                            ) ?: false)
                }
            }
        }
        return this
    }


    /**
     * Sets the on touch listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on touch listener;
     * @return The BaseViewHolder for chaining.
     */
    @Deprecated("")
    fun setOnTouchListener(@IdRes viewId: Int, listener: View.OnTouchListener): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.setOnTouchListener(listener)
        return this
    }

    /**
     * Sets the on long click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on long click listener;
     * @return The BaseViewHolder for chaining.
     * Please use [.addOnLongClickListener] (adapter.setOnItemChildLongClickListener(listener))}
     */
    @Deprecated("")
    fun setOnLongClickListener(@IdRes viewId: Int, listener: View.OnLongClickListener): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.setOnLongClickListener(listener)
        return this
    }

    /**
     * Sets the listview or gridview's item click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item on click listener;
     * @return The BaseViewHolder for chaining.
     * Please use [.addOnClickListener] (int)} (adapter.setOnItemChildClickListener(listener))}
     */
    @Deprecated("")
    fun setOnItemClickListener(@IdRes viewId: Int, listener: AdapterView.OnItemClickListener): BaseViewHolder {
        val view = getView<AdapterView<*>>(viewId)
        view!!.onItemClickListener = listener
        return this
    }

    /**
     * Sets the listview or gridview's item long click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item long click listener;
     * @return The BaseViewHolder for chaining.
     */
    fun setOnItemLongClickListener(@IdRes viewId: Int, listener: AdapterView.OnItemLongClickListener): BaseViewHolder {
        val view = getView<AdapterView<*>>(viewId)
        view!!.onItemLongClickListener = listener
        return this
    }

    /**
     * Sets the listview or gridview's item selected click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item selected click listener;
     * @return The BaseViewHolder for chaining.
     */
    fun setOnItemSelectedClickListener(@IdRes viewId: Int, listener: AdapterView.OnItemSelectedListener): BaseViewHolder {
        val view = getView<AdapterView<*>>(viewId)
        view!!.onItemSelectedListener = listener
        return this
    }

    /**
     * Sets the on checked change listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The checked change listener of compound button.
     * @return The BaseViewHolder for chaining.
     */
    fun setOnCheckedChangeListener(@IdRes viewId: Int, listener: CompoundButton.OnCheckedChangeListener): BaseViewHolder {
        val view = getView<CompoundButton>(viewId)
        view!!.setOnCheckedChangeListener(listener)
        return this
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param tag    The tag;
     * @return The BaseViewHolder for chaining.
     */
    fun setTag(@IdRes viewId: Int, tag: Any): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.tag = tag
        return this
    }

    fun setClickListener(@IdRes viewId: Int, l: (view: View) -> Unit): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.setOnClickListener(l)
        return this
    }


    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param key    The key of tag;
     * @param tag    The tag;
     * @return The BaseViewHolder for chaining.
     */
    fun setTag(@IdRes viewId: Int, key: Int, tag: Any): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.setTag(key, tag)
        return this
    }

    /**
     * Sets the checked status of a checkable.
     *
     * @param viewId  The view id.
     * @param checked The checked status;
     * @return The BaseViewHolder for chaining.
     */
    fun setChecked(@IdRes viewId: Int, checked: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        // View unable cast to Checkable
        if (view is Checkable) {
            (view as Checkable).isChecked = checked
        }
        return this
    }

    /**
     * Set the enabled state of this view.
     *
     * @param viewId  The view id.
     * @param enable The checked status;
     * @return The BaseViewHolder for chaining.
     */
    fun setEnabled(@IdRes viewId: Int, enable: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view!!.isEnabled = enable
        return this
    }

    /**
     * Sets the adapter of a adapter view.
     *
     * @param viewId  The view id.
     * @param adapter The adapter;
     * @return The BaseViewHolder for chaining.
     */
    fun setAdapter(@IdRes viewId: Int, adapter: Adapter): BaseViewHolder {
        val view = getView<AdapterView<Adapter>>(viewId)
        view!!.setAdapter(adapter)
        return this
    }

    /**
     * Sets the adapter of a adapter view.
     *
     * @param adapter The adapter;
     * @return The BaseViewHolder for chaining.
     */
    fun setAdapter(adapter: BaseQuickAdapter<*, *>): BaseViewHolder {
        this.adapter = adapter
        return this
    }

    fun <T : View> getView(@IdRes viewId: Int): T? {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T?
    }
}
