package com.aspirecn.exam;

/**
 * Created by ding on 10/20/17.
 *
 * @author ding
 */
public class Unit3 {

    private final Object obj = new Object();

    private int value = 10;

    private class ThreadA extends Thread {
        @Override
        public void run() {

            synchronized (obj) {

                while (value > 0) {

                    if (value % 2 == 0) {
                        System.out.println("ThreadA value=" + value);
                        value--;

                        obj.notifyAll();
                    } else {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private class ThreadB extends Thread {
        @Override
        public void run() {

            synchronized (obj) {

                while (value > 0) {
                    if (value % 2 == 1) {
                        System.out.println("ThreadB value=" + value);
                        value--;

                        obj.notifyAll();
                    } else {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }


        }


    }

    public static void main(String[] args) {

        Unit3 unit3 = new Unit3();
        ThreadA threadA = unit3.new ThreadA();
        ThreadB threadB = unit3.new ThreadB();

        threadA.start();
        threadB.start();

    }
}
