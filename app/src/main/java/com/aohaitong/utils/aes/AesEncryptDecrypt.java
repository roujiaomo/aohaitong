package com.aohaitong.utils.aes;

import com.aohaitong.utils.base.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptDecrypt {

    private static final String ALGORITHM = "AES";
    private static final String charsetName = "GBK";
    private static final String padding = "AES/ECB/PKCS5Padding";
    private static final String sKey = "XW7Yv39LaQjh0g==";

    /**
     * 字节密码加密返回字节
     */
    public static byte[] encryptByteByByte(byte[] data, String charset, String pad) throws Exception {

        // 判断Key是否正确
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位、24位、32位
        if (sKey.length() != 16 && sKey.length() != 24 && sKey.length() != 32) {
            System.out.print("Key长度不是16位、24位、32位");
            return null;
        }
        byte[] raw = null;
        if (Utils.notEmpty(charset)) {
            raw = sKey.getBytes(charset);
        } else {
            raw = sKey.getBytes();
        }
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(pad);//"算法/模式/补码方式"
//        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;

    }

    /**
     * 16进制字符加密返回16进制加密字符串(默认编码方式)
     */
    public static String encryptHexStrByHexStr(String sSrc) throws Exception {

        return encryptHexStrByHexStr(sSrc, charsetName, padding);

    }

    /**
     * 16进制字符加密返回16进制加密字符串（指定编码方式）
     */
    public static String encryptHexStrByHexStr(String sSrc, String charset, String pad) throws Exception {

        return bytesToHexString(encryptByteByByte(hexStringToBytes(sSrc), charset, pad));

    }


    /**
     * 字符加密返回16进制字符串字符串(默认编码方式)
     */
    public static String encryptHexStrByStr(String sSrc) throws Exception {
        return encryptHexStrByStr(sSrc, charsetName, padding);
    }


    /**
     * 字符加密返回16进制字符串字符串（指定编码方式）
     */
    public static String encryptHexStrByStr(String sSrc, String charset, String pad) throws Exception {

        return bytesToHexString(encryptByteByByte(sSrc.getBytes(charset), charset, pad));

    }


    /**
     * 字节解密返回字节
     */
    public static byte[] decryptByteByByte(byte[] encryptedData, String charset, String pad) throws Exception {

        // 判断Key是否正确
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位、24位、32位
        if (sKey.length() != 16 && sKey.length() != 24 && sKey.length() != 32) {
            System.out.print("Key长度不是16位、24位、32位");
            return null;
        }
        byte[] raw = null;
        if (Utils.notEmpty(charset)) {
            raw = sKey.getBytes(charset);
        } else {
            raw = sKey.getBytes();
        }
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(pad);
//            Cipher cipher = Cipher.getInstance("AES/ECB/no Padding");

        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypt = cipher.doFinal(encryptedData);
        return decrypt;

    }


    /**
     * 16进度字符解密返回字符(默认编码方式)
     */
    public static String decryptStrByHexStr(String sSrc) throws Exception {

        return decryptStrByHexStr(sSrc, charsetName, padding);

    }

    /**
     * 16进度字符解密返回字符（指定编码方式）
     */
    public static String decryptStrByHexStr(String sSrc, String charset, String pad) throws Exception {

        return new String(decryptByteByByte(hexStringToBytes(sSrc), charset, pad), charset);

    }


    /**
     * 16进度字符解密返回16进制字符(默认编码方式)
     */
    public static String decryptHexStrByHexStr(String sSrc) throws Exception {

        return decryptHexStrByHexStr(sSrc, charsetName, padding);

    }

    /**
     * 16进度字符解密返回16进制字符（指定编码方式）
     */
    public static String decryptHexStrByHexStr(String sSrc, String charset, String pad) throws Exception {

        return bytesToHexString(decryptByteByByte(hexStringToBytes(sSrc), charset, pad));

    }

    /**
     * byte[] 转16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * 16进制字符串 转byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
