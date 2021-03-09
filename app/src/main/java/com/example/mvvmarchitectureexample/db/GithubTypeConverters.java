package com.example.mvvmarchitectureexample.db;

import androidx.room.TypeConverter;
import androidx.room.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class GithubTypeConverters {
    @TypeConverter
    public static List<Integer> stringToIntList(String string) {
        List<Integer> list;
        if(string == null) {
            list = Collections.emptyList();
        } else {
            list = StringUtil.splitToIntList(string);
        }

        return list;
    }

    @TypeConverter
    public static String intListToString(List<Integer> list) {
        return StringUtil.joinIntoString(list);
    }
}
