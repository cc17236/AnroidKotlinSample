package com.huawen.baselibrary.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/25
 * desc  : 剪贴板相关工具类
</pre> *
 */
class ClipboardUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 复制文本到剪贴板
         *
         * @param text 文本
         */
        fun copyText(text: CharSequence) {
            val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("text", text))
        }

        /**
         * 获取剪贴板的文本
         *
         * @return 剪贴板的文本
         */
        val text: CharSequence?
            get() {
                val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                return if (clip != null && clip.itemCount > 0) {
                    clip.getItemAt(0).coerceToText(Utils.getContext())
                } else null
            }

        /**
         * 复制uri到剪贴板
         *
         * @param uri uri
         */
        fun copyUri(uri: Uri) {
            val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newUri(Utils.getContext().contentResolver, "uri", uri))
        }

        /**
         * 获取剪贴板的uri
         *
         * @return 剪贴板的uri
         */
        val uri: Uri?
            get() {
                val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                return if (clip != null && clip.itemCount > 0) {
                    clip.getItemAt(0).uri
                } else null
            }

        /**
         * 复制意图到剪贴板
         *
         * @param intent 意图
         */
        fun copyIntent(intent: Intent) {
            val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newIntent("intent", intent))
        }

        /**
         * 获取剪贴板的意图
         *
         * @return 剪贴板的意图
         */
        val intent: Intent?
            get() {
                val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                return if (clip != null && clip.itemCount > 0) {
                    clip.getItemAt(0).intent
                } else null
            }
    }
}
