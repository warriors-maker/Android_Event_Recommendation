package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ToDoItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    public String UserName;
    @ColumnInfo
    public String eventId;
    @ColumnInfo
    public int priority;
}
