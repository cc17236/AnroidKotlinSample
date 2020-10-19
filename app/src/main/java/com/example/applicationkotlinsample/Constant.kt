package com.example.applicationkotlinsample

object Constant {
    val TEXT_ITYPE_CODING = if (BuildConfig.DEBUG) {
        true
    } else {
        true
    }
    const val isUAT: Boolean = false
    //测试环境
    val BASE_URL ="https://www.wanandroid.com/"
    //生产环境加密
    const val TEXT_ITYPE_VALUE = "text/aihua+;charset=UTF-8"
    //测试或UAT环境不加密
    const val TEXT_ITYPE_UNENCRYPT_VALUE = "text/aihua-;charset=UTF-8"

    const val SIGN_KEY="ah-edu-server@2020"
    //=====================环境配置 begin===============================
    //url

    //设置请求头
    val BASE_TEXT_ITYPE_URL = if (BuildConfig.DEBUG) {
        TEXT_ITYPE_VALUE
    } else {
        TEXT_ITYPE_VALUE
    }
    //UAT环境
    const val KEY_TYPE="key_type"
    const val KEY_ID="key_id"
    const val KEY_MODE="key_mode"
}