package com.tucad.cataractor;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

class EyeRecordDatabaseClient {
    private static final String TAG = "EyeRecordDatabaseClient";
    private static final String DATABASE_NAME = "eyerecord_db";

    //our app database object
    private static EyeRecordDatabase eyeRecordDatabase = null;

    static void setupEyeRecordDatabaseClient(Context mCtx) {
        //creating the app database with Room database builder
        eyeRecordDatabase = Room.databaseBuilder(mCtx, EyeRecordDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    static EyeRecordDatabase getDatabase() {
        if (eyeRecordDatabase == null) {
            Log.e(TAG, "Must setup database first.");
            throw new NullPointerException();
        }
        return eyeRecordDatabase;
    }

    static void deleteDatabase(Context mCtx) {
        mCtx.deleteDatabase(DATABASE_NAME); //<<<< ADDED before building Database.
    }

    static void addEyeRecord(EyeRecord eyerecord) {
        getDatabase().daoAccess().insertOnlySingleEyeRecord(eyerecord);
    }
}
