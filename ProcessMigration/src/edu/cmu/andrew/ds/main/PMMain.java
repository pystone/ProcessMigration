package edu.cmu.andrew.ds.main;

import java.io.IOException;

import edu.cmu.andrew.ds.io.*;
import edu.cmu.andrew.ds.ps.PyProcess;

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
	public static void main(String[] args) throws IOException {
		System.out.println("Nothing has been done now!");
		
		PyProcess a = new PyProcess(0);
		a.run();
		PyProcess b = new PyProcess(1);
		b.run();
		
		System.out.println("Finish running!");
	}
	

}
