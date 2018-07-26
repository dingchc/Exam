package com.aspirecn.exam;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ding on 10/20/17.
 *
 * @author ding
 */
public class Unit1 {

    final int DATA_CNT = 10;

    List<Integer> dataList = new ArrayList<>();

    /**
     * 初始化
     */
    private void initialize() {

        AppLogger.i("initialize");

        for (int i = 0; i < DATA_CNT; i++) {
            dataList.add(i);
        }
    }

    /**
     * 删除数值
     */
    public void removeValue() {

        try {
            for (Integer value : dataList) {

                if (value++ < DATA_CNT - 1) {
                    dataList.remove(value);
                }

                System.out.println("value = " + value);
            }
        } catch (Exception e) {
//            System.out.println("Exception");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        Unit1 unit1 = new Unit1();

        unit1.initialize();

        unit1.removeValue();

    }

}
