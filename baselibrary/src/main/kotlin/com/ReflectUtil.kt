package com

import android.os.Build
import com.huawen.baselibrary.utils.Debuger
import java.lang.reflect.*
import java.util.*


/**
 * Created by qiulinmin on 2017/3/7.
 */
object ReflectUtil {

    @JvmStatic
    fun updateFinalModifiers(obj: Any, field: Field, newValue: Any?) {
        val fieldClass = Field::class.java
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val modifiersField = fieldClass.getDeclaredField("accessFlags")
                modifiersField.isAccessible = true
                val old = modifiersField.getInt(field)
                modifiersField.set(field, field.modifiers and Modifier.FINAL.inv())
                field.isAccessible = true
                field.set(obj, newValue)
                modifiersField.set(field, old)
            } else {
                field.isAccessible = true
                field.set(obj, newValue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Debuger.print("${e.message}")
        }
//
//        field.isAccessible = true
//        val modifiersField = Field::class.java.getDeclaredField("accessFlags")
////        val modifiersField = Field::class.java.getDeclaredField("modifiers")
//        modifiersField.isAccessible = true
//        val old=modifiersField.getInt(field)
//        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
//        field.set(obj, newValue)
//        modifiersField.setInt(field, old)
    }
    @JvmStatic
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun setStaticFiled(type: Class<*>, filedName: String, value: Any?) {
        setFiled(type, filedName, null, value)
    }
    @JvmStatic
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun setFiled(type: Class<*>, filedName: String, `object`: Any?, value: Any?) {
        val declaredField = type.getDeclaredField(filedName)
        declaredField.isAccessible = true
        declaredField.set(`object`, value)
    }
    @JvmStatic
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun getStaticClassField(type: Class<*>, filedName: String, filedRealName: String): Any? {
        val innerClazz = type.declaredClasses
        for (cls in innerClazz) {
            val mod = cls.modifiers
            val modifier = Modifier.toString(mod)
            if (modifier.contains("static") && cls.simpleName == filedName) {
                //构造静态内部类实例
                return getStaticFiled(cls, filedRealName)
            }
        }
        return null
    }
    @JvmStatic
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun getStaticFiled(type: Class<*>, filedName: String): Any {
        return getFiled(type, filedName, null)
    }
    @JvmStatic
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun getFiled(type: Class<*>, filedName: String, `object`: Any?): Any {
        val declaredField = type.getDeclaredField(filedName)
        declaredField.isAccessible = true
        return declaredField.get(`object`)
    }
    @JvmStatic
    @Throws(NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun invokeStaticMethod(type: Class<*>, method: String, paramsTypes: Array<Class<*>>, args: Array<Any>): Any {
        return invokeMethod(type, method, paramsTypes, null, args)
    }
    @JvmStatic
    @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    fun invokeMethod(type: Class<*>, method: String, paramsTypes: Array<Class<*>>, receiver: Any?, args: Array<Any>): Any {
        val declaredMethod = type.getDeclaredMethod(method, *paramsTypes)
        declaredMethod.isAccessible = true
        return declaredMethod.invoke(receiver, *args)
    }

    @JvmStatic
    fun makeProxy(loader: ClassLoader, base: Class<*>, handler: InvocationHandler): Any {
        val interfaces = getAllInterfaces(base)
        val ifs = if (interfaces != null && interfaces.size > 0) interfaces.toTypedArray() else arrayOfNulls<Class<*>>(0)
        return Proxy.newProxyInstance(loader, ifs, handler)
    }
    @JvmStatic
    fun makeProxy(loader: ClassLoader, ifs: Array<Class<*>>, handler: InvocationHandler): Any {
        return Proxy.newProxyInstance(loader, ifs, handler)
    }
    @JvmStatic
    fun getAllInterfaces(cls: Class<*>?): List<Class<*>>? {
        if (cls == null) {
            return null
        }
        val interfacesFound = LinkedHashSet<Class<*>>()
        getAllInterfaces(cls, interfacesFound)
        return ArrayList(interfacesFound)
    }

    @JvmStatic
    private fun getAllInterfaces(cls: Class<*>?, interfacesFound: HashSet<Class<*>>) {
        var cls = cls
        while (cls != null) {
            val interfaces = cls.interfaces
            for (i in interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound)
                }
            }
            cls = cls.superclass
        }
    }

}
