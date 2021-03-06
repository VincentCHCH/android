package com.honeywell.hch.airtouch.plateform.smartlink;

import android.os.Handler;
import android.os.Message;

import com.honeywell.hch.airtouch.library.util.ByteUtil;
import com.honeywell.hch.airtouch.library.util.LogUtil;
import com.honeywell.hch.airtouch.plateform.enrollinterface.IConnectAndDeviceManager;
import com.honeywell.hch.airtouch.plateform.smartlink.udpmode.UDPContentData;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by wuyuan on 11/24/15.
 */
public class ConnectAndFindDeviceManager extends IConnectAndDeviceManager {


    private static final String TAG = "ConnectAndFindDeviceManager";

    private Thread mCountThread = null;
    private Thread mSendCooeeThread = null;
    private Thread mSendThread = null;
    private Thread mReceiveThread = null;


    boolean mSendCooeeThreadDone = false;
    boolean mCountThredThreadDone = false;


    public static final int DEST_DEFAULT_PORT = 4320;

    public static final int SOURCE_DEFAULT_PORT = 33333;

    private static final int MAX_COUNT_TIME = 80;


    private DatagramSocket udpSocket;


    private DatagramSocket receiveudpSocket = null;

    private DatagramPacket receiveudpPacket = null;

    private byte[] contentBytes;


    private static final String PRODUCT_UUID_S = "KSN95Y";
    private static final String PRODUCT_UUID_450S = "KHN6YM";
    private static final String PRODUCT_UUID_STR = "productuuid";
    private static final String MAC_STR_KEY = "mac";
    private static final String CODE_STR_KEY = "code";


    private static final String MAC_HAADER_COOEE = "0000";

    private boolean mSendThreadRun = true;
    private boolean mReceiveThreadRun = true;


    private String deviceIdAddress = "";
    private int receiveTime = 0;

    private Object mLockobj = new Object();


    private int mConnectingCount = 0;

    private FinishCallback mFinishCallback;


    public interface FinishCallback {
        void onFinish();
    }

    public void setFinishCallback(FinishCallback finishCallback) {
        mFinishCallback = finishCallback;
    }

    public ConnectAndFindDeviceManager(Handler handler, String deviceMacwithcolon, String deviceNocolon) {

        mDeviceMacWithcolon = deviceMacwithcolon;
        mDeviceMacWithNocolon = deviceNocolon;
        mActvitiyHandler = handler;
    }


