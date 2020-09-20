package com.huawen.baselibrary.utils

import com.google.gson.*
import java.lang.reflect.Type


/**
 * @作者: #Administrator #
 *@日期: #2018/10/24 #
 *@时间: #2018年10月24日 17:18 #
 *@File:Kotlin Class
 */
class StringDeserializer : JsonDeserializer<String> {
    @Throws(JsonParseException::class)
    override fun deserialize(src: JsonElement, srcType: Type,
                             context: JsonDeserializationContext): String? {
        if (src is JsonNull) {
            return ""
        } else if (src is JsonPrimitive) {
            return src.asString
        } else if (src is JsonObject) {
            return src.toString()
        } else if (src is JsonArray) {
            return src.toString()
        } else
            return src.asString
    }

}