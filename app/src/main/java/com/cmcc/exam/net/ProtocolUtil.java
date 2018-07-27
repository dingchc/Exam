package com.cmcc.exam.net;

import com.cmcc.exam.AppLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ding on 6/21/17.
 */

public class ProtocolUtil {

    public static String data1 = "1";

    public static String data = "今日使用火狐浏览器，在测试环境，回归测试客户端公众号保存草稿箱功能，存在如下1个问题，请悉知，具体反馈如下：\n" +
            " \n" +
            "1.       在发件箱选择一次发送人，再次打开发件箱或者草稿箱，不选择发件人也能发送成功，实际为读取的上一次联系人缓存——待修复\n" +
            "2.       编辑纯文本的内容，点击保存按钮，在草稿箱能够查看到记录，且能编辑——测试通过\n" +
            "3.       编辑纯图片的内容，点击保存按钮，在草稿箱能够查看到记录，且能编辑——测试通过\n" +
            "4.       编辑单图片的内容，点击保存按钮，在草稿箱能够查看到记录，且能编辑——测试通过\n" +
            "5.       多图文样式的图文内容没有保存按钮，不能保存到草稿箱——经确认规则如此\n" +
            "6.       草稿箱不保存之前选择的联系人——经确认规则如此\n" +
            "7.       草稿箱发送成功的内容仍停留在草稿箱——经确认暂不修改\n" +
            "8.       草稿箱没有删除按钮——已增加删除按钮，测试通过\n" +
            "9.       草稿箱删除消息，但是草稿箱显示的草稿数量未进行更新——已修复，测试验证通过\n" +
            "10.   对草稿箱内容进行编辑再保存，草稿箱关于此条消息时间未变化——已修复，测试验证通过\n" +
            "11.   不填写内容也能保存在草稿箱——经确认暂不修改\n" +
            "12.   草稿箱选择纯文本内容进行发送，会先弹窗提示true——已修复，测试验证通过";

    public byte[] pack(Protocol protocol) {

        byte[] dataByteArray = null;

        ByteArrayOutputStream bos = null;
        DataOutputStream dos = null;

        try {

            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);

            dos.writeInt(protocol.getPackageLength());
            dos.writeShort(protocol.cmd);

            if (protocol.data != null && protocol.data.length > 0) {

                dos.writeInt(protocol.data.length);
                dos.write(protocol.data);
            }


            dataByteArray = bos.toByteArray();

            AppLogger.i("dataByteArray.length=" + dataByteArray.length);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataByteArray;
    }

    public Protocol unpack(byte[] data) {

        Protocol protocol = null;

        if (data != null && data.length > 0) {

            ByteArrayInputStream bis = null;
            DataInputStream dis = null;

            protocol = new Protocol();

            try {

                bis = new ByteArrayInputStream(data);
                dis = new DataInputStream(bis);

                protocol.cmd = dis.readShort();
                protocol.dataLength = dis.readInt();
                if (protocol.dataLength > 0) {
                    byte[] buffer = new byte[protocol.dataLength];
                    dis.read(buffer);
                    protocol.data = buffer;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return protocol;
    }

    public Protocol serverUnpack(byte[] data) {

        Protocol protocol = null;

        if (data != null && data.length > 0) {

            ByteArrayInputStream bis = null;
            DataInputStream dis = null;

            protocol = new Protocol();

            try {

                bis = new ByteArrayInputStream(data);
                dis = new DataInputStream(bis);

                protocol.cmd = dis.readShort();
                protocol.dataLength = dis.readInt();
                if (protocol.dataLength > 0) {
                    byte[] buffer = new byte[protocol.dataLength];
                    dis.read(buffer);
                    protocol.data = buffer;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return protocol;
    }

    public byte[] serverPack(Protocol protocol) {

        byte[] dataByteArray = null;

        ByteArrayOutputStream bos = null;
        DataOutputStream dos = null;

        try {

            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);

            dos.writeInt(protocol.getPackageLength());
            dos.writeShort(protocol.cmd);

            if (protocol.data != null && protocol.data.length > 0) {

                dos.writeInt(protocol.data.length);
                dos.write(protocol.data);
            }


            dataByteArray = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataByteArray;
    }

    public static class Protocol {
        short cmd = 0x11;
        int dataLength = 0;
        byte[] data;

        public int getPackageLength() {

            int length = 0;

            if (data != null) {
                length += data.length;
                length += 4;
            }

            length += 2;

            return length;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return this.data;
        }
    }

    public static String getUtfString(byte[] data) {

        String ret = "";

        try {
            ret = new String(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
