package cn.aihuaiedu.school.base.context

import cn.aihuaiedu.school.base.BaseContract
import com.huawen.baselibrary.schedule.host.BaseSplashActivityHost


/**
 * @作者: #Administrator #
 *@日期: #2018/4/29 #
 *@时间: #2018年04月29日 20:38 #
 *@File:Kotlin Class
 */
/**
 * 启动页,因启动application后需要时间加载第三方库和数据集,当前activity屏蔽了application.delegate的调用方式,只允许application.unsafedelegate
 * 推荐只用来获取上下文
 * 需要在application的继承类中开启,开启后直接调用delegate将抛出异常
 */
abstract class BaseSplashActivity<in V : BaseContract.BaseView, P : BaseContract.BasePresenter<V>> : BaseActivity<V, P>(), BaseSplashActivityHost {
}