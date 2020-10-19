package cn.aihuaiedu.school.base.entity

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.huawen.baselibrary.adapter.entity.MultiItemEntity
import java.io.Serializable

/**
 * Created by vicky on 2018.01.30.
 *
 * @Author vicky
 * @Date 2018年01月30日  13:39:10
 * @ClassName 请求相应基础类外层封装,状态property
 */
abstract class BaseStruct<T> : Serializable, MultiItemEntity {

    @SerializedName("message")
    open var errorMsg: String? = null
        get() {
            if (field == null) {
                return "未知异常"
            }
            return field
        }
    @SerializedName("code")
    open var errorCode: String? = null

    open var ok: Boolean = true
    @Ignore
    private var assigned = false


    @Ignore
    @SerializedName("body")
    protected var dataInternal: T? = null
        get() {
            if (field != null) {
                if (!assigned) {
                    assigned = true
                }
            }
            return field
        }

    fun setData(data: T?) {
        if (assigned) {
            //不允许重新赋值
            return
        }
        //gson
        this.dataInternal = data
    }

   open fun getData(): T? {
        return this.dataInternal
    }

    @Ignore
    open var date: Long? = null
    open var status: String? = null

    @Ignore
    final override fun itemType(): Int {
        return super.itemType()
    }

    @Ignore
    fun state(): Boolean {
                return ok
    }

    class BaseStructImpl : BaseStruct<BaseStructImpl>()
}