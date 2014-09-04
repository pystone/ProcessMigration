package edu.cmu.andrew.ds.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * 
 * @author KAIILANG CHEN
 * @author YANG PAN
 *
 */

public class TransactionalFileOutputStream  extends OutputStream implements Serializable {

	/*
	 * In order to improve performance, you can also choose to “cache” these connections by reusing them, 
	 * unless a “migrated” flag is set, etc, in which case you would set the flag upon migration and reset
	 * it any time a file handle is created or renewed.
	 */
	private FileChannel _out = null;
	private long _pos = 0;
	private Path _path = null;
	private boolean _isWriting = false;
	private boolean _isMigrating = false;
	
	public TransactionalFileOutputStream(String path) throws IOException {
		_path = Paths.get(path);
		_out = FileChannel.open(_path, WRITE, CREATE, SYNC);
	}
	
	@Override
	public synchronized void write(int b) throws IOException {
		/* if is migrating, wait */
		while (_isMigrating == true) {
			System.out.println("waiting for completion of migration");
			try {
				wait();
			} catch(InterruptedException e) { } 
			finally { }
		}
		
		/* set the reading lock to make sure it won't enter migrating mode */
		setWriting();
		
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.asIntBuffer().put(b);
		
		_out.write(buf);
	
		/* notify other waiting threads before migrating */
		notify();
		
		/* reset the reading lock to make it ready to enter migrating mode */
		resetWriting();
	}
	
	/* suspend before migrate */
	public synchronized void suspend() 
			throws IOException {
		/* ensure one instance is suspended only once */
		if (_isMigrating == true) {
			System.out.println("WARNING: try to suspend a suspended out stream!");
			return;
		}
		
		/* ensure no writing operation is working */
		while (_isWriting == true) {
			System.out.println("waiting for writing lock");
			try{
				wait();
			} catch(InterruptedException e) { } 
			finally { }
		}
		_isMigrating = true;
		
		/* save and close current file description */
		_pos = _out.position();
		_out.close();
		_out = null;
		
		// TODO: serialize other stuffs here
		
		System.out.println("out stream suspended");
	}
	
	/* resume after migrate */
	public synchronized void resume() 
			throws IOException {
		/* resuming a non-migrating stream is meaningless */
		if (_isMigrating == false) {
			System.out.println("WARNING: try to resume a non-migrating out stream!");
			return;
		}
		
		/* reopen the file descriptor and set the position */
		_out = FileChannel.open(_path);
		_out = _out.position(_pos);
		
		// TODO: deserialize other stuffs here
		
		/* mark the end of the migration and notify other waiting threads */
		_isMigrating = false;
		notify();
		
		System.out.println("out stream resumed");
	}
	
	public void close() throws IOException {
		_out.close();
	}
	
	public void setWriting() {
		_isWriting = true;
	}
	public synchronized void resetWriting() {
		_isWriting = false;
		notify();
	}
}
