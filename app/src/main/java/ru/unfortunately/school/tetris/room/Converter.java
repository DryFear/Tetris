package ru.unfortunately.school.tetris.room;

import java.util.Date;

import androidx.room.TypeConverter;

public class Converter {

    @TypeConverter
    public static long fromDate(Date date){
        return date.getTime();
    }

    @TypeConverter
    public static Date toDate(long time){
        return new Date(time);
    }
}
