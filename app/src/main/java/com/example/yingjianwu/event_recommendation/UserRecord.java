package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserRecord implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo (name = "userName")
    private String userName;
    @ColumnInfo
    private String passWord;

    public UserRecord(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