    private void startCountThread() {

        if (mCountThread == null) {
            mCountThread = new Thread() {
                public void run() {
                    while (!mCountThredThreadDone) {

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                            LogUtil.error(TAG, "startCountThread", e);
                        }
                        mConnectingCount++;
                        if (mConnectingCount >= MAX_COUNT_TIME) {
                            if (!mCountThredThreadDone) {
                                Message message = Message.obtain();
                                message.what = CONNECTING_TIMEOUT;
                                mHandler.sendMessage(message);
                            }
                        }
                    }
                }
            };
        }
        if (!mCountThredThreadDone) {
            mCountThread.start();
        }


    }

    /**
     * @param ssid     ssid
     * @param password route's password
     * @param mLocalIp ip
     */
    public void startConnectingAndFinding(final String ssid, final String password, final int mLocalIp) {


        startSendCooeeThread(ssid, password, mLocalIp);

        startCountThread();

        // 发现设备 udp
        constructUDPContentData(1);
        sendUdp();
        receivUdp();
    }

    private void startSendCooeeThread(final String ssid, final String password, final int mLocalIp) {
        if (mSendCooeeThread == null) {
            mSendCooeeThread = new Thread() {
                public void run() {
                    while (!mSendCooeeThreadDone) {
//                        Cooee.send(ssid, password, mLocalIp, mac);

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                            LogUtil.error(TAG, "startCountThread", e);
                        }
                    }
                }
            };
        }
        if (!mSendCooeeThreadDone) {
            mSendCooeeThread.start();
        }
    }

    /**
     * @param type 1 和 3 两种情况
     */
    private void constructUDPContentData(int type) {
        int index = 0;
        UDPContentData udpContentData = new UDPContentData();

        byte[] udpdataBytes;
        if (type == 1) {
            LogUtil.log(LogUtil.LogLevel.INFO, "Main", "constructUDPContentData first udp data");
            udpdataBytes = ByteUtil.getDataBytes(udpContentData.getUdpFirstData());
        } else {
            LogUtil.log(LogUtil.LogLevel.INFO, "Main", "constructUDPContentData third udp data");

            udpContentData.getUdpData().setMac(mDeviceMacWithcolon);
            udpdataBytes = ByteUtil.getDataBytes(udpContentData.getUdpData());
        }


        udpContentData.getUdpcmdHeaderData().setType(type);

        byte[] typeBytes = udpContentData.getUdpcmdHeaderData().getmtypeByte();
        byte[] cmdBytes = udpContentData.getUdpcmdHeaderData().getcmdByte();

        int len = udpdataBytes.length + typeBytes.length + cmdBytes.length;
        udpContentData.getUdpCommonHeaderData().setLen(len);


        int checkSum = 0;
        for (int i = 0; i < udpdataBytes.length; i++) {
            checkSum += udpdataBytes[i];
        }

        for (int i = 0; i < typeBytes.length; i++) {
            checkSum += typeBytes[i];
        }

        for (int i = 0; i < cmdBytes.length; i++) {
            checkSum += cmdBytes[i];
        }

        udpContentData.getUdpCommonHeaderData().setChecksum((byte) (checkSum % 256));

        byte[] lenBytes = udpContentData.getUdpCommonHeaderData().getLenByte();
        byte[] enctypeByte = udpContentData.getUdpCommonHeaderData().getmEnctypeByte();
        byte[] magicByte = udpContentData.getUdpCommonHeaderData().getMagaicByte();
        byte[] checkByte = udpContentData.getUdpCommonHeaderData().getChecksumByte();

        int totalLen = magicByte.length + lenBytes.length + enctypeByte.length + checkByte.length
                + typeBytes.length + cmdBytes.length + udpdataBytes.length;

        synchronized (mLockobj) {
            contentBytes = null;
            contentBytes = new byte[totalLen];

            for (int i = 0; i < magicByte.length; i++) {
                contentBytes[index] = magicByte[i];
                index++;
            }

            for (int i = 0; i < lenBytes.length; i++) {
                contentBytes[index] = lenBytes[i];
                index++;
            }

            for (int i = 0; i < enctypeByte.length; i++) {
                contentBytes[index] = enctypeByte[i];
                index++;
            }

            for (int i = 0; i < checkByte.length; i++) {
                contentBytes[index] = checkByte[i];
                index++;
            }

            for (int i = 0; i < typeBytes.length; i++) {
                contentBytes[index] = typeBytes[i];
                index++;
            }

            for (int i = 0; i < cmdBytes.length; i++) {
                contentBytes[index] = cmdBytes[i];
                index++;
            }

            for (int i = 0; i < udpdataBytes.length; i++) {
                contentBytes[index] = udpdataBytes[i];
                index++;
            }
        }

    }


    private void sendUdp() {
        if (mSendThread == null || !mSendThread.isAlive()) {
            mSendThread =
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (mSendThreadRun) {
                                DatagramPacket dataPacket = null;

                                try {
                                    if (udpSocket == null) {
                                        udpSocket = new DatagramSocket(null);
                                        udpSocket.setReuseAddress(true);
                                        udpSocket.bind(new InetSocketAddress(SOURCE_DEFAULT_PORT));
                                    }

                                    dataPacket = new DatagramPacket(contentBytes, contentBytes.length);
                                    dataPacket.setLength(contentBytes.length);

                                    InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
                                    dataPacket.setPort(DEST_DEFAULT_PORT);
                                    dataPacket.setAddress(broadcastAddr);
                                } catch (Exception e) {
                                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "==1 =" + e.toString());
                                }
                                // while( start ){
                                try {
                                    if (udpSocket != null) {
                                        udpSocket.send(dataPacket);
                                    }
                                } catch (Exception e) {
                                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "==2 =" + e.toString());
                                }
                                // }

                                if (udpSocket != null) {
                                    udpSocket.close();
                                    udpSocket = null;
                                }

                                try {
                                    //发udp的包不能太频繁，会导致设备反应不过来。
                                    Thread.sleep(600);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Thread.currentThread().interrupt();
                                    LogUtil.error(TAG, "startCountThread", e);
                                }
                            }

                        }

                    });
        }
        mSendThread.start();

    }


    private void receivUdp() {
        if (mReceiveThread == null) {
            mReceiveThread =
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            byte[] data = new byte[512];
                            try {
                                if (receiveudpSocket == null) {
                                    receiveudpSocket = new DatagramSocket(null);
                                    receiveudpSocket.setReuseAddress(true);
                                    receiveudpSocket.bind(new InetSocketAddress(SOURCE_DEFAULT_PORT));
                                }
                                receiveudpPacket = new DatagramPacket(data, data.length);
                            } catch (SocketException e) {

                                if (receiveudpSocket != null) {
                                    receiveudpSocket.close();
                                }

                                LogUtil.error(TAG, "receivUdp", e);
                                return;
                            }

                            while (mReceiveThreadRun) {
                                try {
                                    receiveudpSocket.receive(receiveudpPacket);
                                    if (null != receiveudpPacket.getAddress()) {
                                        //解析数据
                                        String thisUdpIp = receiveudpPacket.getAddress().toString();
                                        LogUtil.log(LogUtil.LogLevel.INFO, "WebViewMainActivity", "thisUdpIp = " + thisUdpIp);
                                        if (receiveTime == 0) {
                                            if (pareseDeviceFirstUdpBytes(data, thisUdpIp)) {
                                                LogUtil.log(LogUtil.LogLevel.INFO, "WebViewMainActivity", "receive second upd package success");
                                                //send the third udp to device
                                                constructUDPContentData(3);

                                            }
                                        } else if (receiveTime == 1) {
                                            if (pareseDeviceLastUdpBytes(data, thisUdpIp)) {
                                                LogUtil.log(LogUtil.LogLevel.INFO, "WebViewMainActivity", "receive last upd package success");
                                                Message message = Message.obtain();
                                                message.what = PROCESS_END;
                                                mHandler.sendMessage(message);
                                                return;
                                            }
                                        }

                                    }
                                } catch (Exception e) {
                                    LogUtil.error("Main", "Exception = ", e);
                                }

                            }

                        }
                    });
        }
        mReceiveThread.start();

    }


    private boolean pareseDeviceFirstUdpBytes(byte[] deviceBytes, String ipAddress) {
        try {
            String deviceUdpStr = new String(deviceBytes);
            LogUtil.log(LogUtil.LogLevel.INFO, "WebViewMainActivity", "deviceUdpStr = " + deviceUdpStr);

            int left = deviceUdpStr.indexOf(PRODUCT_UUID_STR);
            if (left >= 0) {

                int macleft = deviceUdpStr.indexOf(MAC_STR_KEY);
                if (macleft >= 0) {
                    String massageStr = deviceUdpStr.substring(macleft).toLowerCase();
                    String lowMac = mDeviceMacWithcolon.toLowerCase();
                    if (massageStr.contains(lowMac)) {
                        mSendCooeeThreadDone = true;
                        deviceIdAddress = ipAddress;
                        receiveTime++;
                        LogUtil.log(LogUtil.LogLevel.INFO, "WebViewMainActivity", "return true");
                        return true;
                    }

                }
            }
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "pareseDeviceFirstUdpBytes Exception = " + e.toString());
        }
        return false;
    }

    private boolean pareseDeviceLastUdpBytes(byte[] deviceBytes, String ipAddress) {
        try {
            if (!"".equals(deviceIdAddress) && deviceIdAddress.equalsIgnoreCase(ipAddress)) {
                String deviceUdpStr = new String(deviceBytes);
                int left = deviceUdpStr.indexOf(CODE_STR_KEY);
                if (left >= 0) {
                    mSendThreadRun = false;
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, e.toString());
        }
        return false;
    }

    @Override
    protected void endAllThread() {

        if (receiveudpSocket != null && !receiveudpSocket.isClosed()) {
            receiveudpSocket.close();
        }
        receiveudpSocket = null;

        mSendThreadRun = false;
        mReceiveThreadRun = false;
        mSendCooeeThreadDone = true;
        mCountThredThreadDone = true;
        receiveTime = 0;
        mConnectingCount = 0;

        mCountThread = null;
        mSendCooeeThread = null;
        mSendThread = null;
        mReceiveThread = null;
    }

    @Override
    protected void restAllThread() {
        mSendThreadRun = true;
        mReceiveThreadRun = true;
        mSendCooeeThreadDone = false;
        mCountThredThreadDone = false;
        receiveTime = 0;
        mConnectingCount = 0;

        mCountThread = null;
        mSendCooeeThread = null;
        mSendThread = null;
        mReceiveThread = null;
        mCheckWifiConnectThread = null;
    }

}
