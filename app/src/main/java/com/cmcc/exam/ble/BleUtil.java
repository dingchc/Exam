package com.cmcc.exam.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.cmcc.exam.AppLogger;
import com.cmcc.exam.MsApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 蓝牙核心类
 *
 * @author ding
 *         Created by ding on 26/02/2018.
 */

public enum BleUtil {

    /**
     * 实例
     */
    INSTANCE;

    /**
     * 客户端UUID
     */
    private static final String CLIENT_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * 读取到数据
     */
    public static final int WHAT_DATA_READ = 0X1;

    /**
     * 蓝牙配置的UUID
     */
    private BleConfigUUID mConfigUUID;

    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 蓝牙Gatt
     */
    private BluetoothGatt mBlueGatt;

    /**
     * 蓝牙特征
     */
    private BluetoothGattCharacteristic mGattCharacteristic;

    /**
     * 蓝牙操作回调
     */
    private List<BleListener> mBleListenerList;

    /**
     * Handler
     */
    private Handler mHandler;

    /**
     * 手表地址集合
     */
    private Map<String, BluetoothDevice> mDeviceMap;


    BleUtil() {
        AppLogger.i("BleUtil");
        mDeviceMap = new HashMap<>(500);
        mBleListenerList = new ArrayList<>();
    }

    /**
     * 初始化蓝牙
     * 请确保蓝牙已开启
     *
     * @return true 初始化成功、false 初始化失败
     */
    public boolean init(Handler handler) {

        // Handler不能为空、且handler为主线程的
        if (handler == null || handler.getLooper() != Looper.getMainLooper()) {
            throw new IllegalArgumentException("handler is null or handler is not on main");
        }

        this.mHandler = handler;

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        return isInitReady();
    }

    /**
     * 是否初始化好
     * @return true 初始化成功、false 初始化失败
     */
    public boolean isInitReady() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * 增加蓝牙监听
     *
     * @param listener 监听
     */
    public void addBleListener(BleListener listener) {

        if (mBleListenerList != null && !mBleListenerList.contains(listener)) {
            mBleListenerList.add(listener);
        }
    }

    /**
     * 增加蓝牙监听
     *
     * @param listener 监听
     */
    public void removeBleListener(BleListener listener) {

        if (mBleListenerList != null && mBleListenerList.contains(listener)) {
            mBleListenerList.remove(listener);
        }
    }

