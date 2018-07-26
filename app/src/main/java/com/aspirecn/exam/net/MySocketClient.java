package com.aspirecn.exam.net;

import com.aspirecn.exam.AppLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;

/**
 * Created by ding on 6/21/17.
 */

public class MySocketClient implements Runnable {

    /**
     * 准备
     */
    private final int STATE_PREPARE = 0;

    /**
     * 正在连接
     */
    private final int STATE_CONNECTING = 1;

    /**
     * 已连接
     */
    private final int STATE_CONNECTED = 2;

    /**
     * 已断开
     */
    private final int STATE_DISCONNECT = 3;

    private Socket mSocket;
    private int mState = -1;

    private int mIndex;

    private Thread mThread;

    public MySocketClient() {

    }

    @Override
    public void run() {
        try {

            AppLogger.i("client prepare");

            mSocket = new Socket();

            // 正在连接
            mState = STATE_CONNECTING;
            AppLogger.i("client connect...");

            // 超时
            mSocket.connect(new InetSocketAddress("10.2.14.103", 36330));

            // 已连接
            mState = STATE_CONNECTED;

            // 读取数据
            readData();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AppLogger.i("client connect isConnected=" + mSocket.isConnected() + ", isClosed" + mSocket.isClosed());
        }

        mSocket = null;
        mState = STATE_DISCONNECT;
        AppLogger.i("client connect end");
    }

    /**
     * 读取数据
     */
    private void readData() {

        AppLogger.i("client read thread begin...");

        InputStream is = null;

        try {
            is = mSocket.getInputStream();

            byte[] buffer = new byte[1024];

            int length = 0;

            byte[] read;

            while ((length = is.read(buffer)) > 0) {
                read = new byte[length];
                System.arraycopy(buffer, 0, read, 0, length);
                AppLogger.i("client receive " + new String(read, "utf-8"));
                AppLogger.i("wait server data ... ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        AppLogger.i("client read thread end...");
    }

    /**
     * 写数据
     * @return true 成功、false 失败
     */
    public boolean writeData() {

        boolean ret = false;

        if (mSocket == null || mSocket.isClosed()) {
            AppLogger.i("socket is closed");
            return ret;
        }

        mIndex++;

        AppLogger.i("[" + mIndex + "]" + "writeData ... start");
        try {
            ProtocolUtil protocol = new ProtocolUtil();

            ProtocolUtil.Protocol req = new ProtocolUtil.Protocol();
            req.data = ("[" + mIndex + "]" + ProtocolUtil.data).getBytes("utf-8");
            byte[] data = protocol.pack(req);

            mSocket.getOutputStream().write(data);

            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        }

        AppLogger.i("[" + mIndex + "]" + "writeData ... end");

        return ret;
    }

    /**
     * 断开服务器
     */
    public void disconnect() {

        AppLogger.i("disconnect");

        try {
            if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mSocket = null;
        }

    }

    /**
     * 连接服务器
     */
    public synchronized void connect() {

        AppLogger.i("connect mState=" + mState);

        if (mState == STATE_PREPARE) {
            AppLogger.i("thread is prepare, can not create multi thread");
            return;
        }

        if (mState == STATE_CONNECTED) {
            AppLogger.i("net is already connected");
            return;
        }

        if (mState == STATE_CONNECTING) {
            AppLogger.i("net is connecting");
            return;
        }

        if (mState == STATE_DISCONNECT && mThread != null) {
            mThread = null;
        }

        setConnectState(STATE_PREPARE);

        if (mThread == null && mState == STATE_PREPARE) {
            mThread = new Thread(this);
            mThread.start();
        }
    }

    /**
     * 设置连接状态
     * @param state 连接状态
     */
    private synchronized void setConnectState(int state) {
        this.mState = state;
    }

}