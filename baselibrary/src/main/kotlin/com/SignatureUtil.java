package com;

import com.huawen.baselibrary.utils.Debuger;

public class SignatureUtil {
    static char[] a_field = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9'};

    public static int indexOf(char element) {
        int i = 0;
        for (char a : a_field) {
            if (element == a) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static void main(String[] args) {
        //SHA1
        String sig = "6DC2522AE83320666EB8EA9A0C024AB9BCFEF789";
        StringBuilder insig = new StringBuilder();
        insig.append("{");
        for (int i = 0; i < sig.length(); i++) {
            char cha = sig.charAt(i);
            int idx = indexOf(cha);
            if (i != 0)
                insig.append(",");
            insig.append(idx);
        }
        insig.append("}");
//        Debuger.INSTANCE.print("insig = [" + insig.toString() + "]");
        System.out.println("insig = [" + insig.toString() + "]");
    }
}
