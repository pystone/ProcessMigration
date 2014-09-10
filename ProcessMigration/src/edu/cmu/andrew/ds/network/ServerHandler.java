/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.net.Socket;

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
			try {
				_svrMgr.receiveMsg(_socket);
			} catch (IOException | ClassNotFoundException e) {
				_svrMgr.clientDisconnected(_socket);
				break;
			}
		}
		
	}
}

