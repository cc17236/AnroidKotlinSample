package cn.aihuaiedu.school.base.gson

import com.google.gson.Gson
import com.google.gson.TypeAdapter

import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset

import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter

import okhttp3.MediaType.Companion.toMediaTypeOrNull
//CustomGsonRequestBodyConverter.java
internal class CustomGsonRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    companion object {

        private val MEDIA_TYPE ="application/json; charset=UTF-8".toMediaTypeOrNull()
        private val UTF_8 = Charset.forName("UTF-8")
    }
}
