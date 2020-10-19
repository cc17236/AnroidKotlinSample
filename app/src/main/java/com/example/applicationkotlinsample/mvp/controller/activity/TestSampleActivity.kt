package com.example.applicationkotlinsample.mvp.controller.activity

import cn.aihuaiedu.school.base.context.BaseActivity
import com.example.applicationkotlinsample.R
import com.example.applicationkotlinsample.mvp.contract.TestSampleContract
import com.example.applicationkotlinsample.mvp.presenter.TestSamplePresenter

class TestSampleActivity : BaseActivity<TestSampleContract.View, TestSampleContract.Presenter>(),
    TestSampleContract.View {
    override fun getLayoutId()= R.layout.act_test

    override fun initPresenter()=TestSamplePresenter()

    override fun configView() {
    }

    override fun initData() {
        mPresenter?.getTestData()
    }

}
