package ru.unfortunately.school.tetris.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface RecordsDao {

    @Query("SELECT * FROM Record WHERE 1 ORDER BY score DESC")
    List<Record> getAllRecords();

    @Insert
    void addRecord(Record record);

    @Query("DELETE FROM Record WHERE 1")
    void removeAll();
}
