package edu.cmu.andrew.ds.ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
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

	private MigratableProcess ps;
	private volatile Map<Integer, MigratableProcess> _pmap = new ConcurrentSkipListMap<Integer, MigratableProcess>();
		
	private volatile AtomicInteger _pid; 
	
	
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
	
	private int getCurrentPid() {
		return _pid.get() - 1;
	}
	
	private MigratableProcess getProcess(int pid) {
		return (MigratableProcess)_pmap.get(Integer.valueOf(pid));
	}
	
	public void startSvr(NetworkManager nwMgr) {
		_networkManager = nwMgr;
		System.out.println("Type 'help' for more information");
		
		/* create another thread to receive msg sent from network */
		_receiver = new Thread(this);
		_receiver.start();
		
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
		case "create":
		case "ct":
			if (arg.length == 1) {
				System.out.println("Invalid command.");
				break;
			}
			create(Arrays.copyOfRange(arg, 1, arg.length));
			break;
		case "migrate":
		case "mg":
			if (arg.length == 1) {
				System.out.println("Invalid command.");
				break;
			}
			migrate(arg[1]);
			break;
		case "call":
			if (arg.length < 3) {
				System.out.println("Invalid command.");
				break;
			}
			callMethod(arg);
		case "ps":
			display();
			break;
		case "exit":
		case "st":
			exit();
			break;
		default:
//			help();
			break;	
		}	
	}
	
    private void create(String[] str) {
    	String psName = str[0];
		try {
			String[] s = Arrays.copyOfRange(str, 1, str.length);
			Class<?> cls = Class.forName(packageName+ "." + str[0]);
			Constructor<?> ctor = cls.getConstructor(String[].class);
			
			ps = (MigratableProcess)ctor.newInstance((Object)s);
			
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			println("Class " + psName + " not found.");
			return;
		} catch (NoSuchMethodException e) {
			println(psName + " should have a constructor with prototype " + psName + "(String[]);");
			return;
		}
		
		addProcess(ps);
		System.out.println(psName + " class has been created, pid: " + getCurrentPid());
    	Thread thread = new Thread(ps);
        thread.start();
        display();
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
			System.out.println("Network problem. Cannot migrate now.");
			ps.resume();
			return;
		} 
		
		deleteProcess(idx);
		println("Migrated to network successfully!");
		display();
	}
	
	private void callMethod(String[] argv) {
		int pid = 0;
		try {
			pid = Integer.parseInt(argv[1]);
		} catch (NumberFormatException e) {
			println("Pid is not a number!");
			return;
		}
		MigratableProcess ps = getProcess(pid);
		if (ps == null) {
			println("Invalid pid!");
			return;
		}
		
		Method method = null;
		try {
			method = ps.getClass().getMethod(argv[2], new Class[]{String[].class});
		} catch (NoSuchMethodException e) {
			println("No such method named " + argv[2] + " found");
			return;
		}catch (SecurityException e) {
			e.printStackTrace();
		}
		try {
			method.invoke(ps, (Object)Arrays.copyOfRange(argv, 3, argv.length));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			println("Illegal argument!");
			return;
		}
	}
	
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
		System.out.println("Here are the commands:");
		System.out.println("\tcommand\t\t\tdescription");
		System.out.println("\tcreate CLASSNAME\tcreate a new instance of CLASSNAME");
		System.out.println("\tmigrate PID\t\tmigrate a process with PID to another computer");
		System.out.println("\tps\t\t\tdisplay all the running processes");
		System.out.println("\texit\t\t\texit the program");
	}
	
	private void exit() {
		_networkManager.close();
		System.exit(0);
	}

	@Override
	public void run() {
		/* wait for msg */
		while (true) {
			Object obj = null;
			
			try {
				obj = _networkManager.receive();
			} catch (ClassNotFoundException e) {
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
