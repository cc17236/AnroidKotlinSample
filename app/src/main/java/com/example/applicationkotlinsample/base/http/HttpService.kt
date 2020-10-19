package com.example.applicationkotlinsample.base.http

import com.example.applicationkotlinsample.Constant
import com.example.applicationkotlinsample.communication.response.TestBodyResp
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface HttpService {
    companion object {
        //网络请求基础路径
        val BASE_URL = Constant.BASE_URL
    }

    //测试接口
    @FormUrlEncoded
    @POST("wxarticle/chapters/json")
    fun getTestData(@FieldMap bean: Map<String, String>): Observable<TestBodyResp>
}