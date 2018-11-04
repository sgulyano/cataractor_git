package com.tucad.cataractor;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {
    @Insert
    void insertOnlySingleEyeRecord(EyeRecord eyeRecord);
    @Insert
    void insertMultipleEyeRecords (List<EyeRecord> eyeRecordList);
    @Query("SELECT * FROM EyeRecord WHERE recordId = :recordId")
    EyeRecord fetchOneEyeRecordbyRecordId(int recordId);
    @Query("SELECT * FROM EyeRecord ORDER BY recordId DESC;")
    List<EyeRecord> fetchAllEyeRecords();
    @Query("SELECT * FROM EyeRecord ORDER BY recordId DESC LIMIT 1;")
    EyeRecord fetchOneEyeRecord();
    @Update
    void updateEyeRecord(EyeRecord eyeRecords);
    @Delete
    void deleteEyeRecord(EyeRecord eyeRecords);
}

