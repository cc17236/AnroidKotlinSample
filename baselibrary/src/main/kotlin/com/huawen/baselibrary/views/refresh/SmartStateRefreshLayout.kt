package com.huawen.baselibrary.views.refresh

import android.content.Context
import androidx.annotation.NonNull
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.footer.FalsifyFooter
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import java.util.*

class SmartStateRefreshLayout : SmartRefreshLayout, SmartDelegate {

    init {
        mEnableAutoLoadMore = false
    }

    override fun getInterrupter(): OnRefreshInterrupter? {
        return interrupter
    }

    private val mListener = SmartOnRefreshListener(this)
    private var interrupter: OnRefreshInterrupter? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    final override fun setOnRefreshListener(listener: OnRefreshListener?): RefreshLayout {
        if (listener != null)
            if (mListener.shouldDismissOnRefreshHash != listener.hashCode()) {
                mListener.shouldDismissOnRefreshHash = listener.hashCode()
            }
        mListener.childRefreshListener = listener
        return super.setOnRefreshListener(mListener)
    }


    private var proxyListener: OnMultiPurposeListener?=null
    override fun setOnMultiPurposeListener(listener: OnMultiPurposeListener?): RefreshLayout {
        proxyListener=listener
        return super.setOnMultiPurposeListener(listener)
    }

    fun haveMultiPurposeListener():Boolean{
        return (proxyListener!=null)
    }

    override fun setOnLoadMoreListener(listener: OnLoadMoreListener?): RefreshLayout {
        if (listener != null)
            if (mListener.shouldDismissOnLoadMoreHash != listener.hashCode()) {
                mListener.shouldDismissOnLoadMoreHash = listener.hashCode()
            }
        mListener.childLoadMoreListener = listener
        return super.setOnLoadMoreListener(mListener)
    }

    private final class SmartOnRefreshListener(val delegate: SmartDelegate?) : OnRefreshListener, OnLoadMoreListener {
        override fun onLoadMore(refreshLayout: RefreshLayout) {
            childLoadMoreListener?.onLoadMore(refreshLayout)
            if (normalOnLoadMoreHash != 0 && normalOnLoadMoreHash == shouldDismissOnLoadMoreHash) {
                refreshLayout.finishLoadMore(2000)
            } else if (normalOnRefreshHash == 0 && shouldDismissOnRefreshHash == 0) {
                refreshLayout.finishLoadMore(2000)
            }
            delegate?.getInterrupter()?.onLoadMore(Date())
        }

        override fun onRefresh(refreshLayout: RefreshLayout) {
            childRefreshListener?.onRefresh(refreshLayout)
            if (normalOnRefreshHash != 0 && normalOnRefreshHash == shouldDismissOnRefreshHash) {
                refreshLayout.finishRefresh(2000)
            } else if (normalOnRefreshHash == 0 && shouldDismissOnRefreshHash == 0) {
                refreshLayout.finishRefresh(2000)
            }
            delegate?.getInterrupter()?.onRefresh(Date())
        }

        internal var normalOnRefreshHash = 0
        internal var shouldDismissOnRefreshHash = 0
        internal var normalOnLoadMoreHash = 0
        internal var shouldDismissOnLoadMoreHash = 0


        var childRefreshListener: OnRefreshListener? = null
        var childLoadMoreListener: OnLoadMoreListener? = null

    }

    fun isEnableRefresh(): Boolean {
        return mEnableRefresh
    }

    fun isEnableLoadMore(): Boolean {
        return mEnableLoadMore
    }


    fun setOnRefreshInterrupter(interrupter: OnRefreshInterrupter) {
        this.interrupter = interrupter
        var mRefreshListener: OnRefreshListener? = null
        var mLoadMoreListener: OnLoadMoreListener? = null
//        try {
//            val listener = (this as SmartRefreshLayout).javaClass.getField("mRefreshListener")
//            listener?.isAccessible = true
//            mRefreshListener = listener?.get(this.javaClass) as? OnRefreshListener
//        } catch (e: Exception) {
//        }
//        try {
//            val listener = (this as SmartRefreshLayout).javaClass.getField("mLoadMoreListener")
//            listener?.isAccessible = true
//            mLoadMoreListener = listener?.get(this.javaClass) as? OnLoadMoreListener
//        } catch (e: Exception) {
//        }
//        if (mRefreshListener != null) {
//            mListener.normalOnRefreshHash = mRefreshListener.hashCode()
//        }
//        if (mLoadMoreListener != null) {
//            mListener.normalOnLoadMoreHash = mLoadMoreListener.hashCode()
//        }

//        setOnRefreshListener(mRefreshListener)
//        setOnLoadMoreListener(mLoadMoreListener)

    }

    private var lastFooter: RefreshFooter? = null
    private var lockLoadMore: Boolean = false

    fun shouldLockLoadMore(lockLoadMore: Boolean) {
        this.lockLoadMore = lockLoadMore
    }


    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        val footer = refreshFooter
        if (lastFooter == null && lockLoadMore && !(footer is FalsifyFooter) && mEnableLoadMore && nestedScrollAxes > 0 && state == RefreshState.None) {
            lastFooter = footer
            mManualLoadMore = true
            setRefreshFooter(FalsifyFooter(context))
        }
        return super.onStartNestedScroll(child, target, nestedScrollAxes)
    }

    override fun notifyStateChanged(state: RefreshState?) {
        if ((state == RefreshState.None || state == RefreshState.PullDownToRefresh) && mEnableLoadMore) {
            resetFooter()
        }
        super.notifyStateChanged(state)
    }

    private fun resetFooter() {
        if (lastFooter != null) {
            mManualLoadMore = true
            setRefreshFooter(lastFooter!!)
            mFooterLocked = false
            lastFooter = null
        }
    }

    override fun dispatchTouchEvent(e: MotionEvent?): Boolean {
        when (e?.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                if (state == RefreshState.None) {
                    resetFooter()
                }
            }
        }
        return super.dispatchTouchEvent(e)
    }


    override fun setRefreshFooter(footer: RefreshFooter): RefreshLayout {
        val footer_ = super.setRefreshFooter(footer)

        return footer_
    }


    private var onAttachToWindowSucc = false
    private var onDeAttachToWindowSucc = false
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttachToWindowSucc = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDeAttachToWindowSucc = true
    }

    fun startWatch(fun0: () -> Unit) {
        if (onAttachToWindowSucc) {
            fun0.invoke()
        } else
            addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {

                }

                override fun onViewAttachedToWindow(v: View?) {
                    fun0.invoke()
                    removeOnAttachStateChangeListener(this)
                }

            })
    }

    fun startWatchDismiss(fun0: () -> Unit) {
        if (onDeAttachToWindowSucc) {
            fun0.invoke()
        } else
            addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    fun0.invoke()
                    removeOnAttachStateChangeListener(this)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
    }

    companion object {

        fun setDefaultRefreshHeaderCreator(@NonNull creator: DefaultRefreshHeaderCreator) {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator(creator)
        }

        fun setDefaultRefreshFooterCreator(creator: DefaultRefreshFooterCreator) {
            SmartRefreshLayout.setDefaultRefreshFooterCreator(creator)
        }
    }
}