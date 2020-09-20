package cn.aihuaiedu.school.base.expandable

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.aihuaiedu.school.base.expandable.adapter.ExpandableAdapterModel
import cn.aihuaiedu.school.base.expandable.dao.ContextDAO
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel


abstract class BaseExpandableAdapter<T : ExpandableAdapterModel<*>> : EpoxyAdapter {


    private constructor() : super() {

    }

    private var poolCleanBlock: (() -> Unit)? = null

    constructor(poolCleanBlock: () -> Unit) : this() {
        this.poolCleanBlock = poolCleanBlock
    }

    constructor(
        poolCleanBlock: () -> Unit,
        listener: (adapter: BaseExpandableAdapter<*>, position: Int, model: T?) -> Unit
    ) : this(poolCleanBlock) {
        this.listener = listener
    }


    protected var listener: ((adapter: BaseExpandableAdapter<*>, position: Int, model: T?) -> Unit)? = null
        private set


    fun setOnItemClickListener(listener: (adapter: BaseExpandableAdapter<*>, position: Int, model: T?) -> Unit) {
        this.listener = listener
    }


    private var singleExpandedItemMode: Boolean = true
    private val adapterModelsCache: MutableList<ExpandableAdapterModel<*>> = mutableListOf()

    private var mRecyclerView: RecyclerView? = null

    open fun setNewData(groups: Array<ContextDAO>) {
        if (adapterModelsCache.isEmpty()) {
            prepareModels(groups.toMutableList(), adapterModelsCache)
            bind().apply {
                showLastExpanded(selection)
            }
        } else {
            adapterModelsCache.clear()
            prepareModels(groups.toMutableList(), adapterModelsCache)
            rebind().apply {
                showLastExpanded(selection)
            }
        }
    }

    private var selection = -1


    fun resetSelection() {
        selection = -1
        notifyDataSetChanged()
    }

    open fun addNewData(groups: Array<ContextDAO>) {
        if (adapterModelsCache.isEmpty()) {
            prepareModels(groups.toMutableList(), adapterModelsCache)
            bind().apply {
                showLastExpanded(selection)
            }
        } else {
            val fromIndex = adapterModelsCache.size - 1;
            prepareModels(groups.toMutableList(), adapterModelsCache)
            attachBind(fromIndex).apply {
                showLastExpanded(selection)
            }
        }
    }


    final override fun addModels(vararg modelsToAdd: EpoxyModel<*>?) {
        super.addModels(*modelsToAdd)
    }


    final override fun addModel(modelToAdd: EpoxyModel<*>?) {
        super.addModel(modelToAdd)
    }

    final override fun addModels(modelsToAdd: MutableCollection<out EpoxyModel<*>>?) {
        super.addModels(modelsToAdd)
    }


    fun setExpandedMode(single: Boolean) {
        val old = singleExpandedItemMode
        if (old != single) {
            singleExpandedItemMode = single
            if (adapterModelsCache.isNotEmpty())
                rebind().apply {
                    showLastExpanded(selection)
                }
        }
    }

