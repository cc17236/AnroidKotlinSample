package cn.aihuaiedu.school.base

import com.huawen.baselibrary.views.refresh.SmartStateRefreshLayout


/**
 * @作者: #Administrator #
 *@日期: #2018/6/26 #
 *@时间: #2018年06月26日 12:56 #
 *@File:Kotlin Class
 */
class UltraRefreshController(smart: SmartStateRefreshLayout?) : RefreshController(smart) {
    private var pipelineCollection: HashMap<Int, PipeLine>? = null


    private var currIndex = 0
        get() {
            pipelineCollection?.forEach {
                val i = it.key
                val pipeLine = it.value
                val fun1 = pipeLine.fun1
                if (fun1 != null) {
                    val rlt = fun1.invoke(i)
                    if (rlt) {
                        return i
                    }
                }
            }

            return 0
        }

    override var pageIndex: Int
        get() {
            var va = super.pageIndex
            val pipeLine = getPipe(currIndex)
            va = pipeLine?.pageIndex ?: va
            return va
        }
        set(value) {

        }

    override var lastLoadData: Int
        get() {
            var va = super.lastLoadData
            val pipeLine = getPipe(currIndex)
            va = pipeLine?.lastLoadData ?: va
            return va
        }
        set(value) {

        }

    private fun getPipe(idx: Int): PipeLine? {
        val pipe = pipelineCollection?.get(idx)
        if (pipe == null) {
            pipelineCollection?.get(idx)
        }
        return pipe
    }


    override fun headFinish(success: Boolean) {
        if (success) {
            val pipeLine = getPipe(currIndex)
            pipeLine?.pageIndex = 1
            if (lastEnableLoadMore) {
                if (lastLoadData < pageSize) {
                    pipeLine?.endless = false
                    super.lockLoadMore(true)
                } else {
                    pipeLine?.endless = true
                    super.lockLoadMore(false)
                }
            }
        }
    }

    override fun footFinish(success: Boolean) {
        val pipeLine = getPipe(currIndex)
        if (lastLoadData >= pageSize && success) {
            if (pipeLine?.pageIndex != null) {
                pipeLine.pageIndex++
                pipeLine?.endless = true
                super.lockLoadMore(false)
            }
        } else {
            if (success) {
                pipeLine?.endless = false
                super.lockLoadMore(true)
            }
        }
    }


    fun setPipelineRefreshEvent(index: Int, fun1: (Boolean, index: Int) -> Unit, fun2: ((index: Int) -> Boolean)?) {
        if (pipelineCollection == null) {
            pipelineCollection = hashMapOf()
        }
        if (pipelineCollection?.containsKey(index) == true) return
        val pipeLine = PipeLine(index, fun1, fun2)
        if (!lastEnableLoadMore) {
            pipeLine.endless = false
        }
        pipelineCollection?.put(index, pipeLine)
        if (this.fun1 == null) {
            this.fun1 = {
                val that = it
                pipelineCollection?.forEach {
                    val i = it.key
                    val pipeLine_ = it.value
                    val rlt = pipeLine_.endless
                    if (that) {
                        pipeLine_.fun0?.invoke(that, i)
                    } else {
                        if (rlt)
                            pipeLine_.fun0?.invoke(that, i)
                    }
                }
            }
        }
        shouldLoadMoreIfNecessary(index)
    }


    override fun loadSize(success: Boolean, loadCount: Int?) {
        val pipeLine = getPipe(currIndex)
        if (pipeLine?.lastLoadData != null) {
            pipeLine.lastLoadData = loadCount ?: pageSize
        }
        super.loadSize(success, loadCount)
    }


    fun shouldLoadMoreIfNecessary(type: Int) {
        if (pipelineCollection != null) {
            val pipe = getPipe(type)
            if (pipe?.endless ?: true == false) {
                super.lockLoadMore(true)
            } else {
                super.lockLoadMore(false)
            }
        }
    }


    open protected class PipeLine {
        internal var index = 0
        internal var fun0: ((Boolean, index: Int) -> Unit)? = null
        internal var fun1: ((index: Int) -> Boolean)? = null
        var pageIndex: Int = 0
        var lastLoadData: Int = 0

        var endless = true

        constructor(index: Int, fun0: ((Boolean, index: Int) -> Unit)?, fun1: ((index: Int) -> Boolean)?) {
            this.index = index
            this.fun0 = fun0
            this.fun1 = fun1
        }
    }


}