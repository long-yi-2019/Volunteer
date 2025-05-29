package com.example.myapplication.Entity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeValidator {
    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final SimpleDateFormat sdf =
            new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

    static {
        sdf.setLenient(false); // 严格模式，禁止自动转换错误日期
    }

    public static boolean isValidFormat(String timeStr) {
        try {
            sdf.parse(timeStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}