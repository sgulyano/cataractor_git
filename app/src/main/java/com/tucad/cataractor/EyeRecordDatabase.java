package com.tucad.cataractor;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {EyeRecord.class}, version = 1, exportSchema = false)
abstract class EyeRecordDatabase extends RoomDatabase {
    abstract DaoAccess daoAccess() ;
}
