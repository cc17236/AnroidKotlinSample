package com.huawen.baselibrary.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Created by you on 2015/8/27.
 *
 * adapter模式管理fragment
 *
 */

class FragmentFrameLayout : FrameLayout {
    /**
     * fragment adapter
     */

    /**
     * 设置管理Fragment适配器
     */
    var frameAdapter: BaseFrameAdapter? = null
        set(adapter) {
            if (field != null) {
                if (this.mRestoredPosition >= 0) {
                    field?.restoreState(mRestoredAdapterState, mRestoredClassLoader)
                    setCurrentItem(mRestoredPosition)
                    mRestoredPosition = -1
                    mRestoredAdapterState = null
                    mRestoredClassLoader = null
                }
            }
            field = adapter
        }
    /**
     * 当前选中的fragment所在位置
     */
    private var mCurrentPosition = -1
    /**
     * (Fragment)是否附着窗体上
     */
    private var isAttachedToWindows: Boolean = false
    /**
     * 内存不足时的意外回收保存的数据
     */
    private var mRestoredAdapterState: Parcelable? = null

    private var mRestoredPosition = -1

    private var mRestoredClassLoader: ClassLoader? = null

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        if (id == View.NO_ID) {
            this.id = hashCode()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isAttachedToWindows) {
            isAttachedToWindows = true
            setCurrentItem(mCurrentPosition)
        }
    }

    fun getCurrentPosition(): Int {
        return mCurrentPosition
    }

    /**
     * 设置显示当前位置的Fragment
     */
    fun setCurrentItem(position: Int) {
        if (frameAdapter != null && position >= 0) {
            val old = mCurrentPosition
            mCurrentPosition = position
            if (isAttachedToWindows) {
                frameAdapter!!.instantFragment(this@FragmentFrameLayout, position)
//                if (old != mCurrentPosition)
//                    mOnPageChangeListeners?.forEach {
//                        it.onPageSelected(position)
//                    }
//            } else if (old == -1) {
//                mOnPageChangeListeners?.forEach {
//                    it.onPageSelected(position)
//                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.position = mCurrentPosition
        if (frameAdapter != null) {
            ss.adapterState = frameAdapter!!.saveState()
        }
        return ss
    }

//    private var mOnPageChangeListeners: MutableList<FixedTabLayout.TabLayoutOnPageChangeListener>? = null
//
//    fun removeOnPageChangeListener(listener: FixedTabLayout.TabLayoutOnPageChangeListener) {
//        if (mOnPageChangeListeners != null) {
//            mOnPageChangeListeners?.remove(listener)
//        }
//    }
//
//    fun addOnPageChangeListener(listener: FixedTabLayout.TabLayoutOnPageChangeListener) {
//        if (mOnPageChangeListeners == null) {
//            mOnPageChangeListeners = mutableListOf()
//            mOnPageChangeListeners?.add(listener)
//        }
//    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        if (frameAdapter != null) {
            frameAdapter!!.restoreState(state.adapterState, state.loader)
            setCurrentItem(state.position)
        } else {
            mRestoredPosition = state.position
            mRestoredAdapterState = state.adapterState
            mRestoredClassLoader = state.loader
        }
    }

    /**
     * Frame默认适配器,切换时只保存fragment对象引用,不保存状态(不会调用onCreate,onActivityCreated会调用onCreateView),
     * 内存不足时的意外回收会保存(Fragment.SaveState)状态
     * 多层嵌套时注意FragmentManager的获取  Fragment.getChildFragmentManager()
     *
     * @author you
     */
    abstract class Adapter(
        /**
         * fragmentManager
         */
        protected val mManager: FragmentManager
    ) {
        /**
         * 当前加载的Fragment
         */
        var curFragment: Fragment? = null
            protected set
        /**
         * 当前加载fargment的位置
         */
        protected var mCurrentPosition = -1

        /**
         * fragments count
         * @return
         */
        abstract val count: Int

        /**
         * 获取fragment
         * @param position
         * @return
         */
        abstract fun getItem(position: Int): Fragment

        fun instantFragment(fm: FrameLayout, position: Int) {
            if (this.mCurrentPosition == position)
                return
            var ft: FragmentTransaction? = mManager.beginTransaction()
            if (curFragment != null) {
                ft!!.detach(curFragment!!)
                curFragment!!.setMenuVisibility(false)
                curFragment!!.userVisibleHint = false
            }
            val itemId = getItemId(position)
            val name = makeFragmentName(fm.id, itemId)
            var fragment: Fragment? = mManager.findFragmentByTag(name)
            if (fragment != null) {
                ft!!.attach(fragment)
            } else {
                fragment = getItem(position)
                ft!!.add(fm.id, fragment, makeFragmentName(fm.id, itemId))
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.userVisibleHint = true
            }
            curFragment = fragment
            this.mCurrentPosition = position
            ft.commitAllowingStateLoss()
            ft = null
            mManager.executePendingTransactions()
        }

        /**
         * 保存fragment的状态
         * @return
         */
        fun saveState(): Parcelable? {
            return null
        }

        /**
         * 恢复fragment的状态,
         * @param state
         * @param loader
         */

        fun restoreState(state: Parcelable?, loader: ClassLoader?) {

        }

        /**
         * fragment唯一标记
         * @param position
         * @return
         */
        fun getItemId(position: Int): Long {
            return position.toLong()
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


    /**
     * 修复开发者选项->不保留活动->resume应用时崩溃问题
     * 内存不足时的状态保存
     */
    internal class SavedState : View.BaseSavedState {
        var position: Int = 0
        var adapterState: Parcelable? = null
        var loader: ClassLoader? = null
        constructor(parcel: Parcel) : super(parcel) {
            try {
                var loader = loader
                if (loader == null) {
                    loader = javaClass.classLoader
                }
                position = parcel.readInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        constructor(superState: Parcelable?) : super(superState) {
        }
        private constructor(`in`: Parcel, loader_: ClassLoader?) : super(`in`) {
            try {
                var loader = loader_
                if (loader == null) {
                    loader = javaClass.classLoader
                }
                position = `in`.readInt()
                adapterState = `in`.readParcelable(loader)
                this.loader = loader
            } catch (e: Exception) {
            }
        }
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            try {
                parcel.writeInt(position)
                if (adapterState != null) {
                    parcel.writeParcelable(adapterState, flags)
                }
            } catch (e: Exception) {
            }
        }
        override fun describeContents(): Int {
            return 0
        }
        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel, SavedState::class.java.classLoader)
            }
            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }


}
