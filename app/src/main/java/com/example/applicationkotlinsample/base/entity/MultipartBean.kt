package cn.aihuaiedu.school.base.entity

import cn.aihuaiedu.school.base.RefreshController
import com.ReflectUtil
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by vicky on 2018.02.04.
 *
 * @Author vicky
 * @Date 2018年02月04日  14:40:26
 * @ClassName 请求提交基础类,分券property
 */
abstract class MultipartBean : RequestBody,Serializable {
    var pageIndex: Int? = 0
    var pageSize: Int? = 10

    constructor()
    constructor(pageIndex: Int, pageSize: Int) {
        this.pageIndex = pageIndex
        this.pageSize = pageSize
    }

    constructor(controller: RefreshController?) {
        val isRefresh = controller?.isRefreshFixable() ?: true
        this.pageIndex = if (isRefresh) 1 else (controller?.pageIndex ?: pageIndex ?: 0) + 1
        this.pageSize = (controller?.pageSize ?: pageSize ?: 10)
    }

    override fun contentType(): MediaType? {
        return null
    }

    override fun writeTo(sink: BufferedSink) {

    }

    companion object {
        private val type = object : TypeToken<Map<String, String>>() {

        }.type

        private val gsonSkipper = GsonSkipper()

        private class GsonSkipper {

            fun toMap(any: MultipartBean, clazz: Class<*>): Map<String, String> {
                val gson = GsonBuilder()//.setPrettyPrinting()
                    .enableComplexMapKeySerialization()
                    .disableInnerClassSerialization()
                    .addSerializationExclusionStrategy(CustomExclusionStrategy(any)).create()
                return gson.fromJson<Map<String, String>>(gson.toJson(any, clazz), type)
            }

            fun toJson(any: MultipartBean, clazz: Class<*>): String {
                val gson = GsonBuilder()//.setPrettyPrinting()
                    .enableComplexMapKeySerialization()
                    .addSerializationExclusionStrategy(CustomExclusionStrategy(any)).create()
                return gson.toJson(any, clazz)
            }

            fun toJson(any: MultipartBean, map: Map<String, String>): String {
                val gson = GsonBuilder()//.setPrettyPrinting()
                    .enableComplexMapKeySerialization()//.disableInnerClassSerialization()
                    .addSerializationExclusionStrategy(CustomExclusionStrategy(any)).create()
                return gson.toJson(map)
            }

        }

        private class CustomExclusionStrategy(private val any: MultipartBean) : ExclusionStrategy {

            override fun shouldSkipClass(classname: Class<*>): Boolean {
                return classname == RefreshController::class.java
            }

            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                if (f == null) return true
                if (f.name == "pageIndex" || f.name == "pageSize") {
                    if (any.pageIndex == 0)
                        return true
                }
                if (f.name == "itemMutableType") {
                    return true
                }

                val dc = f.declaringClass
                val field = ReflectUtil.getFiled(FieldAttributes::class.java, "field", f) as? Field
                if (field != null) {
                    val declaredMethod: Method? = try {
                        dc.getDeclaredMethod("fieldExclusion", String::class.java)
                    } catch (e: Exception) {
                        null
                    }

                    if (declaredMethod != null) {
                        declaredMethod.isAccessible = true
                        val result = (declaredMethod.invoke(field, f.name) as? Boolean) ?: false
                        if (result) {
                            return true
                        }
                    }
                }

                return f.name == "controller"
            }

        }
    }

    /**
     * 将所有属性转换为map
     * retrofit表单提交使用
     */
    fun toMap(): Map<String, String> {
        return gsonSkipper.toMap(this, this.javaClass)
    }

    fun toJsonRequestBody(): RequestBody {
        return toJson().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun toJson(): String {
        return gsonSkipper.toJson(this, this.javaClass)
    }

    fun toMapJson(): String {
        return gsonSkipper.toJson(this, toMap())
    }
}