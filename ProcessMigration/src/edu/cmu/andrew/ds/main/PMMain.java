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
		
		
//		PyProcess a = new PyProcess(0);
//		a.start();
//		PyProcess b = new PyProcess(1);
//		b.start();
		
//		TransactionalFileInputStream a = new TransactionalFileInputStream("./input.txt");
//		try {
//			System.out.println(String.valueOf(a.read()));
//			System.out.println(String.valueOf(a.read()));
//			a.suspend();
//			System.out.println(String.valueOf(a.read()));
//			Thread.sleep(1000);
//			System.out.println(String.valueOf(a.read()));
//			a.resume();
//			System.out.println(String.valueOf(a.read()));
//			System.out.println(String.valueOf(a.read()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("Finish running!");
	}
	

}
