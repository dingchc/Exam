package com.cmcc.exam.db.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author ding
 * Created by ding on 24/02/2018.
 */
@Entity(tableName = "ClassInfo")
public class ClassEntry {

    @PrimaryKey
    @ColumnInfo(name = "class_id")
    private long classId;

    @ColumnInfo(name="class_name")
    private String className;

    @ColumnInfo(name="member_id")
    private long memberId;

    public ClassEntry() {

    }

    public ClassEntry(long classId, String className, long memberId) {
        this.classId = classId;
        this.className = className;
        this.memberId = memberId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
