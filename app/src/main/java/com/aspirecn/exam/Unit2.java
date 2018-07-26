package com.aspirecn.exam;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ding on 10/20/17.
 *
 * @author ding
 */

public class Unit2 {

    public volatile int inc = 0;

    public void increase() {
        inc++;
    }


    public static void main(String[] args) {

        final Unit2 unit2 = new Unit2();

        int activeCnt = Thread.activeCount();

        for (int i = 0; i < 10; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < 10; i++) {
                        unit2.increase();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }

        while (Thread.activeCount() > activeCnt) {
            Thread.yield();
        }

        System.out.println("inc=" + unit2.inc);
    }

}
