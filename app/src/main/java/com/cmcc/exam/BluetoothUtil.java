package com.cmcc.exam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 蓝牙工具类
 *
 * @author ding
 *         Created by ding on 29/01/2018.
 */

public enum BluetoothUtil {

    /**
     * 实例
     */
    INSTANCE;

    private final String BIND_PREFIX = "ARC-Trio";

    private final String TEST_MAC = "FD:67:CE:ED:6C:42";

    /**
     * 手表的读数据UUID
     */
    private final String BIND_READ_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    /**
     * 手表的读数据UUID
     */
    private final String BIND_WRITE_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    /**
     * 手表的通知数据UUID
     */
    private final String BIND_NOTIFY_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";


    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 蓝牙特征
     */
    private BluetoothGattCharacteristic mGattCharacteristic;

    /**
     * Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    /**
     * 手表地址集合
     */
    private Set<String> mMacSet;

    BluetoothUtil() {
        AppLogger.i("BluetoothUtil");
        mMacSet = new HashSet<>();
    }

    /**
     * 搜索蓝牙设备
     */
    public void searchBleDevices() {
        AppLogger.i("searchBleDevices");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {

            final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                    String mac = device.getAddress();

                    if (!TextUtils.isEmpty(device.getName()) && device.getName().contains(BIND_PREFIX) && !mMacSet.contains(mac)) {
                        mMacSet.add(mac);
                    }
                }
            };

            mBluetoothAdapter.startLeScan(scanCallback);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(scanCallback);

                    for (String mac : mMacSet) {
                        AppLogger.i("mac=" + mac);
                    }

                    createBLEConnection();
                }
            }, 1000);

        }

    }

    /**
     * 创建连接
     */
    public void createBLEConnection() {

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(TEST_MAC);

        if (device != null) {

            device.connectGatt(MsApplication.getApp(), false, gattCallback);
        }

    }

    /**
     * 获取Gatt服务
     *
     * @param gatt gatt对象
     * @return Gatt服务
     */
    private BluetoothGattService getBLEDeviceServices(BluetoothGatt gatt) {

        BluetoothGattService service = gatt.getService(UUID.fromString(BIND_READ_UUID));

        AppLogger.i("service="+service);

        if (service != null) {
            return service;
        }

        return null;
    }

    /**
     * 获取Gatt特征
     *
     * @param service Gatt服务
     * @return Gatt特征
     */
    private BluetoothGattCharacteristic getBLEDeviceCharacteristics(BluetoothGattService service) {

        if (service == null) {
            AppLogger.e("Gatt Service is null");
            return null;
        }

        return service.getCharacteristic(UUID.fromString(BIND_WRITE_UUID));
    }

    /**
     * 初始化Gatt特征
     *
     * @param gatt gatt对象
     */
    private void initCharacteristic(BluetoothGatt gatt) {

        BluetoothGattService service = getBLEDeviceServices(gatt);

        if (service != null) {
            mGattCharacteristic = getBLEDeviceCharacteristics(service);

            writeData(setEHandDateAndTime("2018-1-1 10:00:00"));
            gatt.writeCharacteristic(mGattCharacteristic);

            setNotificationEnable(gatt, service);
        }
    }

    /**
     * 设置蓝牙广播可用
     * @param gatt gatt对象
     * @param service gatt服务
     */
    private void setNotificationEnable(BluetoothGatt gatt, BluetoothGattService service) {

        if (gatt != null) {
            BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(UUID.fromString(BIND_NOTIFY_UUID));
            gatt.setCharacteristicNotification(gattCharacteristic, true);

            BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

            AppLogger.i("descriptor="+descriptor);

            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * Gatt回调
     */
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            AppLogger.i("onConnectionStateChange status=" + status + ", newState=" + newState);

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            AppLogger.i("onServicesDiscovered");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                initCharacteristic(gatt);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            AppLogger.i("onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            AppLogger.i("onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            AppLogger.i("onCharacteristicChanged");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            AppLogger.i("onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            AppLogger.i("onDescriptorWrite");
        }
    };

    private void writeData(byte[] data) {

        if (mGattCharacteristic != null) {
            mGattCharacteristic.setValue(data);

        }
    }

    public byte[] setEHandDateAndTime(String time) {
        byte[] writeBuffer = null;
        try {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date t1 = localSimpleDateFormat.parse(time);
            int n = t1.getYear();
            int y = t1.getMonth() + 1;
            int r = t1.getDate();
            int s = t1.getHours();
            int f = t1.getMinutes();
            int m = t1.getSeconds();
            String timecode = "A0" + convertDecimalToBinary1(n, 4) + convertDecimalToBinary1(y, 2) + convertDecimalToBinary1(r, 2) + convertDecimalToBinary1(s, 2) + convertDecimalToBinary1(f, 2) + convertDecimalToBinary1(m, 2) + "5A";
            writeBuffer = hexString2Bytes(timecode);
        } catch (ParseException var12) {
            var12.printStackTrace();
        }

        return writeBuffer;

    }

    public static String convertDecimalToBinary1(int value, int num) {
        String str = Integer.toHexString(value);

        for(int i = str.length(); i < num; ++i) {
            str = "0" + str;
        }

        return str;
    }

    public static byte[] hexString2Bytes(String hexStr) {
        byte[] b = new byte[hexStr.length() / 2];
        int j = 0;

        for(int i = 0; i < b.length; ++i) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte)(parse(c0) << 4 | parse(c1));

            AppLogger.i("b["+i+"]="+b[i] + ", bin=" + Integer.toBinaryString(b[i] & 0xFF));
        }

        return b;
    }

    private static int parse(char c) {
        return c >= 97?c - 97 + 10 & 15:(c >= 65?c - 65 + 10 & 15:c - 48 & 15);
    }


}
