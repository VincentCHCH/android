package com.honeywell.hch.airtouch.plateform.easylinkv3.api;

import android.content.Context;
import android.util.Log;

import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.Helper;
import com.honeywell.hch.airtouch.plateform.easylinkv3.plus.EasyLink_v2;
import com.honeywell.hch.airtouch.plateform.easylinkv3.plus.EasyLink_v3;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EasyLink_plus {
    private static EasyLink_v2 e2;
    private static EasyLink_v3 e3;
    // private static EasyLink_minus minus;
    private static EasyLink_plus me;
    boolean sending = true;
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    private EasyLink_plus(Context ctx) {
        try {
            e2 = EasyLink_v2.getInstence();
            e3 = EasyLink_v3.getInstence();
            // minus = new EasyLink_minus(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EasyLink_plus getInstence(Context ctx) {
        if (me == null) {
            me = new EasyLink_plus(ctx);
        }
        return me;
    }

    public void setSmallMtu(boolean onoff) {
        e3.SetSmallMTU(onoff);
    }

    public void transmitSettings(
            final String ssid,
            final String key,
            final int ipAddress,
            final int sleeptime,
            String extraData,
            final String rc4key) {

        try {
            // final byte[] ssid_byte = ssid.getBytes("UTF-8");
            // final byte[] key_byte = key.getBytes("UTF-8");
            // // int userinfoLen = 6 + extraData.getBytes().length;
            //
            // // final byte[] userinfo = new byte[userinfoLen];
            // final byte[] byteip = new byte[5];
            // byteip[0] = 0x23; // #
            // String strIP = String.format("%08x", ipAddress);
            // System.arraycopy(Helper.hexStringToBytes(strIP), 0, byteip, 1,
            // 4);
            // // if (!"".equals(extraData) || (null != extraData)) {
            // // userinfo[5] = 0x23; // #
            // // System.arraycopy(extraData.getBytes(), 0, userinfo, 6,
            // extraData.getBytes().length);
            // // }

            final byte[] ssid_byte = ssid.getBytes("UTF-8");
            final byte[] key_byte = key.getBytes("UTF-8");

            byte[] userinfo = null;

            if (0 != ipAddress) {
                int userinfoLen = 5 + extraData.getBytes().length;

                userinfo = new byte[userinfoLen];
                String strIP = String.format("%08x", ipAddress);

                if (!"".equals(extraData) || (null != extraData)) {
                    System.arraycopy(extraData.getBytes(), 0, userinfo, 0, extraData.getBytes().length);
                    userinfo[extraData.getBytes().length] = 0x23; // #
                    System.arraycopy(Helper.hexStringToBytes(strIP), 0, userinfo, extraData.getBytes().length + 1, 4);
                } else {
                    userinfo[0] = 0x23; // #
                    System.arraycopy(Helper.hexStringToBytes(strIP), 0, userinfo, 1, 4);
                }
            }

            final byte[] ipPextra = userinfo;

            singleThreadExecutor = Executors.newSingleThreadExecutor();
            sending = true;
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    while (sending) {
                        try {
                            // minus.transmitSettings(ssid, key, ipAddress);
                            e2.transmitSettings(ssid_byte, key_byte, ipPextra, sleeptime);
                            e3.transmitSettings(ssid_byte, key_byte, ipPextra, rc4key, sleeptime);
                            try {
                                Thread.sleep(10 * 1000);
                                e2.stopTransmitting();
                                e3.stopTransmitting();
                                // minus.stopTransmitting();
                                Thread.sleep(3 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void transmitSettings(
            final byte[] ssid_byte,
            final byte[] key_byte,
            final int ipAddress,
            final int sleeptime,
            final byte[] userInfo,
            final String rc4key) {

        try {
            // final byte[] ssid_byte = ssid.getBytes("UTF-8");
            // final byte[] key_byte = key.getBytes("UTF-8");
            // // int userinfoLen = 6 + extraData.getBytes().length;
            //
            // // final byte[] userinfo = new byte[userinfoLen];
            // final byte[] byteip = new byte[5];
            // byteip[0] = 0x23; // #
            // String strIP = String.format("%08x", ipAddress);
            // System.arraycopy(Helper.hexStringToBytes(strIP), 0, byteip, 1,
            // 4);
            // // if (!"".equals(extraData) || (null != extraData)) {
            // // userinfo[5] = 0x23; // #
            // // System.arraycopy(extraData.getBytes(), 0, userinfo, 6,
            // extraData.getBytes().length);
            // // }

//            int ipAddress = phone_ip;

//                //后面5位以23开头，＋ip地址。
//            byte[] userinfo = null;
//
//            if (0 != ipAddress) {
//                int userinfoLen = 5 + extraData.length;
//
//                userinfo = new byte[userinfoLen];
//                String strIP = String.format("%08x", ipAddress);
//
//                if (!"".equals(extraData) || (null != extraData)) {
//                    System.arraycopy(extraData, 0, userinfo, 0, extraData.length);
//                    userinfo[extraData.length] = 0x23; // #
//                    System.arraycopy(Helper.hexStringToBytes(strIP), 0, userinfo, extraData.length + 1, 4);
//                } else {
//                    userinfo[0] = 0x23; // #
//                    System.arraycopy(Helper.hexStringToBytes(strIP), 0, userinfo, 1, 4);
//                }
//            }
            Log.i("---main---", "userInfo: " + EasyLinkEncrptUtil.bytesToHexString(userInfo));

            //后面5位以23开头，＋ip地址。
            byte[] userIp = new byte[5];
            userIp[0] = 0x23; // #
            String s = String.format("%08x", ipAddress);
            System.arraycopy(Helper.hexStringToBytes(s), 0, userIp, 1, 4);

            byte[] tempUserInfo = new byte[userInfo.length + 5];
            System.arraycopy(userInfo, 0, tempUserInfo, 0, userInfo.length);
            System.arraycopy(userIp, 0, tempUserInfo, userInfo.length, userIp.length);

            final byte[] ipPextra = tempUserInfo;
            Log.i("---main---", "ipPextra: " + EasyLinkEncrptUtil.bytesToHexString(ipPextra));
            singleThreadExecutor = Executors.newSingleThreadExecutor();
            sending = true;
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    while (sending) {
                        try {
                            // minus.transmitSettings(ssid, key, ipAddress);
                            e2.transmitSettings(ssid_byte, key_byte, ipPextra, sleeptime);
                            e3.transmitSettings(ssid_byte, key_byte, ipPextra, rc4key, sleeptime);
                            try {
                                Thread.sleep(10 * 1000);
                                e2.stopTransmitting();
                                e3.stopTransmitting();
                                // minus.stopTransmitting();
                                Thread.sleep(3 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTransmitting() {
        // Log.e("easylink", "STOP!!!!");
        sending = false;
        singleThreadExecutor.shutdown();
        e2.stopTransmitting();
        e3.stopTransmitting();
        // minus.stopTransmitting();
    }
}
