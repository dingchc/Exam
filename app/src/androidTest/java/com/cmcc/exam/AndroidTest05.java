package com.cmcc.exam;

import android.os.Handler;
import android.os.Message;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.runner.RunWith;

/**
 * Created by ding on 11/2/17.
 */

@RunWith(AndroidJUnit4.class)
public class AndroidTest05 {

    @org.junit.Test
    public void doTest() {

        MyThread myThread = new MyThread();

        myThread.start();
    }

    /**
     * 线程类
     */
    private class MyThread extends Thread {

        private MyHandler handler;

        @Override
        public void run() {

            Log.i("AndroidTest05", "MyThread start ...");

            handler = new MyHandler();

            Message message = Message.obtain();
            message.what = MyHandler.WHAT_CMD;
            handler.sendMessage(message);
        }
    }

    /**
     * MyHandler类
     */
    private static class MyHandler extends Handler {

        public static final int WHAT_CMD = 100;

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case WHAT_CMD:
                    Log.i("MyHandler", "receive WHAT_CMD");
                    break;
            }
        }
    }
}
