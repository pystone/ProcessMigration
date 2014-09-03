package edu.cmu.andrew.ds.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	
	/*
	 * In order to improve performance, you can also choose to “cache” these connections by reusing them, 
	 * unless a “migrated” flag is set, etc, in which case you would set the flag upon migration and reset
	 * it any time a file handle is created or renewed.
	 */
	
	// TODO: 1. add mutex to ensure atomic; 2. more read interfaces
	private boolean _migrated = false;
	private FileChannel _in = null;
	private long _pos = 0;
	private Path _path = null;

	
	public TransactionalFileInputStream(String path) {
		_path = Paths.get(path);
	}
	
	public void setMigrated() {
		_migrated = true;
	}
	
	public void resetMigrated() {
		_migrated = false;
	}
	
	// read in one byte
	public int read() throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(1);
		
		try {
			if (_in == null) {
				System.out.println("open file: "+_path.toString());
				_in = FileChannel.open(_path);
				_in = _in.position(_pos);
			}
			
			_in.read(buf);	// TODO: handle read in error
			
			/* After reading in, should use this method to get buffer ready to write */
			buf.flip();
			
			_pos = _in.position();
			if (_migrated) {
				System.out.println("file closed");
				_in.close();
				_in = null;
			}
			return buf.get();
			
		}finally {
			
		}
	}
	
}
