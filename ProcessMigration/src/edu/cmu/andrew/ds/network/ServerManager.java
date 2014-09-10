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
	
	public class MigrateTask {
		int _srcCid;
		int _srcPid;
		int _dstCid;
		MigrateTask(int srcCid, int srcPid, int dstCid) {
			_srcCid = srcCid;
			_srcPid = srcPid;
			_dstCid = dstCid;
		}
		boolean compare(MigrateTask a) {
			if (_srcCid == a._srcCid && _srcPid == a._srcPid 
					&& _dstCid == a._dstCid) {
				return true;
			}
			return false;
		}
	}
	

	private ServerSocket _svrSocket = null;
	private volatile AtomicInteger _cid = null;
	private volatile Map<Integer, Socket> _clients = null;
	ArrayList<MigrateTask> _migrateTasks = null;
	
	public ServerManager(int svrPort) {
		try {
			_clients = new ConcurrentSkipListMap<Integer, Socket>();
			_cid = new AtomicInteger(0);
			_migrateTasks = new ArrayList<MigrateTask>();
			
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
			/* message type sent from server to client */
			break;
		case 1:
			/* process info from clients */
			if (msg._content instanceof ArrayList<?>) {
				ArrayList<ArrayList<String>> proc = (ArrayList<ArrayList<String>>)msg._content;
				displayFromClient(proc, getCid(src));
			}
			break;
		case 2:
			/* message type sent from server to client */
			break;
		case 3:
			/* process from one client to be sent to another client */
			int cid = getCid(src);
			if (msg._content == null) {
				System.out.println("Client " + cid + " has no such process! Please check the pid again.");
				break;
			}
			if (msg._content instanceof MigratableProcess) {
				migrateToClient((MigratableProcess)msg._content, getCid(src));
			}
			break;
		case 4:
			/* message type sent from server to client */
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
	
	private void migrateToClient(MigratableProcess mp, int srcCid) {
		for (MigrateTask i: _migrateTasks) {
			if (i._srcCid==srcCid && i._srcPid==mp.getPid()) {
				try {
					sendMsg(getClient(i._dstCid), new MessageStruct(4, mp));
				} catch (IOException e) {
					println("Connection to " + i._dstCid + " is broken! Cannot migrate process to it. Process lost.");
				}
				System.out.println("Migrate process successfully to client " + i._dstCid + ".");
				break;
			}
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
	
	private Socket getClient(int cid) {
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
	
	public void sendMigrateRequest(int srcCid, int srcPid, int dstCid) {
		Socket socket = getClient(srcCid);
		if (socket == null) {
			System.out.println("Cannot migrate. Client " + srcCid + " is not available!");
			return;
		}
		
		_migrateTasks.add(new MigrateTask(srcCid, srcPid, dstCid));
		try {
			sendMsg(socket, new MessageStruct(2, Integer.valueOf(srcPid)));
		} catch (IOException e) {
			println("ERROR: Connection with " + srcCid + " is broken, message cannot be sent!");
			return;
		}
	}
}
