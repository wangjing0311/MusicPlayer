package wangjing.musicplayer.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class StringUtils {

    private static final String TAG = "StringUtils";

    public static boolean isEmptyOrNull(final String str) {
        return str == null || str.trim().length() <= 0;
    }

    public static boolean isEqualCaseInsensitive(String s1, String s2) {
        return isEqual(s1, s2, true);
    }

    public static boolean isEqualCaseSensitive(String s1, String s2) {
        return isEqual(s1, s2, false);
    }

    private static boolean isEqual(String s1, String s2, boolean ignoreCase) {
        if (s1 != null && s2 != null) {
            if (ignoreCase) {
                return s1.equalsIgnoreCase(s2);
            } else {
                return s1.equals(s2);
            }
        } else {
            return ((s1 == null && s2 == null));
        }
    }

    public static String inputStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            }
        }
        return sb.toString();
    }

    public static String downloadSpeed(long current, long total) {
        if (total > 1048576) {
            double t = ((int) (total / 10486.76)) / 100.0;
            double c = ((int) (current / 10486.76)) / 100.0;
            if (c >= t)
                return t + "Mb/" + t + "Mb";
            else
                return c + "Mb/" + t + "Mb";
        } else {
            int t = (int) (total / 1024);
            int c = (int) (current / 1024);
            if (c >= t)
                return t + "Kb/" + t + "Kb";
            else
                return c + "Kb/" + t + "Kb";
        }
    }

    public static String getTimetoString(long timeDiff) {
        StringBuilder builder = new StringBuilder();
        timeDiff = timeDiff / 1000;
        int day = (int) timeDiff / 1440 / 60;
        int hour = (int) (timeDiff / 3600);
        int minute = (int) (timeDiff / 60 % 60);
        int second = (int) (timeDiff % 60);
        if (day > 0)
            builder.append(day).append("天");
        if (hour > 0)
            builder.append(hour).append("时");
        if (minute > 0)
            builder.append(minute).append("分");
        if (builder.length() == 0 || (day == 0 && hour == 0 && second != 0) || (day == 0 && hour == 0 && minute == 0)) {
            builder.append(second).append("秒");
        }
        return builder.toString();
    }

    /**
     * 替换所有的双引号字符
     *
     * @param text
     *
     * @return
     */
    public static String getRepDoubleQouteRes(String text) {
        return text == null ? null : text.replaceAll("\"", "&#34");
    }

    /**
     * 恢复所有的双引号字符
     *
     * @param repText
     *
     * @return
     */
    public static String getTextFromRep(String repText) {
        return repText == null ? null : repText.replaceAll("&#34", "\"");
    }

    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        int arraySize = array.length;
        int bufSize = arraySize == 0 ? 0 : ((array[0] == null ? 16 : array[0].toString().length()) + 1) * arraySize;
        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String format(String str, Object... params) {
        return String.format(Locale.getDefault(), str, params);
    }

    ////////////////////////////将阿拉伯数字转换为中文数字/////////////////////////
    public static String ToCH(int intInput) {
        String si = String.valueOf(intInput);
        String sd = "";
        if (si.length() == 1) // 個
        {
            sd += GetCH(intInput);
            return sd;
        } else if (si.length() == 2)// 十
        {
            if (si.substring(0, 1).equals("1"))
                sd += "十";
            else
                sd += (GetCH(intInput / 10) + "十");
            sd += ToCH(intInput % 10);
        } else if (si.length() == 3)// 百
        {
            sd += (GetCH(intInput / 100) + "百");
            if (String.valueOf(intInput % 100).length() < 2)
                sd += "零";
            sd += ToCH(intInput % 100);
        } else if (si.length() == 4)// 千
        {
            sd += (GetCH(intInput / 1000) + "千");
            if (String.valueOf(intInput % 1000).length() < 3)
                sd += "零";
            sd += ToCH(intInput % 1000);
        } else if (si.length() == 5)// 萬
        {
            sd += (GetCH(intInput / 10000) + "萬");
            if (String.valueOf(intInput % 10000).length() < 4)
                sd += "零";
            sd += ToCH(intInput % 10000);
        }

        return sd;
    }

    private static String GetCH(int input) {
        String sd = "";
        switch (input) {
            case 1:
                sd = "一";
                break;
            case 2:
                sd = "二";
                break;
            case 3:
                sd = "三";
                break;
            case 4:
                sd = "四";
                break;
            case 5:
                sd = "五";
                break;
            case 6:
                sd = "六";
                break;
            case 7:
                sd = "七";
                break;
            case 8:
                sd = "八";
                break;
            case 9:
                sd = "九";
                break;
            default:
                break;
        }
        return sd;
    }
}
