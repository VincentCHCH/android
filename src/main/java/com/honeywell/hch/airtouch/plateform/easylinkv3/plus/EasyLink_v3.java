package com.honeywell.hch.airtouch.plateform.easylinkv3.plus;

import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.ComHelper;
import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.SinRC4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class EasyLink_v3 {
	private static int START_FLAG1 = 0x5AA;
	private static int START_FLAG2 = 0x5AB;
	private static int START_FLAG3 = 0x5AC;
	private static int UDP_START_PORT = 50000;
	private static boolean stopSending;
	private byte key[] = new byte[65];
	private byte ssid[] = new byte[65];
	private byte user_info[] = new byte[65];

	//if send ipaddress to devices
	private boolean issendip = false;

	private static byte send_data[] = new byte[128];
	private static byte buffer[] = new byte[1500];
	// private static DatagramSocket udpSocket;
	private static int len;
	private InetAddress address = null;
	private DatagramPacket send_packet = null;
	private int port;
	private static EasyLink_v3 e3;
	private boolean small_mtu;

	private EasyLink_v3() {
		this.port = 0;
		stopSending = false;
	}

	public static EasyLink_v3 getInstence() {
		if (e3 == null) {
			e3 = new EasyLink_v3();
		}
		return e3;
	}

	/*
	 * private void getBssid() { wifi = (WifiManager)
	 * context.getSystemService(Context.WIFI_SERVICE); info =
	 * wifi.getConnectionInfo(); String bssid_str = info.getBSSID(); bssid_str =
	 * bssid_str.replace(":", ""); bssid = Helper.hexStringToBytes(bssid_str); }
	 */
	public void transmitSettings(byte[] Ssid, byte[] Key, byte[] Userinfo, String rc4key, final int sleeptime) {
		try {
			this.address = InetAddress.getByName("255.255.255.255");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(ComHelper.checkPara(rc4key)){
			// encrypt
			this.ssid = SinRC4.encry_RC4_byte(Ssid, rc4key);
			this.key = SinRC4.encry_RC4_byte(Key, rc4key);
			if(null != Userinfo){
				issendip = true;
				this.user_info = SinRC4.encry_RC4_byte(Userinfo, rc4key);
			}
		}else{
			// no encryption
			this.ssid = Ssid;
			this.key = Key;
			if(null != Userinfo) {
				issendip = true;
				this.user_info = Userinfo;
			}
		}

		int i = 0, j;
		short checksum = 0;
		// getBssid();
		// udpSocket = new DatagramSocket();
		// udpSocket.setBroadcast(true);

		send_data[i++] = (byte) (1 + 1 + 1 + ssid.length + key.length + (issendip?user_info.length:0) + 2);
		// len(total) + len(ssid) +
		// len(key) + ssid + key +
		// user_info + checksum

		send_data[i++] = (byte) ssid.length;
		send_data[i++] = (byte) key.length;

		for (j = 0; j < ssid.length; j++, i++) {
			send_data[i] = ssid[j];
		}

		for (j = 0; j < key.length; j++, i++) {
			send_data[i] = key[j];
		}
		if(issendip){
			for (j = 0; j < user_info.length; j++, i++) {
				send_data[i] = user_info[j];
			}
		}

		for (j = 0; j < i; j++)
			checksum += send_data[j] & 0xff;

		send_data[i++] = (byte) ((checksum & 0xffff) >> 8);
		send_data[i++] = (byte) (checksum & 0xff);
		// Log.d("BSSID", "BSSID = " +
		// Helper.ConvertHexByteArrayToString(bssid));
		// Log.d("send_data", "send_data = " +
		// Helper.ConvertHexByteArrayToString(send_data));
		new Thread(new Runnable() {
			@Override
			public void run() {
				stopSending = false;
				send(sleeptime);
			}
		}).start();
	}

	private void send(int sleeptime) {
		int i, j, k;
		// WifiManager.MulticastLock lock=
		// wifi.createMulticastLock("easylink v3");
		// lock.acquire();
		while (!stopSending) {
			try {
				port = UDP_START_PORT;
				k = 0;
				UDP_SEND(START_FLAG1, sleeptime);
				UDP_SEND(START_FLAG2, sleeptime);
				UDP_SEND(START_FLAG3, sleeptime);
				for (i = 0, j = 1; i < send_data[0]; i++) {
					len = (j * 0x100) + (send_data[i] & 0xff);
					// Log.d("UDP_SEND", "--------" + Integer.toHexString(len)
					// +"   " + i + "--------" + j);
					UDP_SEND(len, sleeptime);
					if ((i % 4) == 3) {
						k++;
						len = 0x500 + k;
						UDP_SEND(len, sleeptime);
					}
					j++;
					if (j == 5)
						j = 1;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// lock.release();
		// udpSocket.close();
	}

	public void SetSmallMTU(boolean onoff) {
		small_mtu = onoff;
	}

	private void UDP_SEND(int length, int sleeptime) {
		try {

			Thread.sleep(sleeptime);

			DatagramSocket udpSocket = new DatagramSocket();
			udpSocket.setBroadcast(true);
			if (small_mtu) {
				if (length > 0x500)
					length -= 0x500;
				if (length < 0x40)
					length += 0xB0;
			}
			send_packet = new DatagramPacket(buffer, length, address, port);
			udpSocket.send(send_packet);
			// Log.d("UDP_SEND", "--------" + Integer.toHexString(length) +"   "
			// + port + "--------");
//			Thread.sleep(sleeptime);
			udpSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop EasyLink
	 */
	public void stopTransmitting() {
		stopSending = true;
	}
}
