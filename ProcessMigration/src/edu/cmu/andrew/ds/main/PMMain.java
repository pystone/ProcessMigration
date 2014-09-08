package edu.cmu.andrew.ds.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import edu.cmu.andrew.ds.network.ClientManager;
import edu.cmu.andrew.ds.network.ServerManager;
import edu.cmu.andrew.ds.ps.ProcessManager;

/**
 * Starting point of the whole project.
 * 
 * @author KAIILANG CHEN
 * @author YANG PAN
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
		String tmp = in.nextLine();
		
		if (tmp.contains("s")) {
			_server = new ServerManager(PORT);
			ProcessManager.getInstance().startSvr(_server);
		} else if (tmp.contains("c")) {
			_client = new ClientManager(SERVER_IP_ADDR, PORT);
			ProcessManager.getInstance().startSvr(_client);
		} else {
			showHelp();
			return;
		}
		
		in.close();
	}
	
	public static void showHelp() {
		System.out.println("Please input s(Server) or c(Client)!");
	}
}
