package com.aspirecn.exam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ding
 *         Created by ding on 01/02/2018.
 */

public class ExecutorActivity extends AppCompatActivity {

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private boolean isExecute = true;

    private MyThread1 thread1 = new MyThread1();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.executor_layout);

        Button btnClick1 = (Button) findViewById(R.id.btn_click1);
        Button btnClick2 = (Button) findViewById(R.id.btn_click2);

        thread1.start();

        btnClick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLogger.i("tryLock begin " );

                if (lock.tryLock()) {
                    thread1.reset();
                    condition.signal();
                    lock.unlock();
                }

                AppLogger.i("tryLock end" );

            }
        });

        btnClick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExecute = false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class MyThread1 extends Thread {

        int index = 0;

        @Override
        public void run() {

            try {
                lock.lock();

                while (isExecute) {
                    AppLogger.i("index=" + (index++));

                    if (index > 100) {
                        condition.await();
                    }

                    AppLogger.i("* index=" + (index) + ", isExecute="+isExecute);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void reset() {
            index = 0;
        }
    }
}
