package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

//@Entity (foreignKeys =  {
//        @ForeignKey(entity = UserRecord.class, parentColumns = "userName", childColumns = "userId"),
//        @ForeignKey(entity = Event.class, parentColumns = "eventId", childColumns = "eventId")
//})

@Entity
public class EventLike implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String eventId;
    @ColumnInfo (name = "userId")
    private String userId;
    @ColumnInfo
    private String genreId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }
}
