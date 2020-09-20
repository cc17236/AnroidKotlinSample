/*
 * Copyright 2013 Joan Zapata
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawen.baselibrary.adapter

import android.animation.Animator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.huawen.baselibrary.adapter.animation.*
import com.huawen.baselibrary.adapter.entity.IExpandable
import com.huawen.baselibrary.adapter.loadmore.LoadMoreView
import com.huawen.baselibrary.adapter.loadmore.SimpleLoadMoreView
import com.huawen.baselibrary.adapter.util.MultiTypeDelegate
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.DebugerCaller
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
abstract class BaseQuickAdapter<T, K : BaseViewHolder>
/**
 * Same as QuickAdapter#QuickAdapter(Context,int) but with
 * some initialization data.
 *
 * @param layoutResId The layout resource id of each item.
 * @param data        A new list is created out of this one to avoid mutable list
 */
@JvmOverloads constructor(@LayoutRes layoutResId: Int, data: MutableList<T>? = null) : RecyclerView.Adapter<K>() {

    //load more
    private var mNextLoadEnable = false
    /**
     * Returns the enabled status for load more.
     *
     * @return True if load more is enabled, false otherwise.
     */
    var isLoadMoreEnable = false
        private set
    /**
     * @return Whether the Adapter is actively showing load
     * progress.
     */
    var isLoading = false
        private set
    private var mLoadMoreView: LoadMoreView = SimpleLoadMoreView()
    private var mRequestLoadMoreListener: RequestLoadMoreListener? = null
    private var mEnableLoadMoreEndClick = false
    /**
     * @return The callback to be invoked with an item in this RecyclerView has
     * been clicked and held, or null id no callback as been set.
     */
    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    protected var onItemClickListener_: OnItemClickListener? = null


    fun itemClickListener(): OnItemClickListener? {
        return onItemClickListener_
    }

    fun setOnItemClickListener(fun0: (adapter: BaseQuickAdapter<*, *>, view: View, position: Int) -> Unit) {
        this.onItemClickListener_ = object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                fun0.invoke(adapter, view, position)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener_ = onItemClickListener
    }

    fun getSingleLayoutRes(): Int {
        return mLayoutResId
    }
    /**
     * @return The callback to be invoked with an item in this RecyclerView has
     * been long clicked and held, or null id no callback as been set.
     */
    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been long clicked and held
     *
     * @param listener The callback that will run
     */
    var onItemLongClickListener: OnItemLongClickListener? = null
    /**
     * @return The callback to be invoked with an itemchild in this RecyclerView has
     * been clicked, or null id no callback has been set.
     */
    /**
     * Register a callback to be invoked when an itemchild in View has
     * been  clicked
     *
     * @param listener The callback that will run
     */
    var onItemChildClickListener: OnItemChildClickListener? = null
    /**
     * @return The callback to be invoked with an itemChild in this RecyclerView has
     * been long clicked, or null id no callback has been set.
     */
    /**
     * Register a callback to be invoked when an itemchild  in this View has
     * been long clicked and held
     *
     * @param listener The callback that will run
     */
    var onItemChildLongClickListener: OnItemChildLongClickListener? = null

    private var mFirstOnlyEnable = true
    private var mOpenAnimationEnable = false
    private val mInterpolator = LinearInterpolator()
    private var mDuration = 300
    private var mLastPosition = -1

    private var mCustomAnimation: BaseAnimation? = null
    private var mSelectAnimation: BaseAnimation = AlphaInAnimation()
    //header footer
    /**
     * Return root layout of header
     */

    var headerLayout: LinearLayout? = null
        private set
    /**
     * Return root layout of footer
     */
    var footerLayout: LinearLayout? = null
        private set
    //empty
    private var mEmptyLayout: FrameLayout? = null
    private var mIsUseEmpty = true
    private var mHeadAndEmptyEnable: Boolean = false
    private var mFootAndEmptyEnable: Boolean = false
    protected var mContext: Context? = null
    protected var mLayoutResId: Int = 0
    private var mLayoutInflater: LayoutInflater? = null
    protected var mData: MutableList<T>? = null


    fun inflater(): LayoutInflater {
        return mLayoutInflater!!
    }

    protected var recyclerView: RecyclerView? = null
        private set

    /**
     * up fetch start
     */
    var isUpFetchEnable: Boolean = false
    var isUpFetching: Boolean = false
    private var mUpFetchListener: UpFetchListener? = null

    /**
     * start up fetch position, default is 1.
     */
    private var mStartUpFetchPosition = 1

    /**
     * Load more view count
     *
     * @return 0 or 1
     */
    val loadMoreViewCount: Int
        get() {
            if (mRequestLoadMoreListener == null || !isLoadMoreEnable) {
                return 0
            }
            if (!mNextLoadEnable && mLoadMoreView.isLoadEndMoreGone) {
                return 0
            }
            return if (mData!!.size == 0) {
                0
            } else 1
        }

    /**
     * Gets to load more locations
     *
     * @return
     */
    val loadMoreViewPosition: Int
        get() = headerLayoutCount + mData!!.size + footerLayoutCount

    /**
     * Get the data of list
     *
     * @return 列表数据
     */
    val data: MutableList<T>
       get() {
           if (mData==null)return arrayListOf()
          return mData!!
       }
    /**
     * if setHeadView will be return 1 if not will be return 0.
     * notice: Deprecated! Use [ViewGroup.getChildCount] of [.getHeaderLayout] to replace.
     *
     * @return
     */
    val headerViewsCount: Int
        @Deprecated("")
        get() = headerLayoutCount

    /**
     * if mFooterLayout will be return 1 or not will be return 0.
     * notice: Deprecated! Use [ViewGroup.getChildCount] of [.getFooterLayout] to replace.
     *
     * @return
     */
    val footerViewsCount: Int
        @Deprecated("")
        get() = footerLayoutCount

    /**
     * if addHeaderView will be return 1, if not will be return 0
     */
    val headerLayoutCount: Int
        get() = if (headerLayout == null || headerLayout!!.childCount == 0) {
            0
        } else 1

    /**
     * if addFooterView will be return 1, if not will be return 0
     */
    val footerLayoutCount: Int
        get() = if (footerLayout == null || footerLayout!!.childCount == 0) {
            0
        } else 1

    /**
     * if show empty view will be return 1 or not will be return 0
     *
     * @return
     */
    val emptyViewCount: Int
        get() {
            if (mEmptyLayout == null || mEmptyLayout!!.childCount == 0) {
                return 0
            }
            if (!mIsUseEmpty) {
                return 0
            }
            return if (mData!!.size != 0) {
                0
            } else 1
        }

    /**
     * if asFlow is true, footer/header will arrange like normal item view.
     * only works when use [GridLayoutManager],and it will ignore span size.
     */
    var isHeaderViewAsFlow: Boolean = false
    var isFooterViewAsFlow: Boolean = false

    private var mSpanSizeLookup: SpanSizeLookup? = null

    var multiTypeDelegate: MultiTypeDelegate<T>? = null

    private//Return to header view notify position
    val headerViewPosition: Int
        get() {
            if (emptyViewCount == 1) {
                if (mHeadAndEmptyEnable) {
                    return 0
                }
            } else {
                return 0
            }
            return -1
        }

    private//Return to footer view notify position
    val footerViewPosition: Int
        get() {
            if (emptyViewCount == 1) {
                var position = 1
                if (mHeadAndEmptyEnable && headerLayoutCount != 0) {
                    position++
                }
                if (mFootAndEmptyEnable) {
                    return position
                }
            } else {
                return headerLayoutCount + mData!!.size
            }
            return -1
        }

    /**
     * When the current adapter is empty, the BaseQuickAdapter can display a special view
     * called the empty view. The empty view is used to provide feedback to the user
     * that no data is available in this AdapterView.
     *
     * @return The view to show if the adapter is empty.
     */
    val emptyView: View?
        get() = mEmptyLayout

    private var mPreLoadNumber = 1

    @IntDef(ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnimationType

    private fun checkNotNull() {
        if (recyclerView == null) {
            throw RuntimeException("please bind recyclerView first!")
        }
    }

    /**
     * same as recyclerView.setAdapter(), and save the instance of recyclerView
     */
    fun bindToRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            throw RuntimeException("Don't bind twice")
        }
        this.recyclerView = recyclerView
        recyclerView?.adapter = this
    }

    /**
     * @see .setOnLoadMoreListener
     */
    @Deprecated(
        "This method is because it can lead to crash: always call this method while RecyclerView is computing a layout or scrolling.\n" +
                "      Please use {@link #setOnLoadMoreListener(RequestLoadMoreListener, RecyclerView)}"
    )
    fun setOnLoadMoreListener(requestLoadMoreListener: RequestLoadMoreListener) {
        openLoadMore(requestLoadMoreListener)
    }

    private fun openLoadMore(requestLoadMoreListener: RequestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener
        mNextLoadEnable = true
        isLoadMoreEnable = true
        isLoading = false
    }

    fun setOnLoadMoreListener(requestLoadMoreListener: RequestLoadMoreListener, recyclerView: RecyclerView?) {
        openLoadMore(requestLoadMoreListener)
        if (recyclerView == null) {
            this.recyclerView = recyclerView
        }
    }

    /**
     * bind recyclerView [.bindToRecyclerView] before use!
     *
     * @see .disableLoadMoreIfNotFullPage
     */
    fun disableLoadMoreIfNotFullPage() {
        checkNotNull()
        disableLoadMoreIfNotFullPage(recyclerView)
    }

    /**
     * check if full page after [.setNewData], if full, it will enable load more again.
     *
     *
     * 不是配置项！！
     *
     *
     * 这个方法是用来检查是否满一屏的，所以只推荐在 [.setNewData] 之后使用
     * 原理很简单，先关闭 load more，检查完了再决定是否开启
     *
     *
     * 不是配置项！！
     *
     * @param recyclerView your recyclerView
     * @see .setNewData
     */
    fun disableLoadMoreIfNotFullPage(recyclerView: RecyclerView?) {
        setEnableLoadMore(false)
        if (recyclerView == null) return
        val manager = recyclerView.layoutManager ?: return
        if (manager is LinearLayoutManager) {
            recyclerView.postDelayed({
                if (isFullScreen(manager)) {
                    setEnableLoadMore(true)
                }
            }, 50)
        } else if (manager is StaggeredGridLayoutManager) {
            recyclerView.postDelayed({
                val positions = IntArray(manager.spanCount)
                manager.findLastCompletelyVisibleItemPositions(positions)
                val pos = getTheBiggestNumber(positions) + 1
                if (pos != itemCount) {
                    setEnableLoadMore(true)
                }
            }, 50)
        }
    }

    private fun isFullScreen(llm: LinearLayoutManager): Boolean {
        return llm.findLastCompletelyVisibleItemPosition() + 1 != itemCount || llm.findFirstCompletelyVisibleItemPosition() != 0
    }

    private fun getTheBiggestNumber(numbers: IntArray?): Int {
        var tmp = -1
        if (numbers == null || numbers.size == 0) {
            return tmp
        }
        for (num in numbers) {
            if (num > tmp) {
                tmp = num
            }
        }
        return tmp
    }

    fun setStartUpFetchPosition(startUpFetchPosition: Int) {
        mStartUpFetchPosition = startUpFetchPosition
    }

    private fun autoUpFetch(positions: Int) {
        if (!isUpFetchEnable || isUpFetching) {
            return
        }
        if (positions <= mStartUpFetchPosition && mUpFetchListener != null) {
            mUpFetchListener!!.onUpFetch()
        }
    }

    fun setUpFetchListener(upFetchListener: UpFetchListener) {
        mUpFetchListener = upFetchListener
    }

    interface UpFetchListener {
        fun onUpFetch()
    }

    /**
     * up fetch end
     */
    fun setNotDoAnimationCount(count: Int) {
        mLastPosition = count
    }

    /**
     * Set custom load more
     *
     * @param loadingView 加载视图
     */
    fun setLoadMoreView(loadingView: LoadMoreView) {
        this.mLoadMoreView = loadingView
    }

    /**
     * Refresh end, no more data
     *
     * @param gone if true gone the load more view
     */
    @JvmOverloads
    fun loadMoreEnd(gone: Boolean = false) {
        if (loadMoreViewCount == 0) {
            return
        }
        if (!isLoading)return
        isLoading = false
        mNextLoadEnable = false
        mLoadMoreView.setLoadMoreEndGone(gone)
        if (gone) {
            isLoadMoreEnable=false
            crashChecker(1)
            notifyDataSetChanged()//todo 崩溃问题临时方案
//            notifyItemRemoved(loadMoreViewPosition)
            crashChecker(2)
        } else {
            mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_END
//
            crashChecker(1)
            notifyDataSetChanged() //todo 崩溃问题临时方案
//            notifyItemChanged(loadMoreViewPosition)
            crashChecker(2)
        }
    }

    /**
     * Refresh complete
     */
    fun loadMoreComplete() {
        if (loadMoreViewCount == 0) {
            return
        }
        isLoading = false
        mNextLoadEnable = true
        mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_DEFAULT
//
        crashChecker(1)
        notifyDataSetChanged() //todo 崩溃问题临时方案
//        notifyItemChanged(loadMoreViewPosition)
        crashChecker(2)
    }

    /**
     * Refresh failed
     */
    fun loadMoreFail() {
        if (loadMoreViewCount == 0) {
            return
        }
        isLoading = false
        mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_FAIL
//
        crashChecker(1)
        notifyDataSetChanged() //todo 崩溃问题临时方案
//        notifyItemChanged(loadMoreViewPosition)
        crashChecker(2)
    }

    fun crashChecker(i: Int) {
//        val traceElement = DebugerCaller.getCallerStackTraceElement()
//        var logTag = "crashChecker"
//        var logBody = "$i"
//        logTag += "(方法名:${traceElement.methodName})"
//        val taskName = StringBuilder()
//        if (!Debuger.hideStackLine) {
//            taskName.append("(")
//                .append(traceElement.fileName).append(":")
//                .append(traceElement.lineNumber).append(")")
//        }
//        logBody = taskName.toString() + logBody
//        DebugerCaller.e(logBody, logTag)
    }


    /**
     * Set the enabled state of load more.
     *
     * @param enable True if load more is enabled, false otherwise.
     */
    fun setEnableLoadMore(enable: Boolean) {
        val oldLoadMoreCount = loadMoreViewCount
        isLoadMoreEnable = enable
        val newLoadMoreCount = loadMoreViewCount

        if (oldLoadMoreCount == 1) {
            if (newLoadMoreCount == 0) {
                crashChecker(1)
                notifyDataSetChanged() //TODO 崩溃问题临时方案
//                notifyItemRemoved(loadMoreViewPosition)
                crashChecker(2)
            }
        } else {
            if (newLoadMoreCount == 1) {
                mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_DEFAULT
                crashChecker(1)
                notifyDataSetChanged() //TODO 崩溃问题临时方案
//                notifyItemInserted(loadMoreViewPosition)
                crashChecker(2)
            }
        }
    }

    /**
     * Sets the duration of the animation.
     *
     * @param duration The length of the animation, in milliseconds.
     */
    fun setDuration(duration: Int) {
        mDuration = duration
    }

    /**
     * If you have added headeview, the notification view refreshes.
     * Do not need to care about the number of headview, only need to pass in the position of the final view
     * @param position
     */
    fun refreshNotifyItemChanged(position: Int) {
        notifyItemChanged(position + headerLayoutCount)
    }

    init {
        this.mData = data ?: arrayListOf()
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId
        }
    }

    constructor(data: MutableList<T>?) : this(0, data) {}

    protected fun dip(size: Int): Float {
        return (mContext?.dip(size) ?: 0).toFloat()
    }

    protected fun dip(size: Float): Float {
        return (mContext?.dip(size) ?: 0).toFloat()
    }


    protected fun sp(size: Int): Float {
        return (mContext?.sp(size) ?: 0).toFloat()
    }

    protected fun sp(size: Float): Float {
        return (mContext?.sp(size) ?: 0).toFloat()
    }

    /**
     * setting up a new instance to data;
     *
     * @param data
     */
    open fun setNewData(data: List<T>?) {
        this.mData = if (data != null) ArrayList(data) else ArrayList()
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true
            isLoadMoreEnable = true
            isLoading = false
            mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_DEFAULT
        }
        mLastPosition = -1
        notifyDataSetChanged()
    }


    /**
     * insert  a item associated with the specified position of adapter
     *
     * @param position
     * @param item
     */
    @Deprecated("use {@link #addData(int, Object)} instead")
    fun add(@IntRange(from = 0) position: Int, item: T) {
        addData(position, item)
    }

    /**
     * add one new data in to certain location
     *
     * @param position
     */
    open fun addData(@IntRange(from = 0) position: Int, data: T?) {
        if (data == null) return
        mData?.add(position, data)
        notifyItemInserted(position + headerLayoutCount)
        compatibilityDataSizeChanged(1)
    }

    /**
     * add one new data
     */
    fun addData(data: T) {
        mData?.add(data)
        notifyItemInserted(mData!!.size + headerLayoutCount)
        compatibilityDataSizeChanged(1)
    }

    /**
     * remove the item associated with the specified position of adapter
     *
     * @param position
     */
    open fun remove(@IntRange(from = 0) position: Int) {
        mData?.removeAt(position)
        val internalPosition = position + headerLayoutCount
        notifyItemRemoved(internalPosition)
        compatibilityDataSizeChanged(0)
        notifyItemRangeChanged(internalPosition, mData!!.size - internalPosition)
    }

    /**
     * change data
     */
    open fun setData(@IntRange(from = 0) index: Int, data: T?) {
        if (data == null) return
        mData?.set(index, data)
        notifyItemChanged(index + headerLayoutCount)
    }

    /**
     * add new data in to certain location
     *
     * @param position the insert position
     * @param newData  the new data collection
     */
    fun addData(@IntRange(from = 0) position: Int, newData: Collection<T>) {
        mData!!.addAll(position, newData)
        notifyItemRangeInserted(position + headerLayoutCount, newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    /**
     * add new data to the end of mData
     *
     * @param newData the new data collection
     */
    open fun addData(newData: Collection<T>?) {
        if (newData == null) return
        mData?.addAll(newData)
        notifyItemRangeInserted(mData!!.size - newData.size + headerLayoutCount, newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    /**
     * use data to replace all item in mData. this method is different [.setNewData],
     * it doesn't change the mData reference
     *
     * @param data data collection
     */
    fun replaceData(data: Collection<T>) {
        // 不是同一个引用才清空列表
        if (data !== mData) {
            mData!!.clear()
            mData!!.addAll(data)
        }
        notifyDataSetChanged()
    }

    /**
     * compatible getLoadMoreViewCount and getEmptyViewCount may change
     *
     * @param size Need compatible data size
     */
    private fun compatibilityDataSizeChanged(size: Int) {
        val dataSize = if (mData == null) 0 else mData!!.size
        if (dataSize == size) {
            notifyDataSetChanged()
        }
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return The data at the specified position.
     */
    fun getItem(@IntRange(from = 0) position: Int): T? {
        return if (position >= 0 && position < mData!!.size)
            mData!![position]
        else
            null
    }

    fun realItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun getItemCount(): Int {
        var count: Int
        if (emptyViewCount == 1) {
            count = 1
            if (mHeadAndEmptyEnable && headerLayoutCount != 0) {
                count++
            }
            if (mFootAndEmptyEnable && footerLayoutCount != 0) {
                count++
            }
        } else {
            count = headerLayoutCount + mData!!.size + footerLayoutCount + loadMoreViewCount
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        if (emptyViewCount == 1) {
            val header = mHeadAndEmptyEnable && headerLayoutCount != 0
            when (position) {
                0 -> return if (header) {
                    HEADER_VIEW
                } else {
                    EMPTY_VIEW
                }
                1 -> return if (header) {
                    EMPTY_VIEW
                } else {
                    FOOTER_VIEW
                }
                2 -> return FOOTER_VIEW
                else -> return EMPTY_VIEW
            }
        }
        val numHeaders = headerLayoutCount
        if (position < numHeaders) {
            return HEADER_VIEW
        } else {
            var adjPosition = position - numHeaders
            val adapterCount = mData!!.size
            if (adjPosition < adapterCount) {
                return getDefItemViewType(adjPosition)
            } else {
                adjPosition = adjPosition - adapterCount
                val numFooters = footerLayoutCount
                return if (adjPosition < numFooters) {
                    FOOTER_VIEW
                } else {
                    LOADING_VIEW
                }
            }
        }
    }

    protected open fun getDefItemViewType(position: Int): Int {
        return if (multiTypeDelegate != null) {
            multiTypeDelegate!!.getDefItemViewType(mData!!, position)
        } else super.getItemViewType(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): K {
        var baseViewHolder: K? = null
        this.mContext = parent.context
        this.mLayoutInflater = LayoutInflater.from(mContext)
        when (viewType) {
            LOADING_VIEW -> baseViewHolder = getLoadingView(parent)
            HEADER_VIEW -> {
                if (headerLayout != null)
                    baseViewHolder = createBaseViewHolder(headerLayout!!)
            }
            EMPTY_VIEW -> {
                if (mEmptyLayout != null) {
                    baseViewHolder = createBaseViewHolder(mEmptyLayout!!)
                }
            }
            FOOTER_VIEW -> {
                if (footerLayout != null)
                    baseViewHolder = createBaseViewHolder(footerLayout!!)
            }

            else -> {
                baseViewHolder = onCreateDefViewHolder(parent, viewType)
                bindViewClickListener(baseViewHolder)
            }
        }
        if (baseViewHolder == null) {
            baseViewHolder = onCreateDefViewHolder(parent, viewType)
            bindViewClickListener(baseViewHolder)
        }
        baseViewHolder?.setAdapter(this)
        return baseViewHolder

    }

    private fun getLoadingView(parent: ViewGroup): K {
        val view = getItemView(mLoadMoreView.layoutId, parent)
        val holder = createBaseViewHolder(view)
        holder.itemView.setOnClickListener {
            if (mLoadMoreView.loadMoreStatus == LoadMoreView.STATUS_FAIL) {
                notifyLoadMoreToLoading()
            }
            if (mEnableLoadMoreEndClick && mLoadMoreView.loadMoreStatus == LoadMoreView.STATUS_END) {
                notifyLoadMoreToLoading()
            }
        }
        return holder
    }

    /**
     * The notification starts the callback and loads more
     */
    fun notifyLoadMoreToLoading() {
        if (mLoadMoreView.loadMoreStatus == LoadMoreView.STATUS_LOADING) {
            return
        }
        mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_DEFAULT
        crashChecker(1)
        notifyDataSetChanged() //TODO 崩溃问题临时方案
//        notifyItemChanged(loadMoreViewPosition)
        crashChecker(2)
    }

    /**
     * Load more without data when settings are clicked loaded
     *
     * @param enable
     */
    fun enableLoadMoreEndClick(enable: Boolean) {
        mEnableLoadMoreEndClick = enable
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * simple to solve item will layout using all
     * [.setFullSpan]
     *
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: K) {
        super.onViewAttachedToWindow(holder)
        val type = holder.itemViewType
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
            setFullSpan(holder)
        } else {
            addAnimation(holder)
        }
    }

    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
     * @param holder True if this item should traverse all spans.
     */
    protected fun setFullSpan(holder: RecyclerView.ViewHolder) {
        if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            val params = holder
                .itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            params.isFullSpan = true
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            val gridManager = manager as? GridLayoutManager
            gridManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val type = getItemViewType(position)
                    if (type == HEADER_VIEW && isHeaderViewAsFlow) {
                        return 1
                    }
                    if (type == FOOTER_VIEW && isFooterViewAsFlow) {
                        return 1
                    }
                    return if (mSpanSizeLookup == null) {
                        if (isFixedViewType(type)) gridManager?.spanCount ?: 0 else 1
                    } else {
                        if (isFixedViewType(type))
                            gridManager?.spanCount ?: 0
                        else
                            mSpanSizeLookup!!.getSpanSize(
                                gridManager,
                                position - headerLayoutCount
                            )
                    }
                }


            }
        }
    }

    protected open fun isFixedViewType(type: Int): Boolean {
        return type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW
    }

    interface SpanSizeLookup {
        fun getSpanSize(gridLayoutManager: GridLayoutManager?, position: Int): Int
    }

    /**
     * @param spanSizeLookup instance to be used to query number of spans occupied by each item
     */
    fun setSpanSizeLookup(spanSizeLookup: SpanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup
    }

    /**
     * To bind different types of holder and solve different the bind events
     *
     * @param holder
     * @param position
     * @see .getDefItemViewType
     */
    override fun onBindViewHolder(holder: K, position: Int) {
        //Add up fetch logic, almost like load more, but simpler.
        autoUpFetch(position)
        //Do not move position, need to change before LoadMoreView binding
        autoLoadMore(position)
        val viewType = holder.itemViewType

        when (viewType) {
            0 -> {
                val item = getItem(position - headerLayoutCount)
                if (item != null)
                    convert(holder, item)
            }
            LOADING_VIEW -> {
                mLoadMoreView.convert(holder)
            }
            HEADER_VIEW -> {
            }
            EMPTY_VIEW -> {
                val emptyView = emptyView
                if (emptyView != null)
                    emptyConvert(holder, emptyView)
            }
            FOOTER_VIEW -> {
            }
            else -> {
                val item = getItem(position - headerLayoutCount)
                if (item != null)
                    convert(holder, item)
            }
        }
    }

    protected open fun emptyConvert(helper: K, emptyView: View) {

    }

    private fun bindViewClickListener(baseViewHolder: BaseViewHolder?) {
        if (baseViewHolder == null) {
            return
        }
        val view = baseViewHolder.itemView ?: return
        if (onItemClickListener_ != null) {
            view.setOnClickListener { v -> setOnItemClick(v, baseViewHolder.layoutPosition - headerLayoutCount) }
        }
        if (onItemLongClickListener != null) {
            view.setOnLongClickListener { v ->
                setOnItemLongClick(
                    v,
                    baseViewHolder.layoutPosition - headerLayoutCount
                )
            }
        }
    }

    /**
     * override this method if you want to override click event logic
     *
     * @param v
     * @param position
     */
    fun setOnItemClick(v: View, position: Int) {
        onItemClickListener_?.onItemClick(this@BaseQuickAdapter, v, position)
    }

    /**
     * override this method if you want to override longClick event logic
     *
     * @param v
     * @param position
     * @return
     */
    fun setOnItemLongClick(v: View, position: Int): Boolean {
        return onItemLongClickListener?.onItemLongClick(this@BaseQuickAdapter, v, position) ?: false
    }

    protected open fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): K {
        var layoutId = mLayoutResId
        if (multiTypeDelegate != null) {
            layoutId = multiTypeDelegate!!.getLayoutId(viewType)
        }
        return createBaseViewHolder(parent, layoutId)
    }

    protected fun createBaseViewHolder(parent: ViewGroup, layoutResId: Int): K {
        return createBaseViewHolder(getItemView(layoutResId, parent))
    }

    /**
     * if you want to use subclass of BaseViewHolder in the adapter,
     * you must override the method to create new ViewHolder.
     *
     * @param view view
     * @return new ViewHolder
     */
    open protected fun createBaseViewHolder(view: View?): K {
        if (view == null) throw RuntimeException("绑定适配器必须保证view不能为空")
        var temp: Class<*>? = javaClass
        var z: Class<*>? = null
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp)
            temp = temp.superclass
        }
        val k: K?
        // 泛型擦除会导致z为null
        if (z == null) {
            k = BaseViewHolder(view) as K
        } else {
            k = createGenericKInstance(z, view)
        }
        return k ?: BaseViewHolder(view) as K
    }

    /**
     * try to create Generic K instance
     *
     * @param z
     * @param view
     * @return
     */
    private fun createGenericKInstance(z: Class<*>, view: View?): K? {
        try {
            val constructor: Constructor<*>
            // inner and unstatic class
            if (z.isMemberClass && !Modifier.isStatic(z.modifiers)) {
                constructor = z.getDeclaredConstructor(javaClass, View::class.java)
                constructor.isAccessible = true
                return constructor.newInstance(this, view) as K
            } else {
                constructor = z.getDeclaredConstructor(View::class.java)
                constructor.isAccessible = true
                return constructor.newInstance(view) as K
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * get generic parameter K
     *
     * @param z
     * @return
     */
    private fun getInstancedGenericKClass(z: Class<*>): Class<*>? {
        val type = z.genericSuperclass
        if (type is ParameterizedType) {
            val types = type.actualTypeArguments
            for (temp in types) {
                if (temp is Class<*>) {
                    if (BaseViewHolder::class.java.isAssignableFrom(temp)) {
                        return temp
                    }
                } else if (temp is ParameterizedType) {
                    val rawType = temp.rawType
                    if (rawType is Class<*> && BaseViewHolder::class.java.isAssignableFrom(rawType)) {
                        return rawType
                    }
                }
            }
        }
        return null
    }

    /**
     * @param header
     * @param index
     * @param orientation
     */
    @JvmOverloads
    fun addHeaderView(header: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
        var index = index
        if (headerLayout == null) {
            headerLayout = LinearLayout(header.context)
            if (orientation == LinearLayout.VERTICAL) {
                headerLayout!!.orientation = LinearLayout.VERTICAL
                headerLayout!!.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                headerLayout!!.orientation = LinearLayout.HORIZONTAL
                headerLayout!!.layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
        }
        val childCount = headerLayout!!.childCount
        if (index < 0 || index > childCount) {
            index = childCount
        }
        headerLayout!!.addView(header, index)
        if (headerLayout!!.childCount == 1) {
            val position = headerViewPosition
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return index
    }

    @JvmOverloads
    fun setHeaderView(header: View, index: Int = 0, orientation: Int = LinearLayout.VERTICAL): Int {
        if (headerLayout == null || headerLayout!!.childCount <= index) {
            return addHeaderView(header, index, orientation)
        } else {
            headerLayout!!.removeViewAt(index)
            headerLayout!!.addView(header, index)
            return index
        }
    }

    /**
     * Add footer view to mFooterLayout and set footer view position in mFooterLayout.
     * When index = -1 or index >= child count in mFooterLayout,
     * the effect of this method is the same as that of [.addFooterView].
     *
     * @param footer
     * @param index  the position in mFooterLayout of this footer.
     * When index = -1 or index >= child count in mFooterLayout,
     * the effect of this method is the same as that of [.addFooterView].
     */
    @JvmOverloads
    fun addFooterView(footer: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
        var index = index
        if (footerLayout == null) {
            footerLayout = LinearLayout(footer.context)
            if (orientation == LinearLayout.VERTICAL) {
                footerLayout!!.orientation = LinearLayout.VERTICAL
                footerLayout!!.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                footerLayout!!.orientation = LinearLayout.HORIZONTAL
                footerLayout!!.layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
        }
        val childCount = footerLayout!!.childCount
        if (index < 0 || index > childCount) {
            index = childCount
        }
        footerLayout!!.addView(footer, index)
        if (footerLayout!!.childCount == 1) {
            val position = footerViewPosition
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return index
    }

    @JvmOverloads
    fun setFooterView(header: View, index: Int = 0, orientation: Int = LinearLayout.VERTICAL): Int {
        if (footerLayout == null || footerLayout!!.childCount <= index) {
            return addFooterView(header, index, orientation)
        } else {
            footerLayout!!.removeViewAt(index)
            footerLayout!!.addView(header, index)
            return index
        }
    }

    /**
     * remove header view from mHeaderLayout.
     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
     *
     * @param header
     */
    fun removeHeaderView(header: View) {
        if (headerLayoutCount == 0) return

        headerLayout!!.removeView(header)
        if (headerLayout!!.childCount == 0) {
            val position = headerViewPosition
            if (position != -1) {
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * remove footer view from mFooterLayout,
     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
     *
     * @param footer
     */
    fun removeFooterView(footer: View) {
        if (footerLayoutCount == 0) return

        footerLayout!!.removeView(footer)
        if (footerLayout!!.childCount == 0) {
            val position = footerViewPosition
            if (position != -1) {
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * remove all header view from mHeaderLayout and set null to mHeaderLayout
     */
    fun removeAllHeaderView() {
        if (headerLayoutCount == 0) return

        headerLayout!!.removeAllViews()
        val position = headerViewPosition
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    /**
     * remove all footer view from mFooterLayout and set null to mFooterLayout
     */
    fun removeAllFooterView() {
        if (footerLayoutCount == 0) return

        footerLayout!!.removeAllViews()
        val position = footerViewPosition
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    fun setEmptyView(layoutResId: Int, viewGroup: ViewGroup?) {
        val view = LayoutInflater.from(viewGroup!!.context).inflate(layoutResId, viewGroup, false)
        setEmptyView(view)
    }

    /**
     * bind recyclerView [.bindToRecyclerView] before use!
     * Recommend you to use [.setEmptyView]
     * @see .bindToRecyclerView
     */
    @Deprecated("")
    fun setEmptyView(layoutResId: Int) {
        checkNotNull()
        setEmptyView(layoutResId, recyclerView)
    }

    fun setEmptyView(emptyView: View) {
        var insert = false
        if (mEmptyLayout == null) {
            mEmptyLayout = FrameLayout(emptyView.context)
            val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            val lp = emptyView.layoutParams
            if (lp != null) {
                layoutParams.width = lp.width
                layoutParams.height = lp.height
            }
            mEmptyLayout!!.layoutParams = layoutParams
            insert = true
        }
        mEmptyLayout!!.removeAllViews()
        mEmptyLayout!!.addView(emptyView)
        mIsUseEmpty = true
        if (insert) {
            if (emptyViewCount == 1) {
                var position = 0
                if (mHeadAndEmptyEnable && headerLayoutCount != 0) {
                    position++
                }
                notifyItemInserted(position)
            }
        }
    }

    /**
     * Call before [RecyclerView.setAdapter]
     *
     * @param isHeadAndEmpty false will not show headView if the data is empty true will show emptyView and headView
     */
    fun setHeaderAndEmpty(isHeadAndEmpty: Boolean) {
        setHeaderFooterEmpty(isHeadAndEmpty, false)
    }

    /**
     * set emptyView show if adapter is empty and want to show headview and footview
     * Call before [RecyclerView.setAdapter]
     *
     * @param isHeadAndEmpty
     * @param isFootAndEmpty
     */
    fun setHeaderFooterEmpty(isHeadAndEmpty: Boolean, isFootAndEmpty: Boolean) {
        mHeadAndEmptyEnable = isHeadAndEmpty
        mFootAndEmptyEnable = isFootAndEmpty
    }

    /**
     * Set whether to use empty view
     *
     * @param isUseEmpty
     */
    fun isUseEmpty(isUseEmpty: Boolean) {
        mIsUseEmpty = isUseEmpty
    }

    @Deprecated("")
    fun setAutoLoadMoreSize(preLoadNumber: Int) {
        setPreLoadNumber(preLoadNumber)
    }

    fun setPreLoadNumber(preLoadNumber: Int) {
        if (preLoadNumber > 1) {
            mPreLoadNumber = preLoadNumber
        }
    }

    private fun autoLoadMore(position: Int) {
        if (loadMoreViewCount == 0) {
            return
        }
        if (position < itemCount - mPreLoadNumber) {
            return
        }
        if (mLoadMoreView.loadMoreStatus != LoadMoreView.STATUS_DEFAULT) {
            return
        }
        mLoadMoreView.loadMoreStatus = LoadMoreView.STATUS_LOADING
        if (!isLoading) {
            isLoading = true
            if (recyclerView != null) {
                recyclerView!!.post { mRequestLoadMoreListener!!.onLoadMoreRequested() }
            } else {
                mRequestLoadMoreListener!!.onLoadMoreRequested()
            }
        }
    }


    /**
     * add animation when you want to show time
     *
     * @param holder
     */
    private fun addAnimation(holder: RecyclerView.ViewHolder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.layoutPosition > mLastPosition) {
                var animation: BaseAnimation? = null
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation
                } else {
                    animation = mSelectAnimation
                }
                for (anim in animation!!.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.layoutPosition)
                }
                mLastPosition = holder.layoutPosition
            }
        }
    }

    /**
     * set anim to start when loading
     *
     * @param anim
     * @param index
     */
    protected fun startAnim(anim: Animator, index: Int) {
        anim.setDuration(mDuration.toLong()).start()
        anim.interpolator = mInterpolator
    }

    /**
     * @param layoutResId ID for an XML layout resource to load
     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
     * provides a set of LayoutParams values for root of the returned
     * hierarchy
     * @return view will be return
     */
    protected open fun getItemView(@LayoutRes layoutResId: Int, parent: ViewGroup): View {
        return mLayoutInflater!!.inflate(layoutResId, parent, false)
    }


    interface RequestLoadMoreListener {

        fun onLoadMoreRequested()

    }


    /**
     * Set the view animation type.
     *
     * @param animationType One of [.ALPHAIN], [.SCALEIN], [.SLIDEIN_BOTTOM],
     * [.SLIDEIN_LEFT], [.SLIDEIN_RIGHT].
     */
    fun openLoadAnimation(@AnimationType animationType: Int) {
        this.mOpenAnimationEnable = true
        mCustomAnimation = null
        when (animationType) {
            ALPHAIN -> mSelectAnimation = AlphaInAnimation()
            SCALEIN -> mSelectAnimation = ScaleInAnimation()
            SLIDEIN_BOTTOM -> mSelectAnimation = SlideInBottomAnimation()
            SLIDEIN_LEFT -> mSelectAnimation = SlideInLeftAnimation()
            SLIDEIN_RIGHT -> mSelectAnimation = SlideInRightAnimation()
            else -> {
            }
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    fun openLoadAnimation(animation: BaseAnimation) {
        this.mOpenAnimationEnable = true
        this.mCustomAnimation = animation
    }

    /**
     * To open the animation when loading
     */
    fun openLoadAnimation() {
        this.mOpenAnimationEnable = true
    }

    /**
     * To close the animation when loading
     */
    fun closeLoadAnimation() {
        this.mOpenAnimationEnable = false
    }

    /**
     * [.addAnimation]
     *
     * @param firstOnly true just show anim when first loading false show anim when load the data every time
     */
    fun isFirstOnly(firstOnly: Boolean) {
        this.mFirstOnlyEnable = firstOnly
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract fun convert(helper: K, item: T)

    /**
     * get the specific view by position,e.g. getViewByPosition(2, R.id.textView)
     *
     *
     * bind recyclerView [.bindToRecyclerView] before use!
     *
     * @see .bindToRecyclerView
     */
    fun getViewByPosition(position: Int, @IdRes viewId: Int): View? {
        checkNotNull()
        return getViewByPosition(recyclerView, position, viewId)
    }

    fun getViewByPosition(recyclerView: RecyclerView?, position: Int, @IdRes viewId: Int): View? {
        if (recyclerView == null) {
            return null
        }
        val viewHolder = recyclerView.findViewHolderForLayoutPosition(position) as BaseViewHolder? ?: return null
        return viewHolder.getView(viewId)
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun recursiveExpand(position: Int, list: List<*>): Int {
        var count = list.size
        var pos = position + list.size - 1
        var i = list.size - 1
        while (i >= 0) {
            if (list[i] is IExpandable<*>) {
                val item = list[i] as IExpandable<*>
                if (item.isExpanded && hasSubItems(item)) {
                    val subList = item.subItems
                    val sl = subList as? Collection<T>
                    if (sl != null) {
                        mData?.addAll(pos + 1, sl)
                        val subItemCount = recursiveExpand(pos + 1, subList)
                        count += subItemCount
                    }
                }
            }
            i--
            pos--
        }
        return count

    }

    /**
     * Expand an expandable item
     *
     * @param position     position of the item
     * @param animate      expand items with animation
     * @param shouldNotify notify the RecyclerView to rebind items, **false** if you want to do it
     * yourself.
     * @return the number of items that have been added.
     */
    fun expand(@IntRange(from = 0) position: Int, animate: Boolean, shouldNotify: Boolean): Int {
        var position = position
        position -= headerLayoutCount

        val expandable = getExpandableItem(position) ?: return 0
        if (!hasSubItems(expandable)) {
            expandable.isExpanded = true
            notifyItemChanged(position)
            return 0
        }
        var subItemCount = 0
        if (!expandable.isExpanded) {
            val list = expandable.subItems
            val ls = list as? Collection<T>
            if (ls != null) {
                mData?.addAll(position + 1, ls)
                subItemCount += recursiveExpand(position + 1, list)
            }
            expandable.isExpanded = true
            //            subItemCount += list.size();
        }
        val parentPos = position + headerLayoutCount
        if (shouldNotify) {
            if (animate) {
                notifyItemChanged(parentPos)
                notifyItemRangeInserted(parentPos + 1, subItemCount)
            } else {
                notifyDataSetChanged()
            }
        }
        return subItemCount
    }

    /**
     * Expand an expandable item
     *
     * @param position position of the item, which includes the header layout count.
     * @param animate  expand items with animation
     * @return the number of items that have been added.
     */
    fun expand(@IntRange(from = 0) position: Int, animate: Boolean): Int {
        return expand(position, animate, true)
    }

    /**
     * Expand an expandable item with animation.
     *
     * @param position position of the item, which includes the header layout count.
     * @return the number of items that have been added.
     */
    fun expand(@IntRange(from = 0) position: Int): Int {
        return expand(position, true, true)
    }

    fun expandAll(position: Int, animate: Boolean, notify: Boolean): Int {
        var position = position
        position -= headerLayoutCount

        var endItem: T? = null
        if (position + 1 < this.mData!!.size) {
            endItem = getItem(position + 1)
        }

        val expandable = getExpandableItem(position) ?: return 0

        if (!hasSubItems(expandable)) {
            expandable.isExpanded = true
            notifyItemChanged(position)
            return 0
        }

        var count = expand(position + headerLayoutCount, false, false)
        for (i in position + 1 until this.mData!!.size) {
            val item = getItem(i)

            if (item === endItem) {
                break
            }
            if (isExpandable(item)) {
                count += expand(i + headerLayoutCount, false, false)
            }
        }

        if (notify) {
            if (animate) {
                notifyItemRangeInserted(position + headerLayoutCount + 1, count)
            } else {
                notifyDataSetChanged()
            }
        }
        return count
    }

    /**
     * expand the item and all its subItems
     *
     * @param position position of the item, which includes the header layout count.
     * @param init     whether you are initializing the recyclerView or not.
     * if **true**, it won't notify recyclerView to redraw UI.
     * @return the number of items that have been added to the adapter.
     */
    fun expandAll(position: Int, init: Boolean): Int {
        return expandAll(position, true, !init)
    }

    fun expandAll() {

        for (i in mData!!.size - 1 + headerLayoutCount downTo headerLayoutCount) {
            expandAll(i, false, false)
        }
    }

    private fun recursiveCollapse(@IntRange(from = 0) position: Int): Int {
        val item = getItem(position)
        if (!isExpandable(item)) {
            return 0
        }
        val expandable = item as IExpandable<*>?
        var subItemCount = 0
        if (expandable!!.isExpanded) {
            val subItems = expandable.subItems ?: return 0

            for (i in subItems.indices.reversed()) {
                val subItem = subItems[i] as? T
                var pos = getItemPosition(subItem)
                if (pos < 0) {
                    continue
                } else if (pos < position) {
                    pos = position + i + 1
                    if (pos >= mData!!.size) {
                        continue
                    }
                }
                if (subItem is IExpandable<*>) {
                    subItemCount += recursiveCollapse(pos)
                }
                mData!!.removeAt(pos)
                subItemCount++
            }
        }
        return subItemCount
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @param animate  collapse with animation or not.
     * @param notify   notify the recyclerView refresh UI or not.
     * @return the number of subItems collapsed.
     */
    @JvmOverloads
    fun collapse(@IntRange(from = 0) position: Int, animate: Boolean = true, notify: Boolean = true): Int {
        var position = position
        position -= headerLayoutCount

        val expandable = getExpandableItem(position) ?: return 0
        val subItemCount = recursiveCollapse(position)
        expandable.isExpanded = false
        val parentPos = position + headerLayoutCount
        if (notify) {
            if (animate) {
                notifyItemChanged(parentPos)
                notifyItemRangeRemoved(parentPos + 1, subItemCount)
            } else {
                notifyDataSetChanged()
            }
        }
        return subItemCount
    }

    private fun getItemPosition(item: T?): Int {
        return if (item != null && mData != null && !mData!!.isEmpty()) mData!!.indexOf(item) else -1
    }

    fun hasSubItems(item: IExpandable<*>?): Boolean {
        if (item == null) {
            return false
        }
        val list = item.subItems
        return list != null && list.size > 0
    }

    open fun isExpandable(item: T?): Boolean {
        return item != null && item is IExpandable<*>
    }

    private fun getExpandableItem(position: Int): IExpandable<*>? {
        val item = getItem(position)
        return if (isExpandable(item)) {
            item as IExpandable<*>?
        } else {
            null
        }
    }

    /**
     * Get the parent item position of the IExpandable item
     *
     * @return return the closest parent item position of the IExpandable.
     * if the IExpandable item's level is 0, return itself position.
     * if the item's level is negative which mean do not implement this, return a negative
     * if the item is not exist in the data list, return a negative.
     */
    fun getParentPosition(item: T): Int {
        val position = getItemPosition(item)
        if (position == -1) {
            return -1
        }

        // if the item is IExpandable, return a closest IExpandable item position whose level smaller than this.
        // if it is not, return the closest IExpandable item position whose level is not negative
        val level: Int
        if (item is IExpandable<*>) {
            level = (item as IExpandable<*>).level
        } else {
            level = Integer.MAX_VALUE
        }
        if (level == 0) {
            return position
        } else if (level == -1) {
            return -1
        }

        for (i in position downTo 0) {
            val temp = mData!![i]
            if (temp is IExpandable<*>) {
                val expandable = temp as IExpandable<*>
                if (expandable.level >= 0 && expandable.level < level) {
                    return i
                }
            }
        }
        return -1
    }

    /**
     * Interface definition for a callback to be invoked when an itemchild in this
     * view has been clicked
     */
    interface OnItemChildClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param view     The view whihin the ItemView that was clicked
         * @param position The position of the view int the adapter
         */
        fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
    }


    /**
     * Interface definition for a callback to be invoked when an childView in this
     * view has been clicked and held.
     */
    interface OnItemChildLongClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param view     The childView whihin the itemView that was clicked and held.
         * @param position The position of the view int the adapter
         * @return true if the callback consumed the long click ,false otherwise
         */
        fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * view has been clicked and held.
     */
    interface OnItemLongClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param adapter  the adpater
         * @param view     The view whihin the RecyclerView that was clicked and held.
         * @param position The position of the view int the adapter
         * @return true if the callback consumed the long click ,false otherwise
         */
        fun onItemLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean
    }


    /**
     * Interface definition for a callback to be invoked when an item in this
     * RecyclerView itemView has been clicked.
     */
    interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this RecyclerView has
         * been clicked.
         *
         * @param adapter  the adpater
         * @param view     The itemView within the RecyclerView that was clicked (this
         * will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         */
        fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
    }

    companion object {

        //Animation
        /**
         * Use with [.openLoadAnimation]
         */
        const val ALPHAIN = 0x00000001
        /**
         * Use with [.openLoadAnimation]
         */
        const val SCALEIN = 0x00000002
        /**
         * Use with [.openLoadAnimation]
         */
        const val SLIDEIN_BOTTOM = 0x00000003
        /**
         * Use with [.openLoadAnimation]
         */
        const val SLIDEIN_LEFT = 0x00000004
        /**
         * Use with [.openLoadAnimation]
         */
        const val SLIDEIN_RIGHT = 0x00000005

        protected val TAG = BaseQuickAdapter::class.java.simpleName
        const val HEADER_VIEW = 0x00000111
        const val LOADING_VIEW = 0x00000222
        const val FOOTER_VIEW = 0x00000333
        const val EMPTY_VIEW = 0x00000555
    }
}
/**
 * Refresh end, no more data
 */
/**
 * Append header to the rear of the mHeaderLayout.
 *
 * @param header
 */
/**
 * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
 * When index = -1 or index >= child count in mHeaderLayout,
 * the effect of this method is the same as that of [.addHeaderView].
 *
 * @param header
 * @param index  the position in mHeaderLayout of this header.
 * When index = -1 or index >= child count in mHeaderLayout,
 * the effect of this method is the same as that of [.addHeaderView].
 */
/**
 * Append footer to the rear of the mFooterLayout.
 *
 * @param footer
 */
/**
 * Collapse an expandable item that has been expanded..
 *
 * @param position the position of the item, which includes the header layout count.
 * @return the number of subItems collapsed.
 */
/**
 * Collapse an expandable item that has been expanded..
 *
 * @param position the position of the item, which includes the header layout count.
 * @return the number of subItems collapsed.
 */
