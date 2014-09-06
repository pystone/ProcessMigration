/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.andrew.ds.ps.MigratableProcess;

/**
 * @author PY
 *
 */
public abstract class NetworkManager {
	
	Socket _socket = null;
	
	public MigratableProcess receive() throws ClassNotFoundException, IOException {
		ObjectInputStream in = new ObjectInputStream(_socket.getInputStream());
		MigratableProcess inprocess = (MigratableProcess)in.readObject();
		return inprocess;
	}
	
	public void send(MigratableProcess mp) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
		out.writeObject(mp);
	}

}
