package com.huawen.baselibrary.utils.utils

import java.math.BigInteger

object ColorUtils {
    fun getMiddleColor(color1: String, color2: String): String {
        if (color1.contains("#") && color2.contains("#") && color1.length == color2.length &&
                (color2.length == 7 || color2.length == 9)
        ) {
            val tempColor1 = color1.replace("#", "")
            val tempColor2 = color2.replace("#", "")
            val stringBuffer = StringBuffer();
            stringBuffer.append("#");
            for (i in 0 until tempColor1.length) {
                val tempResult = ((changeHex2Int(tempColor1.elementAt(i).toString())
                        + changeHex2Int(tempColor2.elementAt(i).toString())) / 2).toString()
                stringBuffer.append(changeInt2Hex(tempResult))
            }
            return stringBuffer.toString()
        }
        return ""
    }

    private fun changeHex2Int(temp: String): Int {
        val srch = BigInteger(temp, 16)
        return Integer.valueOf(srch.toString())
    }

    private fun changeInt2Hex(temp: String): String {
        val srch = BigInteger(temp, 10)
        return Integer.toHexString(Integer.parseInt(srch.toString()))
    }
}