    private fun showLastExpanded(selection: Int) {
        if (selection>=0&&selection <= adapterModelsCache.size - 1) {
            val item = adapterModelsCache[selection]
            item.isExpanded
            showAllItems(item)
            notifyDataSetChanged()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView
    }

    private var layoutManager: LinearLayoutManager? = null
        get() {
            if (field == null) {
                field = this.mRecyclerView?.layoutManager as? LinearLayoutManager
            }
            return field
        }

    protected fun bind() {
        adapterModelsCache.forEach { group ->
            addModel(group)
        }
    }

    protected fun attachBind(fromIndex: Int) {
        adapterModelsCache.subList(fromIndex, adapterModelsCache.size).forEach { group ->
            addModel(group)
        }
    }

    protected fun rebind() {
        removeAllModels()
        bind()
    }


    private fun prepareModels(data: MutableList<ContextDAO>, subModelsList: MutableList<ExpandableAdapterModel<*>>) {
        data.forEach { element ->
            val model = wrapModel(element.groupLevel, element.index) { group ->
                var removeRange: IntRange? = null
                var removeIndex: Int? = null
                if (singleExpandedItemMode) {
                    val list = subModelsList
                        .filter { model ->
                            model.id() != group.id()
                        }
                        .filter { it.isExpanded }
                        .sortedBy { it.index }

                    list.forEach { model ->
                        var beginPosition = -1

                        var endPosition = -1

                        val childModels = model.subItems
                        val beg = ((childModels?.getOrNull(0))?.index ?: -1)
                        val have = beg >= 0
                        if (have) {
                            removeIndex = model.index
                            beginPosition = removeIndex!!
                            endPosition = childModels!!.size
                        }

                        if (beginPosition > -1 && endPosition > 0) {
                            removeRange = IntRange(beginPosition, endPosition)
                        }
                        if (model.isExpanded) {
                            onItemClick(model, false, removeRange)
                        }
                    }
                }
                if (removeRange != null && removeIndex != null) {
                    val selectIndex = group.index
                    val removeStart = removeRange!!.start
                    val removeCount = removeRange!!.endInclusive
                    if (removeStart == selectIndex) {
                        return@wrapModel
                    } else {
                        if (removeIndex!! > selectIndex) {
                            onItemClick(group, true, IntRange(selectIndex + removeCount, 0))
                        } else {
                            onItemClick(group, true, IntRange(selectIndex + removeCount, 0))
                        }
                    }
                } else {
                    onItemClick(group, true)
                }
            }
            val modelSubItems = mutableListOf<ExpandableAdapterModel<*>>()
            model.subItems = modelSubItems
            model.mContextDAO = element
            subModelsList.add(model)
            prepareModels(element.subItems ?: emptyList<ContextDAO>().toMutableList(), modelSubItems)
        }
    }

    abstract fun wrapModel(level: Int, index: Int, lis: (ExpandableAdapterModel<*>) -> Unit): T

    private fun onItemClick(item: ExpandableAdapterModel<*>, shouldUpdate: Boolean, range: IntRange? = null) {
        if (item.isExpanded) {
            hideAllItems(item.subItems ?: emptyList())
            item.isExpanded = false
            val index = item.index
            val level = item.level
            selection(level, index, false)
        } else {
            if (item.subItems == null || item.subItems!!.isEmpty()) {
                listener?.invoke(this, item.index, item as T)
                return
            }
            showAllItems(item)
        }
        if (shouldUpdate) {
            val idx = item.index
            val subSize = item.subItems?.size ?: 0
            if (range != null) {
                if (range.endInclusive > 0) {
                    change(range.start, 1)
                } else {
                    change(range.start, 1)
                }
            } else {
//                if (idx > -1) {
//
//                    change(idx, subSize + 1,true)
//                } else
                notifyDataSetChanged()
            }
            if (subSize > 0) {
                poolCleanBlock?.invoke()
            } else {
                listener?.invoke(this, idx, item as T)
            }
        } else {
            if (range != null) {
                change(range.start, range.endInclusive + 1)
            }
        }
    }

    private fun change(realPosition: Int, itemCount: Int, twoStep: Boolean = false) {
        notifyDataSetChanged()
//        var pos=realPosition
//        if (this.layoutManager!=null){
//            val posFirst=this.layoutManager?.findFirstVisibleItemPosition()?:0
//            pos-=posFirst
//        }
//        if (twoStep){
//            notifyItemChanged(pos)
//            notifyItemRangeInserted(pos+1, itemCount-1)
//        }else
//        notifyItemRangeChanged(pos, itemCount)
    }

    private fun hideAllItems(list: Collection<ExpandableAdapterModel<*>>) {
        list.forEach {
            hideAllItems(it.subItems ?: emptyList())
            it.isExpanded = false
            removeModel(it)
        }
    }

    protected fun showAllItems(item: ExpandableAdapterModel<*>) {
        var after = item
        item.isExpanded = true

        item.subItems?.forEach {
            val modelIndex = getModelPosition(after)
            if (modelIndex == -1) {
            } else {
                insertModelAfter(it, after)
                after = it
            }
        }
        val index = item.index
        val level = item.level
        selection(level, index, true)
    }

    open fun selection(level: Int, index: Int, expand: Boolean) {
        if (expand) {
            selection = level
        } else {
            selection = -1
        }
    }
}