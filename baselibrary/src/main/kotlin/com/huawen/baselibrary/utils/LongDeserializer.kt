package com.huawen.baselibrary.utils

import com.google.gson.*
import java.lang.reflect.Type

class LongDeserializer: JsonDeserializer<Long> {
    override fun deserialize(
        src: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long {
        if (src is JsonNull) {
            return 0
        } else if (src is JsonPrimitive) {
            return if (src.isString){
                src.asString.toLongOrNull()?:0
            }else
            return src.asLong
        } else if (src is JsonObject) {
            return 0
        } else if (src is JsonArray) {
            return 0
        } else
            return src?.asLong?:0
    }
}