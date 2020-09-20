package com.example.applicationkotlinsample.utils;

public class NumToChineseUtils {
    private static String[] units = { "", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千", "万亿" };
    private static char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };

    public static String formatInteger(int num) {

        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        title:
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                boolean falg=false;
                for(int j=i;j<len;j++){
                    String a = val[j] + "";
                    int b = Integer.valueOf(a);
                    if(b!=0){
                        falg=true;
                    }
                }
                if(falg){
                    if ('0' == val[i - 1]) {
                        continue;
                    }else {
                        if(((len - 1) - i)%4==0) {
                            sb.append(unit);
                        }
                        sb.append(numArray[n]);
                    }
                }else {
                    if(((len - 1) - i)%4==0){
                        sb.append(unit);
                        break title;
                    }
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);

            }
        }
        return sb.toString();
    }
}
