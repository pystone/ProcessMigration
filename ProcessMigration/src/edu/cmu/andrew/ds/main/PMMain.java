package edu.cmu.andrew.ds.main;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

	/*
	 * Everything starts from here!
	 */
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		System.out.println("Nothing has been done now!");
		
		ProcessManager.getInstance().startSvr();		
		
		
		System.out.println("Finish running!");
	}
	

}
