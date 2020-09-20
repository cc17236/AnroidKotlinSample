package cn.aihuaiedu.school.base

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.huawen.baselibrary.adapter.BaseQuickAdapter
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.getActivityFromView
import com.huawen.baselibrary.utils.getFragmentFromView
import com.huawen.baselibrary.views.refresh.SmartStateRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.footer.FalsifyFooter
import com.scwang.smartrefresh.layout.header.FalsifyHeader
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.Serializable


/**
 * @作者: #Administrator #
 *@日期: #2018/5/22 #
 *@时间: #2018年05月22日 15:58 #
 *@File:Kotlin Class
 */
open class RefreshController(val smartRefreshLayout: SmartStateRefreshLayout?) : Serializable {
    private var aRefreshLayout: RefreshLayout? = null
    private var bRefreshLayout: RefreshLayout? = null

    protected open var lastEnableLoadMore: Boolean = false
    protected open var lastEnableRefresh: Boolean = false
    protected open var lastLoadData = 0
    open var pageIndex = 0
        protected set
    open var pageSize = 10
        protected set
    //0结束 1刷新 2加载
    private var state = 0


    fun setControllerPageSize(size: Int) {
        this.pageSize = size
    }

    fun isRefresh(): Boolean {
        if (state == 0 || state == 1) return true
        return false
    }

    fun isRefreshFixable(): Boolean {
        if (state == 0 || state == 1) {
            if (state == 0) {
                state = 1
            }
            return true
        }
        return false
    }

    fun reset() {
        pageIndex = 0
        lastLoadData = 0
    }

    var isPreload = false
        protected set

