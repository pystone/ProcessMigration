package edu.cmu.andrew.ds.ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.cmu.andrew.ds.network.NetworkManager;


/**
 * You should create a ProcessManager to monitor for requests to launch, remove, and migrate processes.
 * You can have it poll to determine when processes die, receive periodic updates from them, and/or rely
 * upon them to tell you as part of their depth. 
 * 
 * Think about the trade-offs of each method. Can a process always tell you before it does? 
 * What is the cost of polling or heart-beating?
 * 
 * When asking the ProcessManager to create new processes, you probably want it to accept (or return) 
 * a name for the instance. This way, you have a way to identify it later. Remember, it might be banging
 * several instances of the same type at the same time.
 * 
 * Your ProcessManager should be able to handle any MigratableProcess that conforms to your interface, 
 * abstract base class, and/or other requirements -- not just the examples you provided.
 *
 * This means that you will not know what class you are instantiating until runtime. Thus, you will likely 
 * need to use something like Java’s java.lang.Class<T> class and java.lang.reflect.Constructor<T> class 
 * to handle this at runtime. ★	use reflection
 * 
 * @author KAIILANG CHEN
 * @author YANG PAN
 *
 */

public class ProcessManager implements Runnable {
	private static final String TAG = ProcessManager.class.getSimpleName();
	
	private String packageName;
	private NetworkManager _networkManager = null;
	private Thread _receiver = null;
	/*
	 * Instantiate process class until runtime by reflection.
	 */
	private MigratableProcess ps;
	private volatile Map<Integer, MigratableProcess> _pmap = new ConcurrentHashMap<Integer, MigratableProcess>();
		
	private volatile AtomicInteger _pid; 
	
	private volatile boolean _terminate = false;
	
	/*
	 * Singleton
	 */
	private static ProcessManager self;
	synchronized public static ProcessManager getInstance() {
		if(self == null) {
			self = new ProcessManager();
		}
		return self;
	}
	
	public ProcessManager() {
		_pid = new AtomicInteger(0);
		packageName = this.getClass().getPackage().getName();
	}
	
	private void addProcess(MigratableProcess ps) {
		_pmap.put(Integer.valueOf(_pid.getAndIncrement()), ps);
	}
	
	private boolean deleteProcess(int idx) {
		if (_pmap.remove(Integer.valueOf(idx)) == null) {
			println("delete failed!");
			return false;
		}
		return true;
	}
	
	
	public void startSvr(NetworkManager nwMgr) {
		_networkManager = nwMgr;
		System.out.println("Type 'help' for more information");
		
		/* create another thread to receive msg sent from network */
		_receiver = new Thread(this);
		_receiver.start();
		
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        while (_terminate == false) {
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
		case "ct":
			create(Arrays.copyOfRange(arg, 1, arg.length));
			break;
		case "mg":
			migrate(arg[1]);
			break;
//		case "rs":
//			waitForImmigration();
//			break;
		case "ps":
			display();
			break;
		case "st":
			/* still cannot work appropriately */
			_receiver.stop();
			_terminate = true;
			break;
		default:
			help();
			break;	
		}	
	}
	
    private void create(String[] str) {
		try {
			String[] s = Arrays.copyOfRange(str, 1, str.length);
			Class<?> cls = Class.forName(packageName+ "." + str[0]);
			Constructor<?> ctor = cls.getConstructor(String[].class);
			
			ps = (MigratableProcess)ctor.newInstance((Object)s);
			
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		addProcess(ps);
    	Thread thread = new Thread(ps);
        thread.start();
	}
	
	
	private void migrate(String strIdx) {
		int idx = 0;		
		
		try {
			idx = Integer.parseInt(strIdx);
		} catch (NumberFormatException e) {
			println("ERROR: invalid pid(" + strIdx + "), not a number!");
			return;
		}
		
		MigratableProcess ps = (MigratableProcess)_pmap.get(idx);
		
		if (ps == null) {
			println("ERROR: try to migrate a non-existing pid(" + idx + ")!");
			return;
		}
		
		ps.suspend();
		
		try {
			_networkManager.send(ps);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			deleteProcess(idx);
			println("Migrated to network successfully!");
			display();
		}
	}

	
//	private void waitForImmigration() {
//		Object obj = null;
//		
//		try {
//			obj = _networkManager.receive();
//		} catch (ClassNotFoundException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if (obj instanceof MigratableProcess) {
//			ps = (MigratableProcess) obj;
//			
//			addProcess(ps);
//
//			Thread thread = new Thread(ps);
//	        thread.start();
//	        
//	        println("Migrated from network successfully!");
//	        display();
//		}
//	}
	
	private void println(String msg) {
		System.out.println(TAG + ": " + msg);
	}
	
	/*
	 * 
	 */
	private void display() {
		if (_pmap.size() == 0) {
			System.out.println("\tNo process is currently running.");
			return;
		}
		System.out.println("\tpid\tClass Name");
		for (Map.Entry<Integer, MigratableProcess> entry : _pmap.entrySet())
		{
		    System.out.println("\t" + entry.getKey() + "\t" + entry.getValue().getClass().getName());
		}
	}
	
	public void help() {
		
	}

	@Override
	public void run() {
		/* wait for msg */
		while (true) {
			Object obj = null;
			
			try {
				obj = _networkManager.receive();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (obj instanceof MigratableProcess) {
				ps = (MigratableProcess) obj;
				
				addProcess(ps);

				Thread thread = new Thread(ps);
		        thread.start();
		        
		        println("Migrated from network successfully!");
		        display();
			}
		}
	}
	
	
}
