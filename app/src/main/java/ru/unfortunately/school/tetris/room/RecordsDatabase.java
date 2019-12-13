package ru.unfortunately.school.tetris.room;

import androidx.room.Database;

@Database(entities = {Record.class}, version = 1)
public abstract class RecordsDatabase {

    public abstract RecordsDatabase getRecordsDao();

}
