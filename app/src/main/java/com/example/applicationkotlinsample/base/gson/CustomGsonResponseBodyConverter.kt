package cn.aihuaiedu.school.base.gson

import cn.aihuaiedu.school.base.entity.BaseInfo
import cn.aihuaiedu.school.base.entity.ListBaseInfo
import com.example.applicationkotlinsample.base.http.HttpService
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.StringUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


internal class CustomGsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<out T>,
    private val retrofit: Retrofit?
) : Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T {
        var response = value.string()
        Debuger.print("==========$response============")

        val jsonObject = JSONObject(response)
//        val code = jsonObject.getString("code").toIntOrNull()
//        jsonObject.put("code", code)
        response = jsonObject.toString()
        val contentType = value.contentType()
        value.close()
        val httpStatus = gson.fromJson(response, HttpStatus::class.java)
        var contain = true
        if (retrofit != null) {
            contain = HttpService.BASE_URL.contains(retrofit.baseUrl().host)
        }
        if (contain)
            if (httpStatus.isCodeInvalid) {
                throw ApiException(httpStatus.code, httpStatus.msg ?: "", response)
            }
        val charset =
            if (contentType != null) contentType.charset(Charset.forName("utf-8")) else Charset.forName(
                "utf-8"
            )
        val inputStream = ByteArrayInputStream(response.toByteArray())
        val reader = InputStreamReader(inputStream, charset!!)
        val jsonReader = gson.newJsonReader(reader)
        var converter: T?=null
        try {
            converter = adapter.read(jsonReader)
        } catch (e: Exception) {
            try {
                var baseInfo = BaseInfo.BaseInfoImpl()
                baseInfo.errorCode = jsonObject.getString("errorCode")
                baseInfo.data = null
                baseInfo.errorMsg = jsonObject.getString("errorMsg")
                converter = baseInfo as T
            }catch (e:Exception){
                var baseInfo = ListBaseInfo.ListBaseInfoImpl()
                baseInfo.errorCode = jsonObject.getString("errorCode")
                baseInfo.data = null
                baseInfo.errorMsg = jsonObject.getString("message")
                converter = baseInfo as T
            }
        }
//        try {
//            converter= adapter.read(jsonReader)
//        }catch (e:Exception){
//            converter= str2Object(response)!! as T
//        }

        jsonReader.close()
        reader.close()
        inputStream.close()

        return converter!!
    }

    /**
     * 转换类型
     *
     * @param s
     * @param clz
     * @return
     */
    fun <T> str2Object(jsonStr: String?): BaseInfo<T>? {
        if (StringUtils.isEmpty(jsonStr)) {
            return null
        }
        val resultType = object : TypeToken<BaseInfo<T>>() {}.type
        val fromJson = gson.fromJson<BaseInfo<T>>(jsonStr, resultType)
        return fromJson
    }
}
