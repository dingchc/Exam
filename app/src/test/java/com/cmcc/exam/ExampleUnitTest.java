package com.cmcc.exam;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        long startTime = System.currentTimeMillis();
        int NUM = 3;
        for(int i = 0; i < NUM; i ++) {

            //  创建线程
            final int index = i;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    long value = fabic(40);
                    System.out.println("value=" + value);
                }
            };
            thread.start();
            System.out.println("threat is start");
            thread.join();
            System.out.println("threat is join");
        }
        //  打印花费的时间
        System.out.println(System.currentTimeMillis() - startTime);

    }

    static long fabic(int n) {
        if(n < 0) {
            throw new NumberFormatException("不能小于0");
        }
        if(n == 1 || n == 2) {
            return 1;
        }
        return fabic(n - 1) + fabic(n - 2);
    }

    private void runDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

//        String time = sdf.format(calendar.getTime());
//
//        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);

        Date sundayOfLastWeek = calendar.getTime();

        System.out.println("sundayOfLastWeek=" + sdf.format(sundayOfLastWeek));

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
        Date mondayOfLastWeek = calendar.getTime();


        System.out.println("mondayOfLastWeek="+sdf.format(mondayOfLastWeek));
    }

    private void test() {

        System.out.println("Math.sqrt=" + Math.sqrt(4) + ", " + Math.hypot(2, 3));

        System.out.println("min=" + Integer.toBinaryString(Integer.MIN_VALUE));
        System.out.println("max=" + Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println("max=" + Integer.toBinaryString(128));

        System.out.println("====================================");

        long uInt = Integer.MAX_VALUE;

        System.out.println("uInt=" + Long.toBinaryString(uInt));
        System.out.println("uInt(hex)=" + Long.toHexString(uInt));

        System.out.println("swapEndian =" + Long.toBinaryString(swapEndian(uInt)));
        System.out.println("swapEndian(hex) =" + Long.toHexString(swapEndian(uInt)));

        int[] data = new int[4];
        data[0] = 0x34;
        data[1] = 0x12;
        data[2] = 0x0;
        data[3] = 0x0;

        byte[] data1 = {(byte)data[0], (byte)data[1], (byte)data[2], (byte)data[3]};

        System.out.println("readInt(data1) =" + littleEndian2UInt(data1));

        System.out.println("swapEndian(readInt(data1)) =" + Long.toHexString(littleEndian2UInt(data1)));

        System.out.println("swapEndian =" + Long.toBinaryString(swapEndian(swapEndian(uInt))));

        System.out.println("====================================");

        int uShort = Short.MAX_VALUE;

        int[] shortData = new int[2];
        shortData[0] = 0xff;
        shortData[1] = 0x7f;
        byte[] shortDataArray = {(byte)shortData[0], (byte)shortData[1]};

        System.out.println("====================================");

        System.out.println("uShort=" + Integer.toBinaryString(uShort));
        System.out.println("uShort(hex)=" + Integer.toHexString(uShort));

        System.out.println("littleEndian2UShort(shortDataArray)=" + littleEndian2UShort(shortDataArray));
        System.out.println("littleEndian2UShort(shortDataArray)(hex)=" + Integer.toHexString(littleEndian2UShort(shortDataArray)));

        System.out.println("swapEndian=" + Integer.toBinaryString(swapEndian(uShort)));
        System.out.println("swapEndian(hex)=" + Integer.toHexString(swapEndian(uShort)));
        System.out.println("swapEndian=" + Integer.toBinaryString(swapEndian(swapEndian(uShort))));

        System.out.println("====================================");


        ///////////

        System.out.println("====================================");

        long num1 = Integer.MAX_VALUE;
        System.out.println("num1(hex)=" + Long.toHexString(num1));

        byte[] num1LittleArray = int2LittleEndianUInt32(num1);
        System.out.println("num1(hex)=" + Long.toHexString(0xFF & num1LittleArray[0]) + ", " + Long.toHexString(0xFF & num1LittleArray[1]) + ", " + Long.toHexString(0xFF & num1LittleArray[2]) + ", " + Long.toHexString(0xFF & num1LittleArray[3]));


        System.out.println("====================================");

        int num2 = Short.MAX_VALUE;
        System.out.println("num2(hex)=" + Integer.toHexString(num2));

        byte[] num2LittleArray = int2LittleEndianUInt16(num2);
        System.out.println("num2(hex)=" + Long.toHexString(0xFF & num2LittleArray[0]) + ", " + Long.toHexString(0xFF & num2LittleArray[1]));
    }

    /**
     * 大小端互转: UInt
     *
     * @param val 输入
     * @return 转换后的数值
     */
    private long swapEndian(long val) {

        return ((val & 0xFF) << 24) + (((val >> 8) & 0xFF) << 16) + (((val >> 16) & 0xFF) << 8) + ((val >> 24) & 0xFF);
    }

    /**
     * 大小端互转: UShort
     *
     * @param val 输入
     * @return 转换后的数值
     */
    private int swapEndian(int val) {

        return ((val & 0xFF) << 8) + ((val >> 8) & 0xFF);
    }

    /**
     * 大小端互转: UByte
     *
     * @param val 输入
     * @return 转换后的数值
     */
    private short swapEndian(short val) {

        return (short) (((val & 0xF) << 4) + ((val >> 4) & 0xF));
    }

    /**
     * 小端数据转UInt(long)
     *
     * @param buffer 输入
     * @return 转换后的数值
     */
    private long littleEndian2UInt(byte[] buffer) {

        if (buffer == null || buffer.length < 4) {
            throw new IllegalArgumentException("buffer length must be 4");
        }

        return ((buffer[3] & 0xFF) << 24) + ((buffer[2] & 0xFF) << 16) + ((buffer[1] & 0xFF) << 8) + (buffer[0] & 0xFF);
    }

    /**
     * 小端数据转UShort(int)
     *
     * @param buffer 输入
     * @return 转换后的数值
     */
    private int littleEndian2UShort(byte[] buffer) {

        if (buffer == null || buffer.length < 2) {
            throw new IllegalArgumentException("buffer length must be 2");
        }

        return ((buffer[1] & 0xFF) << 8) + (buffer[0] & 0xFF);
    }

    /**
     * 整数转小端UInt32
     *
     * @param value 输入
     * @return 小端UInt32字节序
     */
    public static byte[] int2LittleEndianUInt32(long value) {

        byte[] buffer = new byte[4];

        buffer[0] = (byte) (value & 0xFF);
        buffer[1] = (byte) ((value >>> 8) & 0xFF);
        buffer[2] = (byte) ((value >>> 16) & 0xFF);
        buffer[3] = (byte) ((value >>> 24) & 0xFF);

        return buffer;
    }

    /**
     * 整数转小端UInt16
     *
     * @param value 输入
     * @return 小端UInt16字节序
     */
    public static byte[] int2LittleEndianUInt16(int value) {

        byte[] buffer = new byte[2];

        buffer[0] = (byte) (value & 0xFF);
        buffer[1] = (byte) ((value >>> 8) & 0xFF);

        return buffer;
    }
}