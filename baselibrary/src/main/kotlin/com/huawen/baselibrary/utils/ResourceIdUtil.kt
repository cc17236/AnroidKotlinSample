package com.huawen.baselibrary.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import org.jetbrains.anko.find

/**
 * Created by vicky on 2018.01.30.
 *
 * @Author vicky
 * @Date 2018年01月30日  12:03:38
 * @ClassName 这里输入你的类名(或用途)
 */
object ResourceIdUtil {
    var focusLast = ""

    fun <T : Activity> findFocus(`object`: T): String? {
        try {
            val decorView = `object`.window.decorView
            val focus = decorView.findFocus() ?: return null
            val focusId = focus.id
            if (focusLast != `object`.resources.getResourceName(focusId)) {
                focusLast = `object`.resources.getResourceName(focusId)
            }
            return getResourceName(`object`, focusId)
        } catch (e: Exception) {
        }

        return null
    }


    fun <T:Activity>focusEquals(`object`:T,view:View):Boolean{
        try {
            val id = findFocus(`object`)
            if (!TextUtils.isEmpty(id)) {
                val focusView=`object`.find<View>(ResourceIdUtil.getId(`object`, id!!))
                if (focusView==view){
                    return true
                }
            }
        } catch (ignored: Exception) {
        }
        return false
    }

    fun <T : Activity> hideFocusKeyboard(`object`: T) {
        try {
            val id = findFocus(`object`)
            if (!TextUtils.isEmpty(id)) {
                var imm: InputMethodManager? = `object`.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(`object`.find<View>(ResourceIdUtil.getId(`object`, id!!)).windowToken, 0)
                imm = null
            }
        } catch (ignored: Exception) {
        }
    }

    fun <T : Activity> eraseFocus(`object`: T) {
        try {
            val id = findFocus(`object`)
            if (!TextUtils.isEmpty(id)) {
                val focus = `object`.findViewById<View>(ResourceIdUtil.getId(`object`, id!!))
                focus?.clearFocus()
            }
        } catch (ignored: Exception) {
        }
    }


    fun <T : Activity> getId(`object`: T, name: String): Int {
        try {
            return `object`.resources.getIdentifier(name, "id", `object`.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }


    fun <T : Fragment> getId(`object`: T, name: String): Int {
        try {
            return `object`.resources.getIdentifier(name, "id", `object`.requireContext().packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }


    fun <T : Activity> getMipmap(`object`: T, name: String): Int {
        try {
            return `object`.resources.getIdentifier(name, "mipmap", `object`.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
    fun <T : Activity> getDrawable(`object`: T, name: String): Int {
        try {
            return `object`.resources.getIdentifier(name, "drawable", `object`.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }


    fun <T : Dialog> findFocus(`object`: T): String? {
        try {
            val decorView = `object`.window!!.decorView
            val focus = decorView.findFocus()
            val focusId = focus.id
            return getResourceName(`object`.context, focusId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getResourceName(context: Context, resId: Int): String {
        return context.resources.getResourceName(resId)
    }

}