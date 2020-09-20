package com.example.applicationkotlinsample.base.http

import com.example.applicationkotlinsample.Constant


interface HttpService {
    companion object {
        //网络请求基础路径
        val BASE_URL = Constant.BASE_URL
    }

}