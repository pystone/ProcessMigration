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
public abstract class NetworkManager implements Runnable{
	
	public void sendMsg(Socket socket, MessageStruct msg) throws IOException {
		ObjectOutputStream out;
		
		out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(msg);
	}
	
	public void receiveMsg(Socket socket) {
		ObjectInputStream inStream = null;
		Object inObj = null;
		try {
			inStream = new ObjectInputStream(socket.getInputStream());
			inObj = inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			
		}
		if (inObj instanceof MessageStruct) {
			MessageStruct msg = (MessageStruct) inObj;
			msgHandler(msg, socket);
		}
		
	}
	
	public void close(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public void close() {
//		try {
//			_socket.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public abstract void msgHandler(MessageStruct msg, Socket src);
}
