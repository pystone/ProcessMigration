/**
 * 
 */
package edu.cmu.andrew.ds.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.cmu.andrew.ds.network.ServerManager;



/**
 * ClusterManager
 * 
 * When working as server, ClusterManager is the main control class of the program.
 * It will start a new thread of Server Manager to do all the network stuffs like 
 * waiting for, read from and write to clients. Also, it support user input to 
 * check status of all clients and migrate process. 
 * 
 * Support input command:
 * 		migrate SRC_CID SRC_PID DST_PID: migrate the process SRC_PID in client 
 * 			SRC_CID to client DST_PID. All three arguments should be specified.
 * 		ps: show all the processes running on all clients.
 * 		exit: close the server program. All clients connected to this server shall
 * 			be CLOSED once server exits.
 * 
 * @author PY
 *
 */
public class ClusterManager {
	
	ServerManager _svrMgr = null;
	
	public ClusterManager(int port) {
		_svrMgr = new ServerManager(port);
		new Thread(_svrMgr).start();
	}

	public void startServer() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        while (true) {
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
            	println("ERROR: read line failed!");
            	return;
            }
            execCmd(line.split("\\s+"));
            System.out.print("> ");
        }
	}
	
	private void execCmd(String[] arg) {
		switch(arg[0]) {
		case "migrate":
		case "mg":
			if (arg.length != 4) {
				System.out.println("Invalid command.");
				break;
			}
			migrate(arg);
			break;
		case "ps":
			display();
			break;
		case "exit":
		case "st":
			exit();
			break;
		case "help":
		case "hp":
			help();
			break;
		default:
			break;	
		}
	}
	
	private void migrate(String[] arg) {
		int srcCid, srcPid, dstCid;
		try {
			srcCid = Integer.parseInt(arg[1]);
			srcPid = Integer.parseInt(arg[2]);
			dstCid = Integer.parseInt(arg[3]);
		} catch (NumberFormatException e) {
			println("Invalid argument!");
			return;
		}
		
		_svrMgr.sendMigrateRequest(srcCid, srcPid, dstCid);
	}
	
	private void display() {
		_svrMgr.examClients();
	}
	
	private void exit() {
		_svrMgr.close();
		System.exit(0);
	}
	
	private void help() {
		
	}
	
	private void println(String msg) {
		System.out.println("ClusterManager: " + msg);
	}
}
