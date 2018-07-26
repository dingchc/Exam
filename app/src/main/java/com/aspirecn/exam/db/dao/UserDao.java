package com.aspirecn.exam.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.aspirecn.exam.db.entry.UserEntry;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author ding
 *         Created by ding on 24/02/2018.
 */
@Dao
public interface UserDao {

    /**
     * 获取用户
     *
     * @return 用户列表
     */
    @Query("select * from User")
    List<UserEntry> getAllUser();

    /**
     * 获取用户
     *
     * @return 用户列表
     */
    @Query("select * from User")
    Flowable<List<UserEntry>> getAllUserRx();

    /**
     * 查找一个用户
     *
     * @param userId 用户id
     * @return 用户
     */
    @Query("select * from User where uid =:userId")
    UserEntry findAUser(long userId);


    /**
     * 插入用户
     *
     * @param users 用户列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntry... users);

    /**
     * 删除用户
     *
     * @param user 用户
     */
    @Delete
    void deleteAUser(UserEntry user);
}
