package com.example.myapplication.Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeUtils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static long parseToMillis(String datetime) {
        try {
            return sdf.parse(datetime).getTime();
        } catch (ParseException e) {
            return Long.MAX_VALUE; // 解析失败视为未结束
        }
    }

}
