package com.cmcc.exam.db.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author ding
 * Created by ding on 24/02/2018.
 */

@Entity(tableName = "User")
public class UserEntry {

    @PrimaryKey
    private int uid = 0;

    @ColumnInfo(name="user_name")
    private String name;

    @ColumnInfo(name="user_age")
    private int age = 12;

    @ColumnInfo(name="user_gender")
    private String gender;

    @ColumnInfo(name="user_sex")
    private int sex;

    @Ignore
    private String alias;

    public UserEntry() {

    }

    public UserEntry(int uid, String name, int age, String gender) {
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
