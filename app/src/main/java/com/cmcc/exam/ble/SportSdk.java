package com.cmcc.exam.ble;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cmcc.exam.AppLogger;

/**
 * 手表Sdk处理类
 *
 * @author ding
 *         Created by ding on 28/02/2018.
 */

public enum SportSdk {

    /**
     * 实例
     */
    INSTANCE;

    /**
     * 蓝牙处理类
     */
    private BleUtil mBleUtil;


    SportSdk() {

        mBleUtil = BleUtil.INSTANCE;
    }

    /**
     * 初始化
     *
     * @return 初始化返回结果
     */
    public boolean init() {

        boolean ret = false;

        if (!mBleUtil.isInitReady()) {
            ret = mBleUtil.init(mHandler);
        }

        return ret;
    }

    /**
     * 搜索蓝牙设备
     *
     * @param timeMillis 持续时长(毫秒)
     * @param filter     过滤字符串
     */
    public void searchBleDevices(final long timeMillis, final String filter) {

        AppLogger.i("searchBleDevices");

        if (mBleUtil.isInitReady()) {
            mBleUtil.searchBleDevices(timeMillis, filter);
        }
    }

    /**
     * 增加蓝牙监听
     *
     * @param listener 监听
     */
    public void addBleListener(BleUtil.BleListener listener) {

        mBleUtil.addBleListener(listener);
    }

    /**
     * 增加蓝牙监听
     *
     * @param listener 监听
     */
    public void removeBleListener(BleUtil.BleListener listener) {

        mBleUtil.removeBleListener(listener);
    }

    /**
     * 创建连接
     *
     * @param address    蓝牙地址
     * @param configUUID 蓝牙配置的UUID信息
     */
    public void createBLEConnection(String address, BleUtil.BleConfigUUID configUUID) {

        mBleUtil.createBLEConnection(address, configUUID);
    }

    private void processEplusData(final byte[] data) {

        if (data != null && data.length > 0) {

            AppLogger.i("data.length=" + data.length);

            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < data.length; i++) {
                String hex = Integer.toHexString(0xff & data[i]);
                buffer.append((hex.length() == 1) ? "0" + hex : hex);
                buffer.append(" ");
            }

            EplusBleTest.appendBoxBuffer(buffer);
            EplusBleTest.analyzeData();
        }
    }

    /**
     * 写手表数据
     */
    public void writeWatchData() {

        mBleUtil.writeData(EplusBleTest.setEHandDateAndTime("2018-1-1 11:22:33"));
    }

    /**
     * 写测试数据
     */
    public void writeBoxData() {

        // 写盒子
        try {
            mBleUtil.writeData(EplusBleTest.getWriteBuffer("ab" + " C1 00 00 " + "ab"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据接收处理Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                // 收到数据
                case BleUtil.WHAT_DATA_READ:

                    if (msg.obj instanceof BleUtil.BleData) {
                        BleUtil.BleData bleData = (BleUtil.BleData) msg.obj;
                        processEplusData(bleData.data);
                    }

                    break;
                default:
            }
        }
    };
}
