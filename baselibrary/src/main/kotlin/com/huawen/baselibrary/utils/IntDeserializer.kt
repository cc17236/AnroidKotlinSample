package com.huawen.baselibrary.utils

import com.google.gson.*
import java.lang.reflect.Type

class IntDeserializer: JsonDeserializer<Int> {
    override fun deserialize(
        src: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Int {
        if (src is JsonNull) {
            return 0
        } else if (src is JsonPrimitive) {
            return if (src.isString){
                src.asString.toIntOrNull()?:0
            }else
                src.asInt
        } else if (src is JsonObject) {
            return 0
        } else if (src is JsonArray) {
            return 0
        } else
            return src?.asInt?:0
    }
}