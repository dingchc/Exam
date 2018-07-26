package com.aspirecn.exam.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ding on 6/21/17.
 */

public class MySocketServer {

    private static final String IP = "yn.51jiaxiaotong.com";

    private static final int PORT = 36330;


    public static void main(String args[]) {

        try {

            String property = System.getProperty("java.library.path");
            System.out.println(property);

            byte[] bytes = new byte[20];

            boolean isException = false;

            try {

                System.out.println("Server socket start ...");
                ServerSocket serverSocket = new ServerSocket(PORT);

                while (true) {

                    System.out.println("Server socket accept ...");
                    Socket socket = serverSocket.accept();

                    System.out.println("Server socket end ...");

                    ReadWorker serverWorker = new ReadWorker(socket);
                    serverWorker.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Server socket throw exception ...");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static class ReadWorker extends Thread {

        private static int receiveCnt = 0;
        private Socket socket;

        public ReadWorker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            System.out.println("socket accept to read.");

            try {

                InputStream is = socket.getInputStream();

                byte[] buffer = new byte[2048];
                int length = 0;

                int bodyLength = 0;

                MyByteBuffer myByteBuffer = new MyByteBuffer();

                while ((length = is.read(buffer)) > 0) {

                    System.out.println("length=" + length);

                    myByteBuffer.append(buffer, length);

                    if (myByteBuffer.capacity < 4) {
                        System.out.println("continue ##");
                        continue;
                    } else {

                        bodyLength = getBodyLength(myByteBuffer.buffer);
                        System.out.println("bodyLength=" + bodyLength);

                        if (bodyLength > 0) {

                            if (myByteBuffer.capacity - 4 < bodyLength) {
                                System.out.println("continue **");
                                continue;
                            } else {

                                System.out.println("will decode length1=" + (myByteBuffer.capacity - 4 ) + ", bodyLength="+bodyLength);

                                byte[] data = myByteBuffer.getBytes(4, bodyLength);
                                decodeData(data);
                                myByteBuffer.reset();

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("socket is end");
        }

        private void decodeData(byte[] data) {

            if (data != null) {
                receiveCnt ++;
                ProtocolUtil protocolUtil = new ProtocolUtil();
                ProtocolUtil.Protocol protocol = protocolUtil.serverUnpack(data);

                System.out.println("[" + receiveCnt + "] data=" + ProtocolUtil.getUtfString(protocol.data) + "[*]");

                testReply();
            }
        }

        private void testReply() {

            ProtocolUtil.Protocol outputProtocol = new ProtocolUtil.Protocol();
            try {

                ProtocolUtil protocolUtil = new ProtocolUtil();
                outputProtocol.data = (String.valueOf(receiveCnt)).getBytes("utf-8");
                writeData(protocolUtil.serverPack(outputProtocol));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * 读取包长度
         *
         * @param read
         * @return
         */
        private int getBodyLength(byte[] read) {

            int bodyLength = 0;

            if (read != null && read.length >= 4) {
                bodyLength = (0xFF & read[3]) + ((0xFF & read[2]) << 8) + ((0xFF & read[1]) << 16) + ((0xFF & read[0]) << 24);
            }

            return bodyLength;
        }


        /**
         * 写数据
         *
         * @param data
         */
        public void writeData(byte[] data) {

            try {
                socket.getOutputStream().write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
