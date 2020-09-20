package com.huawen.baselibrary.utils

import com.google.gson.*
import java.lang.reflect.Type

class FloatDeserializer: JsonDeserializer<Float> {
    override fun deserialize(
        src: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Float {
        if (src is JsonNull) {
            return 0f
        } else if (src is JsonPrimitive) {
            return if (src.isString){
                src.asString.toFloatOrNull()?:0f
            }else
            return src.asFloat
        } else if (src is JsonObject) {
            return 0f
        } else if (src is JsonArray) {
            return 0f
        } else
            return src?.asFloat?:0f
    }
}