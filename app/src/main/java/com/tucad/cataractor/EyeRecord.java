package com.tucad.cataractor;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class EyeRecord {

    @PrimaryKey (autoGenerate = true)
    private Integer recordId;

    private String firstname;
    private String lastname;
    private String age;
    private String sex;
    private String imagepath;

    EyeRecord() {
    }

    @NonNull
    Integer getRecordId() {
        return recordId;
    }

    void setRecordId(@NonNull Integer recordId) {
        this.recordId = recordId;
    }

    public String getFirstname() {
        return firstname;
    }

    void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    String getSex() { return sex; }

    void setSex(String sex) {
        this.sex = sex;
    }

    String getImagepath() {
        return imagepath;
    }

    void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
