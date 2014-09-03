package edu.cmu.andrew.ds.main;

import java.io.IOException;

import edu.cmu.andrew.ds.io.*;

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
	public static void main(String[] args) {
		System.out.println("Nothing has been done now!");
		
		// change the file name to absolute path
		TransactionalFileInputStream a = new TransactionalFileInputStream("./input.txt");
		try {
			a.setMigrated();
			System.out.println(String.valueOf(a.read()));
			System.out.println(String.valueOf(a.read()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
