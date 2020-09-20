package cn.aihuaiedu.school.base.http

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.bind.ArrayTypeAdapter
import com.google.gson.reflect.TypeToken


class NullStringToEmptyAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        try {
            val rawType = type?.rawType as? Class<T>
            return if (rawType != String::class.java) {
                null
            } else StringAdapter(ArrayTypeAdapter.FACTORY.create(gson, type)) as? TypeAdapter<T>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
