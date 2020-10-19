package com.example.applicationkotlinsample.mvp.contract

import cn.aihuaiedu.school.base.BaseContract

interface TestSampleContract {
    interface View : BaseContract.BaseView {
    }

    interface Presenter : BaseContract.BasePresenter<View> {
     fun   getTestData()
    }
}