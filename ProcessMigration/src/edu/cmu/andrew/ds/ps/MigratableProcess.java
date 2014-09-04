package edu.cmu.andrew.ds.ps;

import java.io.Serializable;

/**
 * For this lab we will focus our attention on processes that are specially built to be migratable, 
 * which we’ll refer to thread as migratable processes. In order to simply the discussion, 
 * we are going to assume that the work is represented by object specially designed to be migratable,
 * where the constraints are captured by an interface or abstract base class
 * 
 *
 * @author KAIILANG CHEN
 * @author YANG PAN
 */

public interface MigratableProcess extends Runnable, Serializable {
	
	/*
	 *  This method will be called before the object is serialized. 
	 *  It affords an opportunity for the process to enter a known safe state.
	 */
	public void suspend();
	
	public void resume();
	
	/*
	 *  This method can, for example, print the class name of the process 
	 *  as well as the original set of arguments with which it was called. 
	 *  Without this, debugging and tracing can be really painful. 
	 */
	public String toString();
}
