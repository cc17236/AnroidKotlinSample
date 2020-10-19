package cn.aihuaiedu.school.base.entity

import com.huawen.baselibrary.adapter.entity.MultiItemEntity
import java.io.Serializable

/**
 * Created by vicky on 2018.01.30.
 *
 * @Author vicky
 * @Date 2018年01月30日  13:39:10
 * @ClassName 请求相应基础类外层封装,状态property
 */
abstract class BaseInfo<T> : Serializable, MultiItemEntity {

    open var errorMsg: String? = null

    open var errorCode: String? = null

    open var ok: Boolean = true

    open var data: T? = null

    class BaseInfoImpl : BaseInfo<BaseInfoImpl>()
}