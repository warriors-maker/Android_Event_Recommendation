package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {UserRecord.class, Event.class, EventLike.class, Comment.class},version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DatabaseInterface databaseInterface();
    public static final String Name = "MyApp";

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

}
