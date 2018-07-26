package com.aspirecn.exam.net;

/**
 * @author ding
 * Created by ding on 6/23/17.
 */

public class MyByteBuffer {

    int capacity = 0;

    byte[] buffer;

    private int limit = 0;

    public boolean append(byte[] data, int length) {

        boolean ret = false;

        if (data == null || length <= 0) {
            return ret;
        }

        if (buffer == null) {
            buffer = new byte[length];
            System.arraycopy(data, 0, buffer, 0, length);

        } else {

            byte[] tempArray = new byte[capacity + length];
            System.arraycopy(buffer, 0, tempArray, 0, buffer.length);
            System.arraycopy(data, 0, tempArray, buffer.length, length);

            buffer = tempArray;
        }

        capacity = buffer.length;

        return true;
    }

    /**
     * 获取特定位置数据
     * @param start 开始位置
     * @param length 长度
     * @return 字节数组
     */
    public byte[] getBytes(int start, int length) {

        System.out.println("start="+start + ", length="+length + ", capacity="+capacity);
        if (start < 0 || length < 0 || start > capacity - 1 || (start + length) > capacity) {
            return null;
        }

        limit = start + length;

        byte[] dataArray = new byte[length];
        System.arraycopy(buffer, start, dataArray, 0, length);

        return dataArray;
    }

    public void reset() {

        int remainLength = capacity - limit;

        System.out.println("reset remainLength="+remainLength);

        if (remainLength > 0) {

            byte[] remain = new byte[remainLength];
            System.arraycopy(buffer, limit, remain, 0, remainLength);

            capacity = remainLength;

            buffer = remain;

        } else {
            capacity = 0;
            buffer = null;
        }

        System.out.println("reset capacity="+capacity);
    }
}
