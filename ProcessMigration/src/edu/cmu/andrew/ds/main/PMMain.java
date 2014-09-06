package edu.cmu.andrew.ds.main;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.cmu.andrew.ds.network.ClientManager;
import edu.cmu.andrew.ds.network.ServerManager;
import edu.cmu.andrew.ds.ps.KcProcess;
import edu.cmu.andrew.ds.ps.MigratableProcess;
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
		
		System.out.println("Nothing has been done now!");
		
		if (args.length == 0) {
			showHelp();
			return;
		}
		else if (args[0].equals("server")) {
			_server = new ServerManager(6777);
//			MigratableProcess ps = _server.readFromClient();
			ProcessManager.getInstance().startSvr(_server);
		}
		else if (args[0].equals("client")) {
			_client = new ClientManager("localhost", 6777);
			_client.showConnected();
			ProcessManager.getInstance().startSvr(_client);
		}
		else {
			showHelp();
			return;
		}
		
				
		
//		ClientManager client = new ClientManager();
//		client.test();
		
		System.out.println("Finish running!");
	}
	
	public static void showHelp() {
		System.out.println("No argument!");
	}
	
}
