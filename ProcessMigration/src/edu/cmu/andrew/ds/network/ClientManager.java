/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.cmu.andrew.ds.ps.MigratableProcess;

/**
 * @author PY
 *
 */
public class ClientManager extends NetworkManager {
	
	Socket _socket = null;
	String _svrAddr = null;
	int _svrPort = 0;
	
	public ClientManager(String addr, int port) {
		try {
			_svrAddr = addr;
			_svrPort = port;
			_socket = new Socket(addr, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(MigratableProcess mp) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
		out.writeObject(mp);
	}
	
	public MigratableProcess receive() throws ClassNotFoundException, IOException {
		ObjectInputStream in = new ObjectInputStream(_socket.getInputStream());
		MigratableProcess inprocess = (MigratableProcess)in.readObject();
		return inprocess;
	}
	
	public void showConnected() {
		System.out.println("Connected to server: " + _svrAddr + ":" + _svrPort);
	}
}
