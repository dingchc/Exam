package com.aspirecn.exam.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.aspirecn.exam.db.dao.ClassDao;
import com.aspirecn.exam.db.dao.UserDao;
import com.aspirecn.exam.db.entry.ClassEntry;
import com.aspirecn.exam.db.entry.UserEntry;

/**
 * @author ding
 *         Created by ding on 24/02/2018.
 */
@Database(entities = {UserEntry.class, ClassEntry.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 获取UserDao
     *
     * @return UserDao
     */
    public abstract UserDao userDao();

    /**
     * 获取ClassDao
     *
     * @return ClassDao
     */
    public abstract ClassDao classDao();


    /**
     * 版本迭代
     * 注意：增加新表，需要自己写create table
     */
    public static Migration[] migrations = {new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE User ADD COLUMN user_gender Text");
        }
    }, new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE User ADD COLUMN user_sex INTEGER NOT NULL DEFAULT 1 ");
        }
    }, new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    }};
}
