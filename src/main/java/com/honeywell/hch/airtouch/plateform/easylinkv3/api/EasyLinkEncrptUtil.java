package com.honeywell.hch.airtouch.plateform.easylinkv3.api;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Vincent on 13/4/17.
 * symmetrical encyprition: AES,RC4 3DES
 * asymmetrical encyprition: RSA,DSA/DSS
 * HASH arithmetic: MD5 SHA1 ,SHA256
 */

public class EasyLinkEncrptUtil {
    private static final String ALLCHAR = "0123456789";
    //算法名
    private static final String KEY_ALGORITHM = "AES";
    //ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个参数iv
    private final static String algorithmStr = "AES/CBC/PKCS5Padding";
    private final static String algorithmStrNoPadding = "AES/CBC/NoPadding";

    //IV:两种加密公用同一个iv
    public static byte[] sha256Byte16LengthIv;

//    public void test() {
//        String randomString = "4740766382741682";
//        String macId = "04786300022C";
//        String ssid = "Cloud-QA-2.4G";
//        String pwd = "airtouchs";
//        //2 用sha－256加密macId；
//        byte[] sha256Byte = SHA(macId, "SHA-256");
//
//        //3取byte的前16位。这个是iv 就是偏移量。
//        //加密随机数，ssid，password 都需要用到
//        sha256Byte16LengthIv = subByte16(sha256Byte);
//
//        //4将macId转成byte［］,
//        // 5 macIdByte 不足16位，用0补，往后面加；
//        //这个是加密随机数的key
//        byte[] macIdByteKey = changeMacIdToByte(macId);
//
//        //6 Aes128cbc加密 nopadding 使用上面key 和Iv 加密随机数；
//        byte[] encryptByteRandom = encryptAES_CBC_NOPADDING(randomString, macIdByteKey, sha256Byte16LengthIv);
//        String bytesToHexString = bytesToHexString(encryptByteRandom);
//
//        //7 生成加密ssid和password 的key
//        byte[] key = encryptMacIdPwdKey(macId, randomString);
//        String keString = bytesToHexString(key);
//        Log.i("fsfsfs", "keString: " + keString);
//
//        //8 aes128cbc pkcs5 padding 使用上面key 和Iv 加密ssid 和password；
//        byte[] encryptByteSsid = encrypt(ssid, key, sha256Byte16LengthIv);
//        byte[] encryptBytePwd = encrypt(pwd, key, sha256Byte16LengthIv);
//        bytesToHexString(encryptByteSsid);
//        bytesToHexString(encryptBytePwd);
//    }

//    public static byte[] generateEncryptRandom(String macId,String randomString) {
//        //1 生成一个随机数
//        //  randomString = generateString(16);
//        randomString = "4159634046545657";
//        Log.i("---main---", "randomString: " + randomString);
//        //2 用sha－256加密macId；
//        byte[] sha256Byte = SHA(macId, "SHA-256");
//
//        //3取byte的前16位。这个是iv 就是偏移量。
//        //加密随机数，ssid，password 都需要用到
//        sha256Byte16LengthIv = subByte16(sha256Byte);
//        Log.i("---main---", "IV--: " + EasyLinkEncrptUtil.bytesToHexString(sha256Byte16LengthIv));
//        //4将macId转成byte［］,
//        // 5 macIdByte 不足16位，用0补，往后面加；
//        //这个是加密随机数的key
//        byte[] macIdByteKey = changeMacIdToByte(macId);
//
//        //6 Aes128cbc加密 nopadding 使用上面key 和Iv 加密随机数；
////        byte[] encryptByteRandom = encryptAES_CBC_NOPADDING(randomString, macIdByteKey, sha256Byte16LengthIv);
//        byte[] encryptByteRandom = encrypt(randomString, macIdByteKey, sha256Byte16LengthIv, algorithmStrNoPadding);
//        return encryptByteRandom;
//    }

    //for test
    public static byte[] generateEncryptRandom(String macId, String random) {
        //1 生成一个随机数
        //2 用sha－256加密macId；
        byte[] sha256Byte = SHA(macId, "SHA-256");

        //3取byte的前16位。这个是iv 就是偏移量。
        //加密随机数，ssid，password 都需要用到
        sha256Byte16LengthIv = subByte16(sha256Byte);

        //4将macId转成byte［］,
        // 5 macIdByte 不足16位，用0补，往后面加；
        //这个是加密随机数的key
        byte[] macIdByteKey = changeMacIdToByte(macId);

        //6 Aes128cbc加密 nopadding 使用上面key 和Iv 加密随机数；
//        byte[] encryptByteRandom = encryptAES_CBC_NOPADDING(randomString, macIdByteKey, sha256Byte16LengthIv);
        byte[] encryptByteRandom = encrypt(random, macIdByteKey, sha256Byte16LengthIv, algorithmStrNoPadding);
        return encryptByteRandom;
    }

