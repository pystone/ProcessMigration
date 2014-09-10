/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ServerManager
 * 
 * Server side class to prepare and wait for client connection functions.
 * 
 * @author KAIILANG CHEN(kailianc)
 * @author YANG PAN(yangpan)
 * @version 1.0
 * 
 */
public class ServerHandler extends Thread{
	
	private Socket _socket = null;
	public ServerManager _svrMgr = null;
	
	public ServerHandler(ServerManager svrMgr, Socket socket) {
		_svrMgr = svrMgr;
		_socket = socket;
	}
	
	@Override
	public void run() {
		while (true) {
			ObjectInputStream inStream = null;
			Object inObj = null;
			try {
				inStream = new ObjectInputStream(_socket.getInputStream());
				inObj = inStream.readObject();
			} catch (IOException | ClassNotFoundException e) {
				
			}
			if (inObj instanceof MessageStruct) {
				MessageStruct msg = (MessageStruct) inObj;
				_svrMgr.msgHandler(msg, _socket);
			}
		}
		
	}

	
	private void println(String msg) {
		System.out.println("ServerHandler: " + msg);
	}
}

