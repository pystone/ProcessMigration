package edu.cmu.andrew.ds.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.cmu.andrew.ds.ps.MigratableProcess;

public class ServerManager extends NetworkManager {

	private ServerSocket _svrSocket = null;
	private volatile AtomicInteger _cid = null;
	private volatile Map<Integer, Socket> _clients = null;
	
	public ServerManager(int svrPort) {
		try {
			_clients = new ConcurrentSkipListMap<Integer, Socket>();
			_cid = new AtomicInteger(0);
			
			_svrSocket = new ServerSocket(svrPort);
			
			System.out.println("Waiting for clients...");
			System.out.println("Please connect to " + InetAddress.getLocalHost() + ":" + svrPort + ".");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void msgHandler(MessageStruct msg, Socket src) {
		switch (msg._code) {
		case 0:
			break;
		case 1:
			if (msg._content instanceof ArrayList<?>) {
				ArrayList<ArrayList<String>> proc = (ArrayList<ArrayList<String>>)msg._content;
				displayFromClient(proc, getCid(src));
			}
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			break;
		}
	}
	
	private void displayFromClient(ArrayList<ArrayList<String>> proc, int srcCid) {
		for (ArrayList<String> p : proc) {
			System.out.println("\t" + srcCid + "\t" + p.get(0) + "\t" + p.get(1));
		}
	}
	
	private void addClient(Socket socket) {
		_clients.put(Integer.valueOf(_cid.getAndIncrement()), socket);
	}
	
	private boolean deleteClient(int idx) {
		if (_clients.remove(Integer.valueOf(idx)) == null) {
			println("delete failed!");
			return false;
		}
		return true;
	}
	
	private Socket getSocket(int cid) {
		return (Socket)_clients.get(Integer.valueOf(cid));
	}
	
	private int getCid(Socket socket) {
		for (Map.Entry<Integer, Socket> entry : _clients.entrySet()) {
		    if (entry.getValue() == socket) {
		    	return entry.getKey().intValue();
		    }
		}
		return -1;
	}
	
	@Override
	public void run() {
		/* accepting new clients */
		while (true) {
			try {
				Socket socket = _svrSocket.accept();
				addClient(socket);
				System.out.println("New client(cid is " + getCid(socket) + ") connected!");
				
				new ServerHandler(this, socket).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void println(String msg) {
		System.out.println("ServerManager: " + msg);
	}
	
	/* INTERFACE for cluster manager */
	public void examClients() {
		System.out.println("Processes running on all clients: ");
		System.out.println("\tCID\tPID\tCLASSNAME");
		MessageStruct msg = new MessageStruct(0, null);
		
		for (Map.Entry<Integer, Socket> entry : _clients.entrySet()) {
		    try {
				sendMsg(entry.getValue(), msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
