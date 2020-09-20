package cn.aihuaiedu.school.base.entity

import androidx.room.Ignore
import com.huawen.baselibrary.adapter.entity.MultiItemEntity
import java.io.Serializable

/**
 * Created by vicky on 2018.01.31.
 *
 * @Author vicky
 * @Date 2018年01月31日  15:53:46
 * @ClassName 请求相应body基础类
 */
abstract class BaseBody : Serializable, MultiItemEntity {
    @Ignore
    var itemMutableType: Int = 0

    override fun fieldExclusion(fieldName: String): Boolean {
        if (fieldName == "itemMutableType") return true
        return false
    }

    override fun itemType(): Int {
        return itemMutableType
    }
}