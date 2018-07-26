package com.aspirecn.exam.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 类名称: AESUtils
 * 类描述: AES加密解密方法
 * 创建人: 陈书东
 * 创建时间: 2016/9/2 17:59
 * 修改人: 无
 * 修改时间: 无
 * 修改备注: 无
 */
public class AESUtils {

    //偏移量
    private static final String IV = "5954836451581950";

    //H5偏移量
    private static final String H5_IV = "5075428636499153";

    //密钥
    private static final String secretkey = "l0rRUg0bjqfLTnVgMGU6B5BQRAZF0icC";

    //H5密钥
    private static final String H5_secretkey = "COHeJfoWQgaYBuna";

    //加密
    public static String encrypt(String strIn) throws Exception {
        SecretKeySpec skeySpec = getKey(secretkey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(strIn.getBytes("UTF-8"));
//        return org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
        return Base64.encode(encrypted);
    }

    //加密
    public static String encryptH5(String strIn) throws Exception {
        SecretKeySpec skeySpec = getKey(H5_secretkey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(H5_IV.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(strIn.getBytes("UTF-8"));
//        return org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
        return Base64.encode(encrypted);
    }

    //解密
    public static String decrypt(String strIn) throws Exception {
        strIn = strIn.trim();
        SecretKeySpec skeySpec = getKey(secretkey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//        byte[] encrypted1 = org.apache.commons.codec.binary.Base64.decodeBase64(strIn);
        byte[] encrypted1 = Base64.decode(strIn);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, "UTF-8");
        return originalString;
    }

    // 获取key
    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes("UTF-8");
        byte[] arrB = new byte[16];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");
        return skeySpec;
    }
}
