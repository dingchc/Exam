package com.aspirecn.exam.net;

import com.aspirecn.exam.AppLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author ding
 * Created by ding on 31/01/2018.
 */

public class TcpClient {

    /**
     * 等待队列里面最大的个数
     */
    private static final int MAX_WAIT_COUNT = 20;

    /**
     * 准备
     */
    private final int STATE_IDLE = 0;

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
    private final int STATE_DISCONNECT = STATE_IDLE;

    /**
     * Socket
     */
    private Socket mSocket;

    /**
     * 状态
     */
    private int mState = STATE_IDLE;

    /**
     * 读线程
     */
    private ReadThread mReadThread;

    /**
     * 写线程
     */
    private WriteThread mWriteThread;

    /**
     * 连接服务器
     *
     * @param ip      服务器地址
     * @param port    断开
     * @param timeout 超时时间
     */
    public synchronized void connect(String ip, int port, int timeout, TcpClientListener listener) {

        AppLogger.i("connect");

        if (mState != STATE_IDLE) {
            AppLogger.e("thread is not idle, return");
            return;
        }

        // 正在连接
        mState = STATE_CONNECTING;

        if (mReadThread == null) {
            mReadThread = new ReadThread(ip, port, timeout, listener);
            mReadThread.start();
        }
    }

    /**
     * 断开服务器
     */
    public synchronized void disconnect() {

        AppLogger.i("disconnect");

        try {
            if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {
                mSocket.close();
            }

            mState = STATE_DISCONNECT;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mSocket = null;
        }
    }

    /**
     * 是否已连接
     * @return true 连接、 false 未连接
     */
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    /**
     * 写数据
     * @param data 数据
     */
    public void writeData(byte[] data) {

        if (mSocket == null) {
            AppLogger.e("Socket is null");
            return;
        }

        if (mState != STATE_CONNECTED) {
            AppLogger.e("Socket is not connect");
            return;
        }

        if (mWriteThread == null) {

            mWriteThread = new WriteThread();
            mWriteThread.start();
        }

        mWriteThread.addData(data);
    }

    /**
     * 读线程
     */
     class ReadThread extends Thread {

        /**
         * IP地址
         */
        private String ip;

        /**
         * 端口
         */
        private int port;

        /**
         * 超时时间(毫秒)
         */
        private int timeout;

        /**
         * 监听器
         */
        private TcpClientListener listener;


        ReadThread(String ip, int port, int timeout, TcpClientListener listener) {
            this.ip = ip;
            this.port = port;
            this.timeout = timeout;
            this.listener = listener;
        }

        @Override
        public void run() {

            try {

                mSocket = new Socket();

                AppLogger.i("client connect...");

                // 超时
                mSocket.connect(new InetSocketAddress(ip, port), timeout);

                // 已连接
                mState = STATE_CONNECTED;

                if (listener != null) {
                    listener.onConnected();
                }

                // 读取数据
                readData();

            } catch (SocketTimeoutException e) {

                if (listener != null) {
                    listener.onTimeout();
                }
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 唤醒写线程
                if (mWriteThread != null) {
                    mWriteThread.notifySelf();
                }

                // 睡眠一下
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mSocket = null;
                mReadThread = null;
                mState = STATE_DISCONNECT;
                if (listener != null) {
                    listener.onDisconnected();
                }
                AppLogger.i("client connect end");
            }
        }

        /**
         * 读取数据
         */
        private void readData() {

            AppLogger.i("client read thread begin...");

            try {

                InputStream is = mSocket.getInputStream();

                byte[] buffer = new byte[1024 * 8];
                int length;
                int bodyLength;

                MyByteBuffer myByteBuffer = new MyByteBuffer();

                while ((length = is.read(buffer)) > 0) {

                    AppLogger.i("length=" + length);

                    myByteBuffer.append(buffer, length);

                    if (myByteBuffer.capacity >= 4) {

                        bodyLength = getBodyLength(myByteBuffer.buffer);
                        AppLogger.i("bodyLength=" + bodyLength);

                        if (bodyLength > 0 && myByteBuffer.capacity - 4 >= bodyLength) {

                            AppLogger.i("will decode length=" + (myByteBuffer.capacity - 4 ) + ", bodyLength="+bodyLength);

                            byte[] data = myByteBuffer.getBytes(4, bodyLength);

                            if (listener != null) {
                                listener.onReceiveData(data);
                            }

                            myByteBuffer.reset();
                        }
                    }
                }
            } catch (IOException e) {
                AppLogger.i("client read thread exception " + e.getMessage());
            }

            AppLogger.i("client read thread end...");
        }

        /**
         * 解码数据
         * @param data 数据
         */
        private void decodeData(byte[] data) {

            if (data != null) {
                ProtocolUtil protocolUtil = new ProtocolUtil();
                ProtocolUtil.Protocol protocol = protocolUtil.unpack(data);

                if (listener != null && protocol != null) {
                    listener.onReceiveData(protocol.data);
                }

                AppLogger.i("data=" + ProtocolUtil.getUtfString(protocol.data) + "[*]");
            }
        }


        /**
         * 读取包长度
         *
         * @param data 数据
         * @return 长度
         */
        private int getBodyLength(byte[] data) {

            int bodyLength = 0;

            if (data != null && data.length >= 4) {
                bodyLength = (0xFF & data[3]) + ((0xFF & data[2]) << 8) + ((0xFF & data[1]) << 16) + ((0xFF & data[0]) << 24);
            }

            return bodyLength;
        }
    }


    /**
     * 写线程
     */
    class WriteThread extends Thread {

        private ConcurrentLinkedQueue<byte[]> queue;

        private final Object lock = new Object();

        WriteThread() {
            queue = new ConcurrentLinkedQueue<>();
        }

        @Override
        public void run() {

            AppLogger.i("getWriteBuffer thread is start  ");

            while (mSocket!= null && !mSocket.isClosed()) {

                AppLogger.i("getWriteBuffer thread while ... ");

                synchronized (lock) {

                    try {

                        byte[] data = queue.poll();

                        if (data == null) {
                            AppLogger.i("getWriteBuffer to wait ... ");
                            lock.wait();
                        } else {

                            mSocket.getOutputStream().write(data);
                            AppLogger.i("getWriteBuffer data ");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

            mWriteThread = null;

            AppLogger.i("getWriteBuffer thread is die");
        }

        /**
         * 写数据
         * @param data 数据
         */
        void addData(byte[] data) {

            if (queue.size() >= MAX_WAIT_COUNT) {
                queue.poll();
            }

            queue.add(data);

            notifySelf();
        }

        /**
         * 唤醒自己
         */
        void notifySelf() {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    /**
     * Tcp连接回调
     */
    public interface TcpClientListener {

        /**
         * 已连接
         */
       void onConnected();

        /**
         * 已连接
         */
        void onDisconnected();

        /**
         * 超时
         */
       void onTimeout();

        /**
         * 收到数据
         * @param data 数据
         */
       void onReceiveData(byte[] data);
    }



}
