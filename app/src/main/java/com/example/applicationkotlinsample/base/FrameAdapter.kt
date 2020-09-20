package cn.aihuaiedu.school.base

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.huawen.baselibrary.views.BaseFrameAdapter

/**
 * Created by you on 15/8/31.
 * 即保存Fragment对象引用 ,切换时不调用onCreateView,onActivityCreated又保存了fragment的state(Fragment.SaveState)
 * 内存不足时的意外回收也保存了(Fragment.SaveState)
 */
abstract class FrameAdapter(mFragmentManager: FragmentManager) : BaseFrameAdapter(mFragmentManager) {
    private val mFragments = arrayListOf<Fragment?>()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return true
    }

    override fun instantFragment(fm: FrameLayout, position: Int) {
        if (this.mCurrentPosition == position) {
            if (this.mCurrentFragment == getItem(position))
                return
        }
        var ft: FragmentTransaction? = mManager.beginTransaction()
        if (mCurrentFragment != null) {
            ft?.hide(mCurrentFragment!!)
            mCurrentFragment?.setMenuVisibility(false)
            mCurrentFragment?.userVisibleHint = false
        }
        val itemId = getItemId(position)
        val name = makeFragmentName(fm.id, itemId)
        var fragment: Fragment? = mManager.findFragmentByTag(name)
        if (fragment != null) {
            ft?.show(fragment)
        } else {
            fragment = getItem(position)
            while (mFragments.size <= position) {
                mFragments.add(null)
            }
            mFragments[position] = fragment
            if (fragment != null){
//                if(ft?.)
                ft?.add(fm.id, fragment, name)
            }
//            ft?.show(fragment)
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
        selectFrame(position)
    }

    open fun selectFrame(position: Int) {

    }

    override fun frameTitle(position: Int): CharSequence? {
        return null
    }

    override fun saveState(): Parcelable? {
        val state = Bundle()
        state.putInt("currentPosition", mCurrentPosition)
        for (i in mFragments.indices) {
            val f = mFragments[i]
            if (f != null) {
                val key = KEY + i
                mManager.putFragment(state, key, f)
            }
        }
        return state
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state != null) {
            val bundle = state as Bundle?
            mCurrentPosition = bundle?.getInt("currentPosition") ?: 0
            val keys = bundle?.keySet()
            if (keys != null)
                for (key in keys) {
                    if (key.startsWith(KEY)) {
                        val index = Integer.parseInt(key.substring(KEY.length))
                        val f = mManager.getFragment(bundle, key)
                        if (f != null) {
                            while (mFragments.size <= index) {
                                mFragments.add(null)
                            }
                            f.setMenuVisibility(false)
                            mFragments[index] = f
                        }
                    }
                }
            var ft: FragmentTransaction? = mManager.beginTransaction()
            for (i in mFragments.indices) {
                val f = mFragments[i]
                if (f != null) {
                    if (i == mCurrentPosition) {
                        mCurrentFragment = f
                    } else {
                        ft?.hide(f)
                        f.setMenuVisibility(false)
                        f.userVisibleHint = false
                    }
                }
            }
//            ft?.commit()
            ft?.commitAllowingStateLoss()
            ft = null
            mManager.executePendingTransactions()
        }
    }

    companion object {

        private val KEY = "FrameAdapter"
    }

}