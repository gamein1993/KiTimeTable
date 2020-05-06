package com.kiprogram.kitimetable.util;

public class KiUtilities {
    /**
     * 文字列が０文字かどうか。先頭、最後の半角空白は取り除きます。
     * @param cs 文字列
     * @return ０文字の場合 true
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.toString().trim().length() == 0;
    }
}
