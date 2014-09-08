package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.net.Socket;

/**
 * @author PY
 *
 */
public class ClientManager extends NetworkManager {
	
	String _svrAddr = null;
	int _svrPort = 0;
	
	public ClientManager(String addr, int port) {
		try {
			_svrAddr = addr;
			_svrPort = port;
			_socket = new Socket(addr, port);
			System.out.println("Connected to server: " + _svrAddr + ":" + _svrPort);
		} catch (IOException e) {
			System.out.println("Cannot connect to server " + _svrAddr + ":" + _svrPort);
			System.exit(0);
		}
	}
}
