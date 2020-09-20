package com.huawen.baselibrary.utils

import com.google.gson.*
import java.lang.reflect.Type

class ShortDeserializer: JsonDeserializer<Short> {
    override fun deserialize(
        src: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Short {
        if (src is JsonNull) {
            return 0
        } else if (src is JsonPrimitive) {
            return if (src.isString){
                src.asString.toShortOrNull()?:0
            }else
            return src.asShort
        } else if (src is JsonObject) {
            return 0
        } else if (src is JsonArray) {
            return 0
        } else
            return src?.asShort?:0
    }
}