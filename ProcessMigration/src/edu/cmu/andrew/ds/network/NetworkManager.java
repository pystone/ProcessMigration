package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.andrew.ds.ps.MigratableProcess;

/**
 * NetworkManager
 * 
 * Base class of Server and Client to provide Serializtion/Deserialization functions in bi-directional migration.
 *
 * @author KAIILANG CHEN(kailianc)
 * @author YANG PAN(yangpan)
 * @version 1.0
 * 
 */
public abstract class NetworkManager {
	
	Socket _socket = null;
	boolean _terminated = false;
	
	public Object receive() throws ClassNotFoundException {
		ObjectInputStream in = null;
		Object inprocess = null;
		try {
			in = new ObjectInputStream(_socket.getInputStream());
			inprocess = in.readObject();
		} catch (IOException e) {
			return null;
		}
		
		return inprocess;
	}
	
	public void send(MigratableProcess mp) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
		out.writeObject(mp);
	}
	
	public void close() {
		try {
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
