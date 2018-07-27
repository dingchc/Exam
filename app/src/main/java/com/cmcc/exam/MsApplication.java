package com.cmcc.exam;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.cmcc.exam.db.AppDatabase;
import com.cmcc.library.wrapper.retrofit.HttpController;

/**
 * Created by ding on 10/24/17.
 */

public class MsApplication extends Application {


    private static MsApplication app;
    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        HttpController.INSTANCE.setAppContext(this);

        // .addMigrations(AppDatabase.migrations)
        db = Room.databaseBuilder(this, AppDatabase.class, "app_db").fallbackToDestructiveMigration().build();
    }

    public static MsApplication getApp() {

        return app;
    }

    public static AppDatabase getDb() {
        return db;
    }
}
