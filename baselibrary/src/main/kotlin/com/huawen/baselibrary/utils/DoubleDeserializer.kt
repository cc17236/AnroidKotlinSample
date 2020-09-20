package com.huawen.baselibrary.utils

import com.google.gson.*
import java.lang.reflect.Type

class DoubleDeserializer: JsonDeserializer<Double> {
    override fun deserialize(
        src: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Double {
        if (src is JsonNull) {
            return 0.0
        } else if (src is JsonPrimitive) {
            return if (src.isString){
                src.asString.toDoubleOrNull()?:0.0
            }else
            return src.asDouble
        } else if (src is JsonObject) {
            return 0.0
        } else if (src is JsonArray) {
            return 0.0
        } else
            return src?.asDouble?:0.0
    }
}