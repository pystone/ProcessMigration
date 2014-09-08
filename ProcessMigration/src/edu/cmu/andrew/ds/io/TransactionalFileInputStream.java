package edu.cmu.andrew.ds.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.READ;



/**
 * To facilitate migrating processes with open files, you will need to implement a transactional I/O library.
 * 
 * When a read or write is requested via the library, it should open the file, seek to the requisite location, 
 * perform the operation, and close the file again. In this way, they will maintain all the information required
 * in order to continue performing operations on the file, even if the process is transferred to another node.
 * 
 * Note that you may assume that all of the nodes share a common distributed file system, such as AFS, where all 
 * of the files to be accessed will be located. And, since you are writing the framework – you don’t want to 
 * interrupt these methods with migration. You might find mutexes or monitors (Serialized objects and methods 
 * in Java) helpful for this.
 * 
 * @author KAIILANG CHEN
 * @author YANG PAN
 *
 */

public class TransactionalFileInputStream implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	private static final String TAG = TransactionalFileInputStream.class.getSimpleName();
	
	/*
	 * In order to improve performance, you can also choose to “cache” these connections by reusing them, 
	 * unless a “migrated” flag is set, etc, in which case you would set the flag upon migration and reset
	 * it any time a file handle is created or renewed.
	 */
	
	private File sourceFile;
	private long offset;
	private transient RandomAccessFile handler;
	
	// TODO: 1. more read interfaces
	//		 2. what about the read requests emerging within the period that the isMigrating flag is set?
	private boolean _isReading = false;
	private boolean _isMigrating = false;
	
	public TransactionalFileInputStream(String path) 
			throws IOException {
		this.sourceFile = new File(path);
        this.offset = 0;
	}
	
	/* read in one byte */
	public synchronized int read() throws IOException {
		if (!_isMigrating || handler == null) {
            handler = new RandomAccessFile(sourceFile, "r");
            handler.seek(offset);
        }
        int result = handler.read();
        offset++;
		
		/* set the reading lock to make sure it won't enter migrating mode */
		setReading();

		/* notify other waiting threads before migrating */
		notify();
		
		/* reset the reading lock to make it ready to enter migrating mode */
		resetReading();
		return result;
	}
	
	/* suspend before migrate */
	public synchronized void suspend() 
			throws IOException {
		/* ensure one instance is suspended only once */
		if (_isMigrating == true) {
			println("WARNING: try to suspend a suspended in stream!");
			return;
		}
		
		/* ensure no reading operation is working */
		while (_isReading == true) {
			println("waiting for reading lock");
			try{
				wait();
			} catch(InterruptedException e) { } 
			finally { }
		}
		_isMigrating = true;
		
		// TODO: serialize other stuffs here
		
		println("in stream suspended");
	}
	
	/* resume after migrate */
	public synchronized void resume() 
			throws IOException {
		/* resuming a non-migrating stream is meaningless */
		if (_isMigrating == false) {
			println("WARNING: try to resume a non-migrating in stream!");
			return;
		}
		
		// TODO: deserialize other stuffs here
		
		/* mark the end of the migration and notify other waiting threads */
		_isMigrating = false;
		notify();
		
		println("in stream resumed");
	}
	
	private void println(String msg) {
		System.out.println(TAG + ": " + msg);
	}
	
	public void close() throws IOException {
		handler.close();
	}
	
	private void setReading() {
		_isReading = true;
	}

	private synchronized void resetReading() {
		_isReading = false;
		notify();
	}
	
}
