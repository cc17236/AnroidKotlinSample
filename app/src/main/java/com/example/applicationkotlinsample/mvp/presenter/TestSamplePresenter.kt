package com.example.applicationkotlinsample.mvp.presenter

import cn.aihuaiedu.school.base.ObserverImp
import cn.aihuaiedu.school.base.RxPresenter
import cn.aihuaiedu.school.base.ext.makeCustomerIdMap
import cn.aihuaiedu.school.base.http.requestApi
import com.example.applicationkotlinsample.communication.response.TestBodyResp
import com.example.applicationkotlinsample.mvp.contract.TestSampleContract
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.ToastUtils

class TestSamplePresenter : RxPresenter<TestSampleContract.View>(), TestSampleContract.Presenter {


    override fun getTestData() {
        requestApi({
            it?.getTestData(
                makeCustomerIdMap()
            )
        },  object : ObserverImp<TestBodyResp>() {
            override fun onErr(errCode: Int, errMsg: String) {
                ToastUtils.showShort(errMsg)
                mView?.disMissLoading()
            }

            override fun doNext(model: TestBodyResp) {
//                model.data?.forEach {
//                    Debuger.print("====AAAAAAAAA==========$it========")
//                }
                mView?.disMissLoading()
//                mView?.getExchangeListFinish(model)

            }
        })
    }

}