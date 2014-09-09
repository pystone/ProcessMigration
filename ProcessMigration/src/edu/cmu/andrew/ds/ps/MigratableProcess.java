package edu.cmu.andrew.ds.ps;

import java.io.Serializable;

/**
 * MigratableProcess
 * 
 * An interface for all the migratable processes, in which suspend and resume functions before/after
 * migration are provided.
 * 
 * In order to focus our attention on processes that are specially built to be migratable, 
 * threads here is used as migratable processes. 
 *
 * @author KAIILANG CHEN(kailianc)
 * @author YANG PAN(yangpan)
 * @version 1.0
 * 
 */

public interface MigratableProcess extends Runnable, Serializable {
	/*
	 *  This method will be called before the object is serialized. 
	 *  It affords an opportunity for the process to enter a known safe state.
	 */
	public void suspend();
	
	/*
	 * This method will be called after migration.
	 */
	public void resume();
	
	
	/*
	 *  This method can, for example, print the class name of the process 
	 *  as well as the original set of arguments with which it was called. 
	 *  Without this, debugging and tracing can be really painful. 
	 */
	public String toString();
	
	public void setPid(int pid);
}
