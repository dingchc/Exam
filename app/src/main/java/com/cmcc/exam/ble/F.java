package com.cmcc.exam.ble;

import java.util.Arrays;

/**
 * Created by ding on 27/02/2018.
 */

public class F {
    static byte q = 2;
    static byte r = 3;

    public F() {
    }

    public static byte[] a(short a) {
        byte[] b = new byte[]{(byte)(a >> 8), (byte)a};
        return b;
    }

    public static void a(short a, byte[] b, int offset) {
        b[offset] = (byte)(a >> 8);
        b[offset + 1] = (byte)a;
    }

    public static short b(byte[] b) {
        return (short)((b[0] & 255) << 8 | b[1] & 255);
    }

    public static short a(byte[] b, int offset) {
        return (short)((b[offset] & 255) << 8 | b[offset + 1] & 255);
    }

    public static void a(long a, byte[] b, int offset) {
        b[offset + 0] = (byte)((int)(a >> 56));
        b[offset + 1] = (byte)((int)(a >> 48));
        b[offset + 2] = (byte)((int)(a >> 40));
        b[offset + 3] = (byte)((int)(a >> 32));
        b[offset + 4] = (byte)((int)(a >> 24));
        b[offset + 5] = (byte)((int)(a >> 16));
        b[offset + 6] = (byte)((int)(a >> 8));
        b[offset + 7] = (byte)((int)a);
    }

    public static long b(byte[] b, int offset) {
        return ((long)b[offset + 0] & 255L) << 56 | ((long)b[offset + 1] & 255L) << 48 | ((long)b[offset + 2] & 255L) << 40 | ((long)b[offset + 3] & 255L) << 32 | ((long)b[offset + 4] & 255L) << 24 | ((long)b[offset + 5] & 255L) << 16 | ((long)b[offset + 6] & 255L) << 8 | ((long)b[offset + 7] & 255L) << 0;
    }

    public static long c(byte[] b) {
        return (long)((b[0] & 255) << 56 | (b[1] & 255) << 48 | (b[2] & 255) << 40 | (b[3] & 255) << 32 | (b[4] & 255) << 24 | (b[5] & 255) << 16 | (b[6] & 255) << 8 | b[7] & 255);
    }

    public static byte[] b(long a) {
        byte[] b = new byte[]{(byte)((int)(a >> 56)), (byte)((int)(a >> 48)), (byte)((int)(a >> 40)), (byte)((int)(a >> 32)), (byte)((int)(a >> 24)), (byte)((int)(a >> 16)), (byte)((int)(a >> 8)), (byte)((int)(a >> 0))};
        return b;
    }

    public static int d(byte[] b) {
        return (b[0] & 255) << 24 | (b[1] & 255) << 16 | (b[2] & 255) << 8 | b[3] & 255;
    }

    public static int c(byte[] b, int offset) {
        return (b[offset++] & 255) << 24 | (b[offset++] & 255) << 16 | (b[offset++] & 255) << 8 | b[offset++] & 255;
    }

    public static byte[] f(int a) {
        byte[] b = new byte[]{(byte)(a >> 24), (byte)(a >> 16), (byte)(a >> 8), (byte)a};
        return b;
    }

    public static void a(int a, byte[] b, int offset) {
        b[offset++] = (byte)(a >> 24);
        b[offset++] = (byte)(a >> 16);
        b[offset++] = (byte)(a >> 8);
        b[offset++] = (byte)a;
    }

    public static byte[] G(String hexString) {
        if(hexString != null && hexString.length() > 0) {
            hexString = hexString.toLowerCase();
            byte[] byteArray = new byte[hexString.length() / 2];
            int k = 0;

            for(int i = 0; i < byteArray.length; ++i) {
                byte high = (byte)(Character.digit(hexString.charAt(k), 16) & 255);
                byte low = (byte)(Character.digit(hexString.charAt(k + 1), 16) & 255);
                byteArray[i] = (byte)(high << 4 | low);
                k += 2;
            }

            return byteArray;
        } else {
            return null;
        }
    }

    public static String e(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if(src != null && src.length > 0) {
            for(int i = 0; i < src.length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if(hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv + " ");
            }

            return stringBuilder.toString().toUpperCase();
        } else {
            return null;
        }
    }

    public static String[] f(byte[] src) {
        if(src != null && src.length > 0) {
            String[] str = new String[src.length];

            for(int i = 0; i < src.length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if(hv.length() < 2) {
                    hv = hv + "0";
                }

                str[i] = hv.toUpperCase();
            }

            return str;
        } else {
            return null;
        }
    }

    static byte[] g(byte[] bbb) {
        try {
            for(int i = 0; i < bbb.length; ++i) {
                if(bbb[i] != 0) {
                    for(int j = bbb.length; j > 0; --j) {
                        if(bbb[j - 1] != 0) {
                            byte[] b = new byte[j - i];
                            System.arraycopy(bbb, i, b, 0, j - i);
                            return b;
                        }
                    }
                }
            }

            return new byte[0];
        } catch (Exception var4) {
            return new byte[0];
        }
    }

    static byte[] a(byte[] byte_1, byte[] byte_2) {
        if(byte_1 != null && byte_2 == null) {
            return byte_1;
        } else if(byte_1 == null && byte_2 != null) {
            return byte_2;
        } else if(byte_1 != null && byte_2 != null) {
            byte[] byte_3 = new byte[byte_1.length + byte_2.length];
            System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
            System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
            return byte_3;
        } else {
            return new byte[0];
        }
    }

    static int h(byte[] s) {
        byte p = 0;

        for(int i = 1; i < s.length; ++i) {
            p ^= s[i];
        }

        return p;
    }

    public static boolean i(byte[] buff) {
        try {
            if(buff != null && buff.length > 8 && buff[0] == q && buff[buff.length - 2] == r) {
                byte[] datalen = new byte[]{buff[1], buff[2]};
                int datal = Integer.parseInt(j(datalen));
                if(datal + 5 == buff.length && buff[buff.length - 1] == h(Arrays.copyOf(buff, buff.length - 1))) {
                    return true;
                }
            }

            return false;
        } catch (Exception var3) {
            return false;
        }
    }

    static byte[] H(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if(mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte[] abt = new byte[len];
        if(len >= 2) {
            len /= 2;
        }

        byte[] bbt = new byte[len];
        abt = asc.getBytes();

        for(int p = 0; p < asc.length() / 2; ++p) {
            int j;
            if(abt[2 * p] >= 48 && abt[2 * p] <= 57) {
                j = abt[2 * p] - 48;
            } else if(abt[2 * p] >= 97 && abt[2 * p] <= 122) {
                j = abt[2 * p] - 97 + 10;
            } else {
                j = abt[2 * p] - 65 + 10;
            }

            int k;
            if(abt[2 * p + 1] >= 48 && abt[2 * p + 1] <= 57) {
                k = abt[2 * p + 1] - 48;
            } else if(abt[2 * p + 1] >= 97 && abt[2 * p + 1] <= 122) {
                k = abt[2 * p + 1] - 97 + 10;
            } else {
                k = abt[2 * p + 1] - 65 + 10;
            }

            int a = (j << 4) + k;
            byte b = (byte)a;
            bbt[p] = b;
        }

        return bbt;
    }

    public static String j(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            temp.append((byte)((bytes[i] & 240) >>> 4));
            temp.append((byte)(bytes[i] & 15));
        }

        return temp.toString().substring(0, 1).equalsIgnoreCase("0")?temp.toString().substring(1):temp.toString();
    }
}