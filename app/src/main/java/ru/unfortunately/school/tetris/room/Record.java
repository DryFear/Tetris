package ru.unfortunately.school.tetris.room;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "nickname")
    private String mNickname;

    @ColumnInfo(name = "score")
    private int mScore;

    @ColumnInfo(name = "record_date")
    private Date mDate;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }


}
