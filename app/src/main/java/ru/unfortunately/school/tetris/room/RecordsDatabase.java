package ru.unfortunately.school.tetris.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Record.class}, version = 2)
@TypeConverters({Converter.class})
public abstract class RecordsDatabase extends RoomDatabase {

    public abstract RecordsDao getRecordsDao();



}