    public static byte[] generateEncryptData(byte[] key, String encryData) {
        //7 生成加密ssid和password 的key
//        byte[] key = encryptMacIdPwdKey(macId, randomStr);
        Log.i("---main---", "key: " + EasyLinkEncrptUtil.bytesToHexString(key));
        //8 aes128cbc pkcs5 padding 使用上面key 和Iv 加密ssid 和password；
        return encrypt(encryData, key, sha256Byte16LengthIv, algorithmStrNoPadding);
    }

    public static String dataPadding(String data) {
        int length = data.length();
        int paddingLength = ((length % 16 == 0) ? 0 : (16 - length % 16));
        StringBuilder mutableInfo = new StringBuilder(data);
        for (int i = 0; i < paddingLength; i++) {
            mutableInfo.append("\0");
        }
        return mutableInfo.toString();
    }

    public static byte[] initUserInfo(String ssid, String password, String macId, String randomString) {
        byte[] ssidkey = encryptMacIdPwdKey(macId, randomString);
        byte[] key = userInfoRandomKey(macId);
        byte[] giv = sha256Byte16LengthIv;
        byte[] randomEncryptData = encrypt(randomString, key, giv, algorithmStrNoPadding);

        String fullStr = ssid + password + randomString;
        byte[] sha1FullData = SHA(fullStr, "SHA-256");
        byte[] hmacSha2FullData = HMACSHA256(sha1FullData, ssidkey);

        byte[] userInfo = new byte[randomEncryptData.length + hmacSha2FullData.length];
        System.arraycopy(randomEncryptData, 0, userInfo, 0, randomEncryptData.length);
        System.arraycopy(hmacSha2FullData, 0, userInfo, randomEncryptData.length, hmacSha2FullData.length);
        return userInfo;
    }


    private static byte[] userInfoRandomKey(String macid) {
        byte[] macIdbyte = macid.getBytes();
        byte[] newByte = new byte[16];
        System.arraycopy(macIdbyte, 0, newByte, 0, macIdbyte.length);
        int oldLength = macIdbyte.length;
        int number = 16 - macIdbyte.length;
        for (int i = 0; i < number; i++) {
            newByte[oldLength + i] = 0x00;
        }
        return newByte;
    }

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 字符串 SHA 加密
     *
     * @return
     */
    private static byte[] SHA(final String strText, final String strType) {
        // 返回值
        String strResult = null;
        byte byteBuffer[] = null;
        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {

            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 類型结果
                byteBuffer = messageDigest.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
        return byteBuffer;
    }
    /*
        hmac sha-256
     */

    public static byte[] HMACSHA256(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    //3取byte的前16位。
    private static byte[] subByte16(byte[] srcPos) {
        byte[] sha256Byte16Length = new byte[16];
        System.arraycopy(srcPos, 0, sha256Byte16Length, 0, 16);
        return sha256Byte16Length;
    }

    //4将macId转成byte［］；
    private static byte[] changeMacIdToByte(String macId) {
//       return macId.getBytes();

        return paddingByte(macId.getBytes());
    }

    //5 macIdByte 不足16位，用0补，往后面加；
    private static byte[] paddingByte(byte[] macIdByte) {
        byte[] paddingByte = new byte[16];
        for (int i = 0; i < 16; i++) {
            if (i < macIdByte.length) {
                paddingByte[i] = macIdByte[i];
            } else {
                paddingByte[i] = 0;
            }
        }
        return paddingByte;
    }

    //6，Aes28cbc加密 nopadding
    public static byte[] encryptAES_CBC_NOPADDING(String encryData, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithmStrNoPadding);
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = encryData.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
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
//        Log.i("fsfsfs", "stringBuilder.toString(): " + stringBuilder.toString());

        return stringBuilder.toString();
    }

    //7 生成加密ssid和password 的key
    //把macId放到随机数中间，进行sha256计算，取前16位。
    public static byte[] encryptMacIdPwdKey(String macId, String randomStr) {
        String random = randomStr.substring(0, 8) + macId + randomStr.substring(8);
        byte[] sha256Byte = SHA(random, "SHA-256");

        return subByte16(sha256Byte);
    }

    /*TODO:
     * 算法/模式/填充                16字节加密后数据长度        不满16字节加密后长度
     * AES/CBC/NoPadding             16                          不支持
     * AES/CBC/PKCS5Padding
     */
    private static byte[] encrypt(String encryData, byte[] keyBytes, byte[] iv, String encryptType) {

        try { // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyBytes.length % base != 0) {
                int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
                keyBytes = temp;
            }
            // 转化成JAVA的密钥格式
            Key key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            // 初始化cipher
            Cipher cipher = Cipher.getInstance(encryptType, "BC");

            byte[] encryptedText = null;
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryData.getBytes());
            return encryptedText;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
