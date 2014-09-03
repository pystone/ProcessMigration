package edu.cmu.andrew.ds.ps;

import java.util.Set;

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
	
	/*
	 * Instantiate process class until runtime by reflection.
	 */
	Set<Class<? extends MigratableProcess>> processClasses;
	
}
