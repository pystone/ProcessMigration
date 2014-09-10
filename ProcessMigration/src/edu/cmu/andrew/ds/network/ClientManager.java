package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.net.Socket;

import edu.cmu.andrew.ds.ps.MigratableProcess;
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
			/* gather process info and send to server */
			_procMgr.displayToServer();
			break;
		case 1:
			/* message type sent from client to server */
			break;
		case 2:
			/* request from server to migrate a process */
			if (msg._content instanceof Integer) {
				int pid = ((Integer)msg._content).intValue();
				System.out.println("Request from server to emmigrate process " + pid);
				_procMgr.emmigrateToServer(pid);
			}
			
			break;
		case 3:
			/* message type sent from client to server */
			break;
		case 4:
			/* immigrating process sent from server */
			if (msg._content instanceof MigratableProcess) {
				_procMgr.immigrateFromServer((MigratableProcess)msg._content);
			}
			break;
		case 5:
			/* get client id from server */
			if (msg._content instanceof Integer) {
				_procMgr._prompt = "#" + ((Integer)msg._content).intValue() + " > ";
			}
		default:
			break;
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				receiveMsg(_socket);
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Connection to server is broken. Please restart client.");
				close(_socket);
				System.exit(-1);
			}
		}
	}
}
