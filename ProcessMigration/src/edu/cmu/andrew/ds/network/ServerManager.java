/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * @author PY
 *
 */
public class ServerManager extends NetworkManager {
	int _svrPort = 6400;
	ServerSocket _svrSocket = null;
	
	
	public ServerManager(int svrPort) {
		_svrPort = svrPort;
		try {
			_svrSocket = new ServerSocket(_svrPort);
			
			System.out.println("Waiting for clients...");
			System.out.println("Please connect to " + InetAddress.getLocalHost() + ":" + svrPort + ".");
			
			_socket = _svrSocket.accept();
			
			System.out.println("Client connected! " + _socket.getInetAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
