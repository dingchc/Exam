package com.example.demo.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.newstartpoint.bleeplus.bean.DeviceBleBean;
import com.newstartpoint.bleeplus.bean.ReturnData;
import com.newstartpoint.bleeplus.tools.CursorSdk;
import com.newstartpoint.bleeplus.tools.DataCallbackHandler;
import com.newstartpoint.bleeplus.tools.OtherUtils;

import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by jingjing on 2016/11/26.
 * 操作蓝牙实体类
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleUtils {
    private static BleUtils instance;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private Handler mHandler;
    public BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mCharacteristic;
    private String mDeviceAddress="";
    private boolean connect;
    private DataCallbackHandler mCallbackHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    public static final int DEVICE_SCAN_STARTED = 1;       // 开始搜索
    public static final int DEVICE_SCAN_STOPPED = 2;       // 结束搜索
    public static final int DEVICE_SCAN_COMPLETED = 3;      //搜索完成
    public static final int DEVICE_SCAN = 4; //搜索蓝牙结果
    public static final int DEVICE_CONNECTED = 5;
    public static final int DEVICE_DISCONNECTED = 6;
    public static final int CHARACTERISTIC_ACCESSIBLE = 7;

    // 5秒后停止查找搜索.
    private static final long SCAN_PERIOD = 5000;
    public static BleUtils getInstance(){
        if(null == instance){
            instance = new BleUtils();
        }
        return instance;
    }
    boolean linkHandBle=false;

    public boolean isLinkHandBle() {
        return linkHandBle;
    }

    /**
     * 设置连接手环还是中控器
     * @param linkHandBle
     */
    public void setLinkHandBle(boolean linkHandBle) {
        this.linkHandBle = linkHandBle;
        CursorSdk.getInstance().setLinkHandBle(linkHandBle);
    }
    /**
     * 检测是否支持蓝牙 初始化蓝牙设备
     * @param context
     * @return
     */
    public boolean initBle(Context context, Handler handler){
        this.mContext=context;
        this.mHandler=handler;
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, "BLE is not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(null==bluetoothManager||null==mBluetoothAdapter){
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 检查蓝牙是否开启 为了确保设备上蓝牙能使用
     */
    public void checkBleEnable(Context context){
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
//        if (!mBluetoothAdapter.isEnabled()) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
//        }
    }
    public void setmCallbackHandler(DataCallbackHandler mCallbackHandler) {
        this.mCallbackHandler = mCallbackHandler;
    }
    /**
     * 蓝牙搜索关闭操作
     * @param enable true =开启 false=关闭
     */
    public void scanLeDevice(final boolean enable) {
        scanLeDevice(enable, SCAN_PERIOD);
    }
    public void scanLeDevice(final boolean enable, long scanTime) {
//        if(!connect) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mHandler.sendEmptyMessage(DEVICE_SCAN_COMPLETED);
                }
            }, scanTime);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mHandler.sendEmptyMessage(DEVICE_SCAN_STARTED);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mHandler.sendEmptyMessage(DEVICE_SCAN_STOPPED);
        }
    }

    /**
     * 蓝牙设备搜索回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if  (device.getName()!=null)
            {
                if (device.getName().contains("Eplus-WS")
                        ||device.getName().contains("Cursor Box")
                        ||device.getName().contains("ARC-Trio-")
                        )
                {
                    Message message=new Message();
                    message.what=DEVICE_SCAN;
                    DeviceBleBean m = new DeviceBleBean(device.getName(),device.getAddress(),rssi);
                    message.obj=m;
                    mHandler.sendMessage(message);
                }
            }
        }
    };

    /**
     * 蓝牙uuid写入
     * @param mDeviceAddress 蓝牙uuid
     */
    public void setmDeviceAddress(String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    /**
     * 蓝牙广播
     */
    public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            final String action = intent.getAction();
            System.out.println("action = " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if(null==mCharacteristic) {
                    if (linkHandBle) {
                        try {
                            if(null!=mBluetoothLeService&&null!=mBluetoothLeService.getmBluetoothGatt()) {

                                BluetoothGattService service = mBluetoothLeService.getmBluetoothGatt().getService(UUID
                                        .fromString(SampleGattAttributes.COMMUMICATION_READ_UUID1));
                                if (null != service) {
                                    mCharacteristic = service.getCharacteristic(UUID
                                            .fromString(SampleGattAttributes.COMMUMICATION_WRITE_UUID1));
                                    mBluetoothLeService.setCharacteristicNotification(service.getCharacteristic(UUID
                                            .fromString(SampleGattAttributes.COMMUMICATION_WRITE_UUID2)), true);
                                    if (mCharacteristic != null) {
                                        connect = true;
                                        CursorSdk.getInstance().setConnect(true);
                                        CursorSdk.getInstance().setBleSetting(mBluetoothAdapter,mBluetoothLeService.getmBluetoothGatt(),mCharacteristic);

                                        Message message = new Message();
                                        message.obj = mDeviceAddress;
                                        message.what = DEVICE_CONNECTED;
                                        mHandler.sendMessage(message);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            connect = false;
                            CursorSdk.getInstance().setConnect(false);
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if(null!=mBluetoothLeService&&null!=mBluetoothLeService.getmBluetoothGatt()) {
                                BluetoothGattService service = mBluetoothLeService.getmBluetoothGatt().getService(UUID
                                        .fromString(SampleGattAttributes.COMMUMICATION_READ_UUID));
                                if (null != service) {
                                    mCharacteristic = service.getCharacteristic(UUID
                                            .fromString(SampleGattAttributes.COMMUMICATION_WRITE_UUID));
                                    mBluetoothLeService.setCharacteristicNotification(mCharacteristic, true);

                                    if (mCharacteristic != null) {
                                        connect = true;
                                        CursorSdk.getInstance().setConnect(true);
                                        CursorSdk.getInstance().setBleSetting(mBluetoothAdapter,mBluetoothLeService.getmBluetoothGatt(),mCharacteristic);
                                        Message message = new Message();
                                        message.obj = mDeviceAddress;
                                        message.what = CHARACTERISTIC_ACCESSIBLE;
                                        mHandler.sendMessage(message);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            connect = false;
                            CursorSdk.getInstance().setConnect(false);
                            e.printStackTrace();
                        }
                    }
                }
//
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connect=false;
                CursorSdk.getInstance().setConnect(false);
                mCharacteristic=null;
                mHandler.sendEmptyMessage(DEVICE_DISCONNECTED);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                if(null==mCharacteristic) {
                    if (!linkHandBle) {
                        try {
                            if(null!=mBluetoothLeService&&null!=mBluetoothLeService.getmBluetoothGatt()) {
                                BluetoothGattService service = mBluetoothLeService.getmBluetoothGatt().getService(UUID
                                        .fromString(SampleGattAttributes.COMMUMICATION_READ_UUID));
                                if (null != service) {
                                    mCharacteristic = service.getCharacteristic(UUID
                                            .fromString(SampleGattAttributes.COMMUMICATION_WRITE_UUID));
                                    mBluetoothLeService.setCharacteristicNotification(mCharacteristic, true);

                                    if (mCharacteristic != null) {
                                        connect = true;
                                        CursorSdk.getInstance().setConnect(true);
                                        CursorSdk.getInstance().setBleSetting(mBluetoothAdapter,mBluetoothLeService.getmBluetoothGatt(),mCharacteristic);

                                        Message message = new Message();
                                        message.obj = mDeviceAddress;
                                        message.what = CHARACTERISTIC_ACCESSIBLE;
                                        mHandler.sendMessage(message);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            connect = false;
                            CursorSdk.getInstance().setConnect(false);
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if(null!=mBluetoothLeService&&null!=mBluetoothLeService.getmBluetoothGatt()) {
                                BluetoothGattService service = mBluetoothLeService.getmBluetoothGatt().getService(UUID
                                        .fromString(SampleGattAttributes.COMMUMICATION_READ_UUID1));
                                if (null != service) {
                                    mCharacteristic = service.getCharacteristic(UUID
                                            .fromString(SampleGattAttributes.COMMUMICATION_WRITE_UUID1));
                                    mBluetoothLeService.setCharacteristicNotification(service.getCharacteristic(UUID
                                            .fromString(SampleGattAttributes.COMMUMICATION_WRITE_UUID2)), true);
                                    if (mCharacteristic != null) {
                                        connect = true;
                                        CursorSdk.getInstance().setConnect(true);
                                        CursorSdk.getInstance().setBleSetting(mBluetoothAdapter,mBluetoothLeService.getmBluetoothGatt(),mCharacteristic);

                                        Message message = new Message();
                                        message.obj = mDeviceAddress;
                                        message.what = DEVICE_CONNECTED;
                                        mHandler.sendMessage(message);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            connect = false;
                            CursorSdk.getInstance().setConnect(false);
                            e.printStackTrace();
                        }
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //接收数据
                try {
                    String validData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    if (!TextUtils.isEmpty(validData)) {
                        if(!linkHandBle)
                            CursorSdk.getInstance().receiveData(validData);
                        else
                            CursorSdk.getInstance().parseHandData(validData);
                    }
                } catch (Exception e) {
//                    Toast.makeText(mContext,"出错",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }else if(BluetoothLeService.ACTION_WRITE_DATA_STATE_OK.equals(action)){

                CursorSdk.getInstance().setWrite(true);
                if(CursorSdk.getInstance().getWritedata() != null && CursorSdk.getInstance().getWritedata().size() > 0)
                    CursorSdk.getInstance().getWritedata().remove(0);
                CursorSdk.getInstance().writeBleData();
            }else if(BluetoothLeService.ACTION_WRITE_DATA_STATE_FAIL.equals(action)){
                CursorSdk.getInstance().setWrite(false);
            }
        }
    };

    /**
     * 初始化蓝牙连接
     */
    public void initConnection(Context context){
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        boolean bll = context.bindService(gattServiceIntent, mServiceConnection,BIND_AUTO_CREATE);
    }

    /**
     * 蓝牙连接
     */
    public void connection(){
        if(null!=mBluetoothLeService&&!"".equals(mDeviceAddress))
            mBluetoothLeService.connect(mDeviceAddress);
    }

    /**
     * 断开蓝牙
     */
    public void disconnect(){
        if(null!=mBluetoothLeService)
            mBluetoothLeService.disconnect();
        connect = false;
    }





    private void logWriteforUi(String notemess, int code){
        if(null!=mCallbackHandler) {
            ReturnData returnData=new ReturnData();
            returnData.setReturnType("00");
            returnData.setCode(code);
            returnData.setMessage(notemess);
            mCallbackHandler.doCallbackHandler(OtherUtils.ToGjsonString(returnData));
        }
    }




    /**
     * Code to manage Service lifecycle.
     */
    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    /**
     * 蓝牙隐式处理
     * @return
     */
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_DATA_STATE_OK);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_DATA_STATE_FAIL);
        return intentFilter;
    }

    public boolean IsConnect(){
        return connect;
    }

    public void closeBluetoothGatt(){
        mBluetoothLeService.closeBluetoothGatt();
        connect=false;
        mCharacteristic=null;
    }
}
