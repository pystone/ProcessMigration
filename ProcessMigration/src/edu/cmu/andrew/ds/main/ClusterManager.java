/**
 * 
 */
package edu.cmu.andrew.ds.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import edu.cmu.andrew.ds.network.ServerManager;



/**
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
		
	}
	
	private void help() {
		
	}
	
	private void println(String msg) {
		System.out.println("ClusterManager: " + msg);
	}
}
