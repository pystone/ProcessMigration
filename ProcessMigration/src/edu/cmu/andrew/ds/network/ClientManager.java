package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.net.Socket;

import edu.cmu.andrew.ds.ps.ProcessManager;

/**
 * ClientManager
 * 
 * Client side class to create and connect to server functions.
 * 
 * @author KAIILANG CHEN(kailianc)
 * @author YANG PAN(yangpan)
 * @version 1.0
 * 
 */
public class ClientManager extends NetworkManager {
	
	String _svrAddr = null;
	int _svrPort = 0;
	Socket _socket = null;
	
	public ProcessManager _procMgr = null;
	
	public ClientManager(String addr, int port) {
		try {
			_svrAddr = addr;
			_svrPort = port;
			_socket = new Socket(addr, port);
			System.out.println("Connected to server: " + _svrAddr + ":" + _svrPort);
			
//			receiveMsg(_socket);
		} catch (IOException e) {
			System.out.println("Cannot connect to server " + _svrAddr + ":" + _svrPort);
			e.setStackTrace(e.getStackTrace());
			System.exit(0);
		}
	}
	
	public void sendMsg(MessageStruct msg) throws IOException {
		sendMsg(_socket, msg);
	}
	
	public void close() {
		try {
			_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void msgHandler(MessageStruct msg, Socket src) {
		switch (msg._code) {
		case 0:
			_procMgr.displayToServer();
			break;
		case 1:
			break;
		case 2:
			_procMgr.emmigrateToServer("0");
			break;
		case 3:
			break;
		case 4:
			_procMgr.immigrateFromServer(msg._content);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void run() {
		while(true) {
			receiveMsg(_socket);
		}
	}
}
