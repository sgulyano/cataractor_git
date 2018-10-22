package com.tucad.cataractor;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {EyeRecord.class}, version = 1, exportSchema = false)
public abstract class EyeRecordDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess() ;
}
