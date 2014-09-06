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

	private static ServerManager _server = null;
	private static ClientManager _client = null;
	
	/*
	 * Everything starts from here!
	 */
	public static void main(String[] args) 
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
//		if (args.length == 0) {
//			showHelp();
//			return;
//		}
//		else if (args[0].equals("server")) {
//			_server = new ServerManager(6777);
//			ProcessManager.getInstance().startSvr(_server);
//		}
//		else if (args[0].equals("client")) {
//			_client = new ClientManager("localhost", 6777);
//			ProcessManager.getInstance().startSvr(_client);
//		}
//		else {
//			showHelp();
//			return;
//		}
		
		// for test only
		Scanner in = new Scanner(System.in);
		String tmp = in.nextLine();
		
		if (tmp.contains("s")) {
			_server = new ServerManager(6777);
			ProcessManager.getInstance().startSvr(_server);
		}
		else if (tmp.contains("c")) {
			_client = new ClientManager("localhost", 6777);
			ProcessManager.getInstance().startSvr(_client);
		}
		
		in.close();
	}
	
	public static void showHelp() {
		System.out.println("No argument!");
	}
	
}
