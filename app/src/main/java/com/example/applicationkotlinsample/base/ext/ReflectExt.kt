package cn.aihuaiedu.school.base.ext

import cn.aihuaiedu.school.base.ext.ReflectExt.getReflectExtTopClass
import com.huawen.baselibrary.adapter.entity.MultiItemEntity

fun Class<*>.topMultiItemEntitySuperClass(): Class<MultiItemEntity>? {
    return getReflectExtTopClass(clazz = this)
}

object ReflectExt {
    fun getReflectExtTopClass(clazz: Class<*>): Class<MultiItemEntity>? {
        if (clazz.isInterface && clazz.canonicalName=="${MultiItemEntity::class.java.canonicalName}") return clazz as? Class<MultiItemEntity>
        val su = clazz.superclass
        if (su?.canonicalName=="${MultiItemEntity::class.java.canonicalName}") {
            return getReflectExtTopClass(su)
        } else {
            return clazz  as? Class<MultiItemEntity>
        }
    }
}