    /**
     * 搜索蓝牙设备
     *
     * @param timeMillis 持续时长(毫秒)
     * @param filter     过滤字符串
     */
    public void searchBleDevices(final long timeMillis, final String filter) {

        AppLogger.i("searchBleDevices");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scanByScanner(timeMillis, filter);
            } else {
                scanDefault(timeMillis, filter);
            }
        }
    }

    /**
     * 创建连接
     *
     * @param address    蓝牙地址
     * @param configUUID 蓝牙配置的UUID信息
     */
    public void createBLEConnection(String address, BleConfigUUID configUUID) {

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        this.mConfigUUID = configUUID;

        if (this.mConfigUUID == null || !this.mConfigUUID.checkValid()) {
            throw new IllegalArgumentException("蓝牙UUID为空");
        }

        if (device != null) {

            device.connectGatt(MsApplication.getApp(), false, mGattCallback);
        }

    }

    /**
     * 关闭蓝牙Gatt
     */
    public void closeBLEConnection() {

        if (mBlueGatt != null) {
            mBlueGatt.disconnect();
            mBlueGatt.close();
        }

    }

    public void writeTest() {

        // 写测试数据
//            writeData(EplusBleTest.setEHandDateAndTime("2018-1-1 10:00:00"));
//            gatt.writeCharacteristic(mGattCharacteristic);

        // 写盒子
        try {
            writeData(EplusBleTest.getWriteBuffer("ab" + " C1 00 00 " + "ab"));
            mBlueGatt.writeCharacteristic(mGattCharacteristic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认搜索
     *
     * @param timeMillis 持续时长(毫秒)
     * @param filter     过滤字符串
     */
    private void scanDefault(final long timeMillis, final String filter) {

        final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                cacheDevice(device, filter);
            }
        };

        mBluetoothAdapter.startLeScan(scanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(scanCallback);

                for (BluetoothDevice device : mDeviceMap.values()) {
                    AppLogger.i("device=" + device.getName() + ", " + device.getAddress());
                }

                // 通知回调
                notifyScanResult(new ArrayList<>(mDeviceMap.values()));

            }
        }, timeMillis);
    }

    /**
     * 使用Scanner搜索
     *
     * @param timeMillis 持续时长(毫秒)
     * @param filter     过滤字符串
     */
    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
    private void scanByScanner(final long timeMillis, final String filter) {

        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();

        final ScanCallback callback = new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                cacheDevice(result.getDevice(), filter);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                AppLogger.i("onBatchScanResults");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        scanner.startScan(callback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(callback);

                // 通知回调
                notifyScanResult(new ArrayList<>(mDeviceMap.values()));

            }
        }, timeMillis);

    }

    /**
     * 缓存设备
     *
     * @param device 设备
     * @param filter 名字过滤
     */
    private void cacheDevice(BluetoothDevice device, String filter) {

        String deviceName = device.getName();

        if (!TextUtils.isEmpty(deviceName)) {
            String mac = device.getAddress();
            if (!TextUtils.isEmpty(filter) && deviceName.contains(filter)) {
                mDeviceMap.put(mac, device);
            } else {
                mDeviceMap.put(mac, device);
            }
        }
    }


    /**
     * 通知扫描结果
     *
     * @param deviceList 设备列表
     */
    private void notifyScanResult(List<BluetoothDevice> deviceList) {

        // 通知监听
        if (mBleListenerList != null && mBleListenerList.size() > 0) {
            for (BleListener listener : mBleListenerList) {
                listener.onScanFinish(deviceList);
            }
        }
    }

    /**
     * Gatt回调
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
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
                mBlueGatt = gatt;
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
            AppLogger.i("onCharacteristicWrite status=" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            AppLogger.i("onCharacteristicChanged = " + characteristic.getUuid());

            BleData bleData = new BleData(characteristic.getUuid().toString(), characteristic.getValue());

            Message msg = Message.obtain();
            msg.what = WHAT_DATA_READ;
            msg.obj = bleData;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            AppLogger.i("onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            AppLogger.i("onDescriptorWrite status= " + status);
        }
    };

    /**
     * 初始化Gatt特征
     *
     * @param gatt gatt对象
     */
    private void initCharacteristic(BluetoothGatt gatt) {

        BluetoothGattService service = getBLEDeviceServices(gatt);

        if (service != null) {

            mGattCharacteristic = getBLEDeviceCharacteristics(service);

            AppLogger.i("mGattCharacteristic=" + mGattCharacteristic);

            // 开启通知服务
            if (mGattCharacteristic != null && !TextUtils.isEmpty(mConfigUUID.indicateUUID)) {
                setNotificationEnable(gatt, service);
            }
        }
    }

    /**
     * 获取Gatt服务
     *
     * @param gatt gatt对象
     * @return Gatt服务
     */
    private BluetoothGattService getBLEDeviceServices(BluetoothGatt gatt) {

        BluetoothGattService service = gatt.getService(UUID.fromString(mConfigUUID.serviceUUID));

        AppLogger.i("service=" + service);

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

        return service.getCharacteristic(UUID.fromString(mConfigUUID.characteristicUUID));
    }

    /**
     * 设置蓝牙广播可用
     *
     * @param gatt    gatt对象
     * @param service gatt服务
     */
    private void setNotificationEnable(BluetoothGatt gatt, BluetoothGattService service) {

        if (gatt != null) {

            BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(UUID.fromString(mConfigUUID.indicateUUID));
            gatt.setCharacteristicNotification(gattCharacteristic, true);

            BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString(CLIENT_UUID));

            AppLogger.i("descriptor=" + descriptor);

            if (descriptor != null) {

                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * 蓝牙操作回调
     */
    public interface BleListener {

        /**
         * 蓝牙操作回调
         *
         * @param deviceList 蓝牙设备列表
         */
        void onScanFinish(List<BluetoothDevice> deviceList);

        /**
         * 错误
         *
         * @param result 结果
         */
        void onError(BleResult result);

    }

    /**
     * 蓝牙配置的UUID
     */
    public static class BleConfigUUID {

        /**
         * Gatt服务的UUID
         */
        String serviceUUID;

        /**
         * 特征的UUID
         */
        String characteristicUUID;

        /**
         * 通知的UUID
         */
        String indicateUUID;


        public BleConfigUUID(String serviceUUID, String characteristicUUID) {

            this(serviceUUID, characteristicUUID, null);
        }

        public BleConfigUUID(String serviceUUID, String characteristicUUID, String indicateUUID) {
            this.serviceUUID = serviceUUID;
            this.characteristicUUID = characteristicUUID;
            this.indicateUUID = indicateUUID;
        }

        /**
         * 检查UUID是否为空
         *
         * @return true 正确、false 不正确
         */
        public boolean checkValid() {
            return !TextUtils.isEmpty(serviceUUID) && !TextUtils.isEmpty(characteristicUUID);
        }
    }

    /**
     * 蓝牙结果
     */
    public static class BleResult {

        /**
         * 错误码
         */
        public int errorCode;

        /**
         * 错误信息
         */
        public int errorMsg;

        /**
         * 指令
         */
        public int cmd;

        /**
         * 数据
         */
        public Object obj;

    }

    /**
     * 蓝牙结果
     */
    public static class BleData {

        /**
         * uuid值
         */
        public String uuid;

        /**
         * 数据
         */
        public byte[] data;

        public BleData(String uuid, byte[] data) {

            this.uuid = uuid;
            this.data = data;
        }
    }


    /**
     * 写数据
     * @param buffer 数据
     */
    public void writeData(byte[] buffer) {

        if (mGattCharacteristic != null) {
            mGattCharacteristic.setValue(buffer);
        }

        if (mBlueGatt != null) {
            mBlueGatt.writeCharacteristic(mGattCharacteristic);
        }
    }


}
