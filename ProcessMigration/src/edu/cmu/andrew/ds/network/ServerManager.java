/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cmu.andrew.ds.ps.MigratableProcess;

/**
 * @author PY
 *
 */
public class ServerManager extends NetworkManager {
	int _svrPort = 6400;
	ServerSocket _svrSocket = null;
	Socket _socket = null;
	
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
	
	public void showWaiting() {
		
	}
	
	public MigratableProcess receive() throws ClassNotFoundException, IOException  {
		ObjectInputStream in = new ObjectInputStream(_socket.getInputStream());
		MigratableProcess inprocess = (MigratableProcess)in.readObject();
		return inprocess;
	}
	
	public void send(MigratableProcess mp) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
		out.writeObject(mp);
	}
	
	public void test() {
		String a = String.valueOf('a');
		System.out.println(a);
		System.out.println(a);
	}
	
//	public void mainLoop() {
//		while (true) {
//			System.out.println("Waiting for clients...");
//			System.out.println("Please connect to " + _socket.getInetAddress() + ":" + _socket.getLocalPort() + ".");
//			try {
//				Socket _svr = _socket.accept();
//				System.out.println("Connected to " + _svr.getRemoteSocketAddress());
//				ObjectOutputStream in = new ObjectOutputStream(_svr.getOutputStream());
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
}
