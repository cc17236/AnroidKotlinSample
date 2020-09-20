package cn.aihuaiedu.school.base.http


import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

class StringAdapter(private val adapter: TypeAdapter<*>?) : TypeAdapter<String>() {


    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: String?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): String {
        var str = ""
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
        } else {
            if (`in`.hasNext()) {
                val peek = `in`.peek()
                when (peek) {
                    JsonToken.STRING -> {

                    }
                }
                str = `in`.nextString()

//                try {
//                } catch (e: Exception) {
//                    if (peek == JsonToken.BEGIN_ARRAY) {
//                        val list = ArrayList<String>()
//                        `in`.beginArray()
//                        while (`in`.hasNext()) {
//                            val instance = componentTypeAdapter.read(`in`)
//                            list.add(instance)
//                        }
//                        `in`.endArray()
//                        val array = Array.newInstance(componentType, list.size)
//                        for (i in list.indices) {
//                            Array.set(array, i, list[i])
//                        }
//                        str = array.toString()///ReflectUtil.invokeMethod(JsonReader::class.java, "nextQuotedValue", arrayOf(Char::class.java), `in`, arrayOf('\'')) as? String).toString()
//                    }
//                }
            }
        }
        return str
    }
}