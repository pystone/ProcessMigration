package edu.cmu.andrew.ds.ps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
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

public class ProcessManager {
	private static final String TAG = ProcessManager.class.getSimpleName();
	
//	private static final String SERIALIZED_FILENAME = "PM.ser";
	private String packageName;
	private NetworkManager _networkManager = null;
	/*
	 * Instantiate process class until runtime by reflection.
	 */
	private MigratableProcess ps;
	private Map<String, MigratableProcess> map = new ConcurrentHashMap<String, MigratableProcess>();
		
	private AtomicInteger pid; 
	
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
	
	private ProcessManager() {
		pid = new AtomicInteger(0);
		packageName = this.getClass().getPackage().getName();
	}
	
	public int generateID() {
        return pid.getAndIncrement();
    }
	
	public void startSvr(NetworkManager nwMgr) {
		_networkManager = nwMgr;
		System.out.println("Type 'help' for more information");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        while (true) {
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
            }
            execCmd(line.split("\\s+"));
            System.out.print("> ");
        }
	}
	
	private void execCmd(String[] arg) {
		switch(arg[0]) {
		case "st":
			start(arg[1]);
			break;
		case "mg":
			migrate(arg[1]);
			break;
		case "rs":
			resume(arg[1]);
			break;
		case "ps":
			display();
			break;
		default:
			help();
			break;	
		}	
	}
	
    private void start(String psName) {
		try {
			ps = (MigratableProcess) Class.forName(packageName+ "." + psName).getConstructor(String[].class).newInstance((Object)null);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	map.put(psName, ps);
    	Thread thread = new Thread(ps);
        thread.start();
	}
	
	/*
	 * 
	 */
	private void migrate(String psName) {
		MigratableProcess ps = map.get(psName);
		ps.suspend();
		try {
			_networkManager.send(ps);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ObjectOutputStream out = null;
//		try {
//			out = new ObjectOutputStream(new FileOutputStream(SERIALIZED_FILENAME));
//			out.writeObject(ps);
//			out.flush();
//			out.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/*
	 * 
	 */
	private void resume(String psName) {	
		Object obj = null;
		
//		ObjectInputStream in = null;
//		try {
//			in = new ObjectInputStream(new FileInputStream(SERIALIZED_FILENAME));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			obj = in.readObject();
//		} catch (ClassNotFoundException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			obj = _networkManager.receive();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (obj instanceof MigratableProcess) {
			ps = (MigratableProcess) obj;
			
			Thread thread = new Thread(ps);
	        thread.start();
	        
//	        try {
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	/*
	 * 
	 */
	private void display() {}
	
	public void help() {}
	
	
}