    final fun bindAdapterPreload(adapter: BaseQuickAdapter<*, *>?,recyclerView: RecyclerView, preloadNum: Int = 3) {
        if (adapter == null) return
        adapter.setPreLoadNumber(preloadNum)
        adapter.setEnableLoadMore(true)
        adapter.setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener {
            override fun onLoadMoreRequested() {
                if (!lastEnableLoadMore) {
                    try {
                        recyclerView.post {
                            adapter.loadMoreEnd(true)
                        }
                    } catch (e: Exception) {
                    }
                    return
                }
                if (state == 0) {
                    if(adapter.realItemCount()==0){
                        doAsync {
                            Thread.sleep(500)
                            if(adapter.realItemCount()==0){
                                uiThread {
                                    state = 2
                                    runtimeBlock = {
                                        isPreload=false
                                        recyclerView.post {
                                            if (it == finished) {
                                                adapter.loadMoreEnd(true)
                                            } else {
                                                adapter.loadMoreComplete()
                                            }
                                        }
                                    }
                                    setLoadMore(null)
                                    isPreload=true
                                    fun1?.invoke(false)
                                }
                            }
                        }
                    }else{
                        state = 2
                        runtimeBlock = {
                            isPreload=false
                            if (it == finished) {
                                adapter.loadMoreEnd(true)
                            } else {
                                adapter.loadMoreComplete()
                            }
                        }
                        setLoadMore(null)
                        isPreload=true
                        fun1?.invoke(false)
                    }
                } else {
                    adapter.loadMoreComplete()
                }
            }
        }, null)
    }

    private val finished = 10086
    private val notfinished = 10088

    private var runtimeBlock: (Int) -> Unit = {}
    private var stateCheckHandler = Handler(Looper.getMainLooper(), object : Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            if (msg?.what == finished || msg?.what == notfinished) {
                runtimeBlock(msg.what)
                runtimeBlock = {}
            } else {
                runtimeBlock(0)
                runtimeBlock = {}
                if (isRefresh()) {
                    aRefreshLayout?.finishLoadMore(0, false, false)
                } else {
                    bRefreshLayout?.finishLoadMore(0, false, false)
                }
            }
            return false
        }
    })

    var mSimplePurposeDelegating: OnSimpleMultiPurposeListener? = null

    init {

        watchOnAttachedToWindow {
            if (smartRefreshLayout?.isEnableRefresh() == true) {
                lastEnableRefresh = true
                smartRefreshLayout.setOnRefreshListener {
                    isPreload=false
                    if (it.refreshHeader is FalsifyHeader) {
                        return@setOnRefreshListener
                    }
                    state = 1
                    stateCheckHandler.sendEmptyMessage(notfinished)
                    setRefresh(it)
                    fun1?.invoke(true)
                    stateCheckHandler.sendEmptyMessageDelayed(0, 20 * 1000)
//                (smartRefreshLayout as? SmartStateRefreshLayout)?.mManualLoadMore=true

                }
            }
            if (smartRefreshLayout?.isEnableLoadMore() == true) {
                lastEnableLoadMore = true
                smartRefreshLayout.setOnLoadMoreListener {

                    isPreload=false
                    if (it.refreshFooter is FalsifyFooter) {
                        return@setOnLoadMoreListener
                    }
                    if (state == 2) {
                        stateCheckHandler.sendEmptyMessageDelayed(0, 20 * 1000)
                        return@setOnLoadMoreListener
                    }
                    state = 2
                    setLoadMore(it)
                    fun1?.invoke(false)
                    stateCheckHandler.sendEmptyMessageDelayed(0, 20 * 1000)
                }
            }

            val haveMultiPurposeListener = smartRefreshLayout?.haveMultiPurposeListener() ?: false
            if (haveMultiPurposeListener) {
                val smart = smartRefreshLayout!!
                val activity: Activity? = smart.getActivityFromView()
                val fragment: Fragment? = smart.getFragmentFromView()
                if (fragment != null) {
                    Debuger.print("\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43 当前刷新布局已设置过监听器,将被覆盖,请检查代码=${fragment.javaClass.canonicalName} \uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43")
                } else if (activity != null) {
                    Debuger.print("\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43 当前刷新布局已设置过监听器,将被覆盖,请检查代码=${activity.javaClass.canonicalName} \uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43")
                } else {
                    Debuger.print("\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43 当前刷新布局已设置过监听器,将被覆盖,请检查代码 \uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43\uD83D\uDE43")
                }
            }

            smartRefreshLayout?.setOnMultiPurposeListener(object : OnSimpleMultiPurposeListener() {
                override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {
                    setLoadMore(null)
                    mSimplePurposeDelegating?.onFooterFinish(footer, success)
                }

                override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
                    setRefresh(null)
                    mSimplePurposeDelegating?.onHeaderFinish(header, success)
                }


                override fun onFooterMoving(
                    footer: RefreshFooter?,
                    isDragging: Boolean,
                    percent: Float,
                    offset: Int,
                    footerHeight: Int,
                    maxDragHeight: Int
                ) {
                    super.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight)
                    footerPullingListener?.invoke(percent, false)

                    mSimplePurposeDelegating?.onFooterMoving(
                        footer,
                        isDragging,
                        percent,
                        offset,
                        footerHeight,
                        maxDragHeight
                    )
                }

                override fun onFooterReleased(footer: RefreshFooter?, footerHeight: Int, extendHeight: Int) {
                    super.onFooterReleased(footer, footerHeight, extendHeight)
                    footerPullingListener?.invoke(0f, true)

                    mSimplePurposeDelegating?.onFooterReleased(footer, footerHeight, extendHeight)
                }


                override fun onHeaderMoving(
                    header: RefreshHeader?,
                    isDragging: Boolean,
                    percent: Float,
                    offset: Int,
                    headerHeight: Int,
                    maxDragHeight: Int
                ) {
                    super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
                    headerPullingListener?.invoke(percent, false)

                    mSimplePurposeDelegating?.onHeaderMoving(
                        header,
                        isDragging,
                        percent,
                        offset,
                        headerHeight,
                        maxDragHeight
                    )
                }

                override fun onHeaderReleased(header: RefreshHeader?, headerHeight: Int, extendHeight: Int) {
                    super.onHeaderReleased(header, headerHeight, extendHeight)
                    headerPullingListener?.invoke(0f, true)

                    mSimplePurposeDelegating?.onHeaderReleased(header, headerHeight, extendHeight)
                }

                override fun onFooterStartAnimator(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {
                    super.onFooterStartAnimator(footer, footerHeight, maxDragHeight)

                    mSimplePurposeDelegating?.onFooterStartAnimator(footer, footerHeight, maxDragHeight)
                }

                override fun onHeaderStartAnimator(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
                    super.onHeaderStartAnimator(header, headerHeight, maxDragHeight)
                    mSimplePurposeDelegating?.onHeaderStartAnimator(header, headerHeight, maxDragHeight)
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    super.onLoadMore(refreshLayout)
                    mSimplePurposeDelegating?.onLoadMore(refreshLayout)
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    super.onRefresh(refreshLayout)
                    mSimplePurposeDelegating?.onRefresh(refreshLayout)
                }

                override fun onStateChanged(
                    refreshLayout: RefreshLayout,
                    oldState: RefreshState,
                    newState: RefreshState
                ) {
                    super.onStateChanged(refreshLayout, oldState, newState)
                    mSimplePurposeDelegating?.onStateChanged(refreshLayout, oldState, newState)
                }

            })
            watchOnDeAttachedToWindow {
                stateCheckHandler.removeCallbacksAndMessages(null)
            }
        }
    }

    private fun watchOnDeAttachedToWindow(function: () -> Unit) {
        if (smartRefreshLayout is SmartStateRefreshLayout) {
            smartRefreshLayout.startWatchDismiss {
                function.invoke()
            }
        } else {
            function.invoke()
        }
    }

    private fun watchOnAttachedToWindow(function: () -> Unit) {
        if (smartRefreshLayout is SmartStateRefreshLayout) {
            smartRefreshLayout.startWatch {
                function.invoke()
            }
        } else {
            function.invoke()
        }
    }

    open protected fun headFinish(success: Boolean) {
        if (success) {
            pageIndex = 1
            if (lastEnableLoadMore) {
                if (lastLoadData < pageSize) {
                    smartRefreshLayout?.setEnableLoadMore(false)
                } else
                    smartRefreshLayout?.setEnableLoadMore(true)
            }
        }
    }

    open protected fun footFinish(success: Boolean) {
        if (lastLoadData >= pageSize && success) {
            pageIndex++
            stateCheckHandler.sendEmptyMessage(notfinished)
        } else {
            if (success) {
                smartRefreshLayout?.setEnableLoadMore(false)
                stateCheckHandler.sendEmptyMessage(finished)
            } else {
                stateCheckHandler.sendEmptyMessage(notfinished)
            }
        }
    }

    private var headerPullingListener: ((percent: Float, release: Boolean) -> Unit)? = null
    fun setHeaderPullingListener(pulling: ((percent: Float, release: Boolean) -> Unit)) {
        headerPullingListener = pulling
    }

    private var footerPullingListener: ((percent: Float, release: Boolean) -> Unit)? = null
    fun setFooterPullingListener(pulling: ((percent: Float, release: Boolean) -> Unit)) {
        footerPullingListener = pulling
    }


    fun finishRefresh(succ: Boolean = true) {
        finishRefresh<Any>(succ, null, null)
    }

    fun <T> finishRefresh(
        succ: Boolean = true,
        list: List<T>? = null,
        fun2: ((refresh: Boolean, list: List<T>?) -> Unit)? = null
    ) {
        stateCheckHandler?.removeCallbacksAndMessages(null)
        val loadCount = list?.size ?: 0
        if (state == 1) {
            fun2?.invoke(true, list as? MutableList<T>)
            val oldForce = force
            if (force) {
                finishForceRefresh(succ, loadCount)
            }
            force = false
            finishRefreshInternal(succ, loadCount)
            if (!oldForce) {
                headFinish(succ)
            }
        } else if (state == 2) {
            fun2?.invoke(false, list as? MutableList<T>)
            val oldForce = force
            if (force) {
                finishForceLoadMore(succ, loadCount)
            }
            force = false
            finishLoadMoreInternal(succ, loadCount)
            if (!oldForce) {
                footFinish(succ)
            }
        } else {
            fun2?.invoke(true, list as? MutableList<T>)
            finishForceRefresh(succ, loadCount)
            finishRefreshInternal(succ, loadCount)
        }
        state = 0
    }

    private fun finishForceRefresh(success: Boolean, loadCount: Int? = 0) {
        stateCheckHandler?.removeCallbacksAndMessages(null)
        loadSize(success, loadCount)
        headFinish(success)
    }

    open protected fun loadSize(success: Boolean, loadCount: Int?) {
        lastLoadData = loadCount ?: pageSize
    }

    private fun finishForceLoadMore(success: Boolean, loadCount: Int? = 0) {
        loadSize(success, loadCount)
        footFinish(success)
    }


    private fun finishLoadMoreInternal(succ: Boolean = true, loadCount: Int? = 0) {
        loadSize(succ, loadCount)
        bRefreshLayout?.finishLoadMore(0, succ, loadCount == null)

    }

    private fun finishRefreshInternal(succ: Boolean = true, loadCount: Int? = 0) {
        loadSize(succ, loadCount)
        aRefreshLayout?.finishRefresh(0, succ, (loadCount ?: 0) < pageSize)
    }


    fun setLoadMore(refreshLayout: RefreshLayout?) {
        bRefreshLayout = refreshLayout
    }

    fun setRefresh(refreshLayout: RefreshLayout?) {
        aRefreshLayout = refreshLayout
    }

    open protected var fun1: ((Boolean) -> Unit)? = null


    fun setRefreshEvent(fun1: (Boolean) -> Unit) {
        this.fun1 = fun1
    }

    fun initRefresh() {
        smartRefreshLayout?.autoRefresh()
    }

    fun initLoadMore() {
        smartRefreshLayout?.autoLoadMore()
    }

    private var force = false
    fun forceLoadMore() {
        force = true
        if (smartRefreshLayout?.isEnableLoadMore() == true) {
            lastEnableLoadMore = true
        } else {
            return
        }
        state = 2
        isPreload=false
        fun1?.invoke(false)
    }

    fun forceRefresh() {
        pageIndex = 0
        force = true
        if (smartRefreshLayout?.isEnableLoadMore() == true) {
            lastEnableLoadMore = true
        }
        state = 1
        isPreload=false
        fun1?.invoke(true)
    }

    fun lockLoadMore(lockLoadMore: Boolean) {
        smartRefreshLayout?.shouldLockLoadMore(lockLoadMore)
    }

    fun setState(state: Int) {
        this.state = state
    }


    abstract class OnSimpleMultiPurposeListener : OnMultiPurposeListener {


        override fun onFooterMoving(
            footer: RefreshFooter?,
            isDragging: Boolean,
            percent: Float,
            offset: Int,
            footerHeight: Int,
            maxDragHeight: Int
        ) {

        }

        override fun onHeaderStartAnimator(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {

        }

        override fun onFooterReleased(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {

        }

        override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {

        }

        override fun onHeaderMoving(
            header: RefreshHeader?,
            isDragging: Boolean,
            percent: Float,
            offset: Int,
            headerHeight: Int,
            maxDragHeight: Int
        ) {

        }

        override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {

        }

        override fun onFooterStartAnimator(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {

        }

        override fun onHeaderReleased(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {

        }

        override fun onLoadMore(refreshLayout: RefreshLayout) {

        }

        override fun onRefresh(refreshLayout: RefreshLayout) {

        }

        override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {

        }
    }

}