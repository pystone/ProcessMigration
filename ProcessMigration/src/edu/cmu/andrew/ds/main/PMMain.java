package edu.cmu.andrew.ds.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import edu.cmu.andrew.ds.network.ClientManager;
import edu.cmu.andrew.ds.network.ServerManager;
import edu.cmu.andrew.ds.ps.ProcessManager;

/**
 * PMMain
 * 
 * Main class of a tiny framework to simulate a procedure of migrating processes from one machine to another via network.
 * 
 * A bi-directional migration between server and client is supported using JAVA Serialization/Reflection and Socket.
 * Detailed system design, user case and limitations are elaborated in report.
 *
 * @author KAIILANG CHEN(kailianc)
 * @author YANG PAN(yangpan)
 * @version 1.0
 * 
 */
public class PMMain {

	private static final String SERVER_IP_ADDR = "localhost";
	private static final int PORT = 6777;
	
	private static ServerManager _server = null;
	private static ClientManager _client = null;
	
	/*
	 * Everything starts from here!
	 */
	public static void main(String[] args) 
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Scanner in = new Scanner(System.in);
		String line = in.nextLine();
		
		if (line.contains("s")) {
			_server = new ServerManager(PORT);
			ProcessManager.getInstance().startSvr(_server);
		} else if (line.contains("c")) {
			_client = new ClientManager(SERVER_IP_ADDR, PORT);
			ProcessManager.getInstance().startSvr(_client);
		} else {
			showHelp();
			return;
		}
		in.close();
	}
	
	public static void showHelp() {
		System.out.println("Restart and selct role as Server by s or Client by c");
		System.exit(0);
	}
}