package com.cmcc.exam.ble;

import android.text.TextUtils;

import com.cmcc.exam.AppLogger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ding on 27/02/2018.
 */

public class EplusBleTest {

    static char serNo = 0;
    static String startsWith = "ab";
    static String endsWith = "ab";

    public static byte[] setEHandDateAndTime(String time) {
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
            print(writeBuffer);

        } catch (ParseException var12) {
            var12.printStackTrace();
        }

        return writeBuffer;

    }

    public static void print(byte[] buffer) {
        for (byte value : buffer) {
            AppLogger.i(", " + (0xFF & value));
        }
    }

    public static String convertDecimalToBinary1(int value, int num) {
        String str = Integer.toHexString(value);

        for (int i = str.length(); i < num; ++i) {
            str = "0" + str;
        }

        return str;
    }

    public static byte[] hexString2Bytes(String hexStr) {
        byte[] b = new byte[hexStr.length() / 2];
        int j = 0;

        for (int i = 0; i < b.length; ++i) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) (parse(c0) << 4 | parse(c1));
        }

        return b;
    }

    private static int parse(char c) {
        return c >= 97 ? c - 97 + 10 & 15 : (c >= 65 ? c - 65 + 10 & 15 : c - 48 & 15);
    }

    public static byte[] getWriteBuffer(String writeData) throws IOException {

        byte[] writeBuffer = null;

        String sendInstructionRepertoire;

        StringBuffer str = new StringBuffer(writeData);
        String serNo = calculateSerNo();
        str.insert(startsWith.length() + 1, serNo);
        String sunCheck = getSumCheck(str.substring(startsWith.length() + 1, str.length() - endsWith.length()).replace(" ", "")) + " ";
        str.insert(str.length() - endsWith.length(), sunCheck);
        sendInstructionRepertoire = startsWith + C(str.substring(startsWith.length(), str.length() - endsWith.length())) + endsWith;

        sendInstructionRepertoire = sendInstructionRepertoire.replaceAll(" ", "");
        if (sendInstructionRepertoire.startsWith(startsWith) && sendInstructionRepertoire.endsWith(endsWith)) {
            writeBuffer = hexString2Bytes(sendInstructionRepertoire);
        }

        return writeBuffer;
    }

    public static String C(String data) {
        data = data.replace("ab", "Ac 01");
        data = data.replace("ac", "Ac 02");
        data = data.replace("AB", "aC 01");
        data = data.replace("AC", "aC 02");
        return data;
    }



    private static String calculateSerNo() {
        ++serNo;
        if (serNo == 171 || serNo == '각') {
            serNo += 2;
        }

        if (serNo == 172 || serNo == '갂') {
            ++serNo;
        }

        String str = integerTo16Intstring(serNo, false);

        if (serNo >= '\uffff') {
            serNo = 0;
        }

        return str;
    }

    private static String integerTo16Intstring(int num, boolean delHighZero) {
        byte[] result;
        if (delHighZero) {
            num /= 2;
            result = new byte[]{(byte) (num & 255)};
            return F.e(result);
        } else {
            result = new byte[]{(byte) (num >> 8 & 255), (byte) (num & 255)};
            return F.e(result);
        }
    }

    private static String getSumCheck(String data) {
        if (data == null || "".equals(data)) {
            return "";
        } else {
            int total = 0;
            int len = data.length();

            String hex;
            for (int num = 0; num < len; num += 2) {
                hex = data.substring(num, num + 2);
                total += Integer.parseInt(hex, 16);
            }

            hex = "";
            int mod = total % 256;
            hex = Integer.toHexString(mod);
            len = hex.length();
            if (len < 2) {
                hex = "0" + hex;
            }

            return hex;
        }
    }

    int PNum = 15;

    public void BluetoothWrite(byte[] writeBuffer) {

        List<byte[]> writedata = new ArrayList<>();

        if (writeBuffer != null) {
            int num = writeBuffer.length / this.PNum;
            int yushu = writeBuffer.length % this.PNum;
            if (yushu != 0) {
                ++num;
            }

            for (int i = 0; i < num; ++i) {
                byte[] bytes;
                int n;
                if (yushu != 0 && i == num - 1) {
                    bytes = new byte[yushu];

                    for (n = 0; n < yushu; ++n) {
                        bytes[n] = writeBuffer[n + i * this.PNum];
                    }
                    writedata.add(bytes);
                } else {
                    bytes = new byte[this.PNum];

                    for (n = 0; n < this.PNum; ++n) {
                        bytes[n] = writeBuffer[n + i * this.PNum];
                    }

                    writedata.add(bytes);
                }
            }
        }

        AppLogger.i(" writedata.get(0).length=" + writedata.get(0).length);

    }

    static StringBuffer strBuffer = new StringBuffer();

    public static void appendBoxBuffer(StringBuffer sb) {
        strBuffer.append(sb);
    }

    public static void analyzeData() {

        AppLogger.i("analyzeData = " + strBuffer);

        if(strBuffer.length() > 0) {

            int firstIR = strBuffer.indexOf(startsWith);
            if(firstIR != 0 && firstIR != -1) {
                strBuffer.delete(0, firstIR);
                firstIR = strBuffer.indexOf(startsWith);
            }

            int secondIR = strBuffer.indexOf(startsWith, firstIR + startsWith.length());

            AppLogger.i("firstIR="+firstIR + ", secondIR="+secondIR);

            if(firstIR > -1 && secondIR > firstIR + 3) {
                String dataIR = strBuffer.substring(firstIR + startsWith.length() + 1, secondIR);
                dataIR = D(dataIR);
                if(strBuffer.length() > secondIR + startsWith.length()) {
                    if(strBuffer.length() == secondIR + startsWith.length() + 1) {
                        strBuffer.delete(firstIR, secondIR + startsWith.length() + 1);
                    } else {
                        strBuffer.delete(firstIR, secondIR);
                        if(strBuffer.length() >= 5 && strBuffer.substring(0, 5).equalsIgnoreCase("ab ab")) {
                            strBuffer.delete(0, 3);
                        }
                    }
                } else if(strBuffer.length() == secondIR + startsWith.length()) {
                    strBuffer.delete(firstIR, secondIR + startsWith.length());
                }

                String validData = dataIR.substring(0, dataIR.length() - 3);
                AppLogger.i("validData="+validData);

                checkSumCheck(validData.replace(" ", ""), dataIR.substring(dataIR.length() - 3, dataIR.length()));

                if(!TextUtils.isEmpty(validData)) {
                    parseData(validData);
                }

                if(strBuffer.length() > 0) {
                    analyzeData();
                }
            } else if(secondIR != -1 && firstIR != -1) {
                if(strBuffer.length() >= secondIR + startsWith.length()) {
                    strBuffer.delete(firstIR, secondIR - 1);
                }

                if(strBuffer.length() > 0) {
                    analyzeData();
                }
            }
        }

    }

    public static void parseData(String dataValue) {

        String[] dataIRs = dataValue.split(" ");
        int getchanel;
        int numsings;
        int end;
        int succed;
        String mv;
        String bv;
        int start;
        if(dataIRs.length > 6 && "51".equals(dataIRs[2])) {
            if("00".equals(dataIRs[3])) {
                succed = Integer.parseInt(dataIRs[4], 16);
                getchanel = Integer.parseInt(dataIRs[5], 16);

                AppLogger.e("succed=" + succed + "---" + succed);

                for(numsings = 0; numsings < getchanel; ++numsings) {
                    mv = "";
                    bv = "";
                    end = 6 + numsings * 10;
                    start = (numsings + 1) * 10 + 6;
                    start = start > dataIRs.length?dataIRs.length:start;

                    for(int i = end; i < start; ++i) {
                        if(i < end + 2) {
                            mv = mv + dataIRs[i] + " ";
                        }

                        if(i >= end + 2 && i < end + 2 + 8) {
                            bv = bv + dataIRs[i];
                        }
                    }

                    AppLogger.e("CursorSdk", bv + "---" + mv);
                }
            } else if("01".equals(dataIRs[3])) {
                succed = Integer.parseInt(dataIRs[4], 16);
                getchanel = Integer.parseInt(dataIRs[5], 16);

                for(numsings = 0; numsings < getchanel; ++numsings) {
                    mv = "";
                    bv = "";
                    end = 6 + numsings * 10;
                    start = (numsings + 1) * 10 + 6;
                    start = start > dataIRs.length?dataIRs.length:start;

                    for(end = end; end < start; ++end) {
                        if(end < end + 2) {
                            mv = mv + dataIRs[end] + " ";
                        }

                        if(end >= end + 2 && end < end + 2 + 8) {
                            bv = bv + dataIRs[end];
                        }
                    }

                    AppLogger.e("CursorSdk", bv + "---" + mv);
                }
            }
        }
    }

    private static boolean checkSumCheck(String data, String sign) {
        if(!TextUtils.isEmpty(data) && !TextUtils.isEmpty(sign)) {
            String checksum = getSumCheck(data);
            return checksum.trim().equals(sign.trim());
        } else {
            return false;
        }
    }

    public static String D(String data) {
        data = data.replace("AC 01", "AB");
        data = data.replace("AC 02", "AC");
        data = data.replace("ac 01", "ab");
        data = data.replace("ac 02", "ac");
        return data;
    }
}
