package com.huawen.baselibrary.views

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.os.Parcelable
import android.widget.FrameLayout

/**
 * Frame默认适配器,切换时只保存fragment对象引用,不保存状态(不会调用onCreate,onActivityCreated会调用onCreateView),
 * 内存不足时的意外回收会保存(Fragment.SaveState)状态
 * 多层嵌套时注意FragmentManager的获取  Fragment.getChildFragmentManager()
 *
 * @author you
 */
abstract class BaseFrameAdapter(
    protected val mManager: androidx.fragment.app.FragmentManager
    /**
     * fragmentManager
     */
) : androidx.viewpager.widget.PagerAdapter() {

    private val mObservable = DataSetObservable()

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        mObservable.notifyChanged()
    }

    open fun getTabLayout(position: Int): Int {
        return 0
    }

    open fun getTabIcon(position: Int): Int? {
        return null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return frameTitle(position)
    }

    abstract fun frameTitle(position: Int): CharSequence?

    /**
     * 当前加载的Fragment
     */
    var mCurrentFragment: androidx.fragment.app.Fragment? = null
        protected set
    /**
     * 当前加载fargment的位置
     */
    protected var mCurrentPosition = -1

    /**
     * 获取fragment
     * @param position
     * @return
     */
    abstract fun getItem(position: Int): androidx.fragment.app.Fragment?

    open fun instantFragment(fm: FrameLayout, position: Int) {
        if (this.mCurrentPosition == position) {
            if (this.mCurrentFragment == getItem(position))
                return
        }
        var ft: androidx.fragment.app.FragmentTransaction? = mManager.beginTransaction()
        if (mCurrentFragment != null) {
            ft?.detach(mCurrentFragment!!)
            mCurrentFragment?.setMenuVisibility(false)
            mCurrentFragment?.userVisibleHint = false
        }
        val itemId = getItemId(position)
        val name = makeFragmentName(fm.id, itemId)
        var fragment = mManager.findFragmentByTag(name)
        if (fragment != null) {
            ft?.attach(fragment)
        } else {
            fragment = getItem(position)
            if (fragment != null)
                ft?.add(fm.id, fragment, makeFragmentName(fm.id, itemId))
        }
        if (fragment != null) {
            fragment.setMenuVisibility(true)
            fragment.userVisibleHint = true
        }
        mCurrentFragment = fragment
        this.mCurrentPosition = position
        ft?.commitAllowingStateLoss()
        ft = null
        mManager.executePendingTransactions()
    }

    /**
     * 保存fragment的状态
     * @return
     */
    override fun saveState(): Parcelable? {
        return null
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        mObservable.unregisterObserver(observer)
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        mObservable.registerObserver(observer)
    }

    /**
     * 恢复fragment的状态,
     * @param state
     * @param loader
     */

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }

    /**
     * fragment唯一标记
     * @param position
     * @return
     */
    open fun getItemId(position: Int): Long {
        val frag = getItem(position)
        return (frag?.javaClass?.canonicalName?.hashCode()?:position).toLong()
    }

    /**
     * 生成FragmentManager管理的tag
     * @param viewId
     * @param id
     * @return
     */
    protected fun makeFragmentName(viewId: Int, id: Long): String {
        return "FrameAdapter:$viewId:$id"
    }
}