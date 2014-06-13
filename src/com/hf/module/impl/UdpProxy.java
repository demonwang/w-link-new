package com.hf.module.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import android.util.Log;

import com.hf.lib.protocol.t1.T1Message;
import com.hf.lib.util.HexBin;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;

public class UdpProxy {
	private static UdpProxy instance = new UdpProxy();
	
	private UdpProxy(){
		super();
	}
	
	public synchronized static UdpProxy getInstance(){
		return instance;
	}
	
	public void broadCast(byte[] cmd){
		ConfigUdpBroadcast mConfigBroadUdp = new ConfigUdpBroadcast();
		mConfigBroadUdp.open();
		mConfigBroadUdp.send(cmd);
		mConfigBroadUdp.close();
	}
	
	
	public byte[] send(byte[] msg, String ip, byte[] key) throws ModuleException{
		byte[] result = new byte[0];
		
        try {
        	DatagramSocket socket = new DatagramSocket();
        	
	        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(ip), ModuleConfig.broadcastPort);
	        socket.send(sendPacket);
			Log.i("DEMOn", HexBin.bytesToString(msg));
			Log.i("DEMOn", new String(key));
	        
	        byte[] buffer = new byte[ModuleConfig.maxTMsgPacketSize];
	        DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
	        //DatagramSocket receiver =  new DatagramSocket();
	        socket.setSoTimeout(ModuleConfig.defaultTimeout);
	        socket.receive(recvPacket);
	        
            byte[] l1bytes = Arrays.copyOfRange(buffer, 0, recvPacket.getLength());
            T1Message t1msg = new T1Message();
            t1msg.setKey(key);
            t1msg.unpack(l1bytes);
            
            result = t1msg.getPayload();
        } catch (SocketException e) {
	        e.printStackTrace();
	        throw new ModuleException(ModuleException.ERROR_CODE_COMMOM_RUNTIME_GENERAL_EXCEPTION, e.getMessage());
        } catch (UnknownHostException e) {
	        e.printStackTrace();
	        throw new ModuleException(ModuleException.ERROR_CODE_COMMOM_RUNTIME_GENERAL_EXCEPTION, e.getMessage());
        } catch (IOException e) {
	        e.printStackTrace();
	        throw new ModuleException(ModuleException.ERROR_CODE_COMMOM_RUNTIME_GENERAL_EXCEPTION, e.getMessage());
        } catch (Exception e) {
	        e.printStackTrace();
	        throw new ModuleException(ModuleException.ERROR_CODE_COMMOM_RUNTIME_GENERAL_EXCEPTION, e.getMessage());
        }
        return result;
	}
	
	
	public class ConfigUdpBroadcast{
		private InetAddress inetAddress;
		private DatagramSocket socket;
		private DatagramPacket packetToSend;
		private int port = ModuleConfig.SmartLinkPort;
		public ConfigUdpBroadcast() {
			try {
				// 255.255.255.255
				inetAddress = InetAddress.getByName("255.255.255.255");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		public void open() {
			try {
				socket = new DatagramSocket(port);
				socket.setBroadcast(true);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		public void close() {
			if (socket != null) {
				socket.close();
			}
		}
		
		public void send(byte[] data){
			packetToSend = new DatagramPacket(data, data.length, inetAddress, port);
			try {
				socket.send(packetToSend);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void recv(byte[] data) throws IOException{
			DatagramPacket packetToRecv = new DatagramPacket(data,data.length);
			socket.receive(packetToRecv);
		}
	}
}
