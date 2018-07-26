package com.aspirecn.exam.db.dao;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.aspirecn.exam.db.entry.ClassEntry;

import java.util.List;

import io.reactivex.Flowable;

/**
 * 班级Dao
 * Created by ding on 24/02/2018.
 */
@Dao
public interface ClassDao {

    /**
     * 组合查询
     * @param classId 班级id
     * @return 组合数据
     */
    @Query("select a.class_name, u.uid, u.user_name from ClassInfo a, User u where a.class_id = :classId and a.member_id = u.uid")
    List<ClassMember> getClassMember(long classId);

    /**
     * 获取班级列表
     * @return 班级列表
     */
    @Query("select * from classinfo")
    List<ClassEntry> getAllClass();

    /**
     * 插入班级数据
     * @param classEntry 班级
     * @return 数据id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertClassMember(ClassEntry classEntry);

    class ClassMember {

        @ColumnInfo(name = "class_name")
        public String className;

        @ColumnInfo(name = "uid")
        public long userId;

        @ColumnInfo(name = "user_name")
        public String userName;
    }
}
