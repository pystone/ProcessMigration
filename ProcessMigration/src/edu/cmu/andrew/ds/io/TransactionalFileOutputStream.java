package edu.cmu.andrew.ds.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * 
 * @author KAIILANG CHEN
 * @author YANG PAN
 *
 */

public class TransactionalFileOutputStream  extends OutputStream implements Serializable {
	private static final String TAG = TransactionalFileOutputStream.class.getSimpleName();
	
	/*
	 * In order to improve performance, you can also choose to “cache” these connections by reusing them, 
	 * unless a “migrated” flag is set, etc, in which case you would set the flag upon migration and reset
	 * it any time a file handle is created or renewed.
	 */
	
	private File targetFile;
	private long offset;
	private transient RandomAccessFile handler;
	private boolean migrated;
	
	private boolean _isWriting = false;
	
	public TransactionalFileOutputStream(String path) throws IOException {
		this.targetFile = new File(path);
        this.offset = 0;
        this.migrated = false;
	}
	
	@Override
    public synchronized void write(int b) throws IOException {
		/* set the reading lock to make sure it won't enter migrating mode */
		setWriting();
		
        if (migrated || handler == null) {
            handler = new RandomAccessFile(targetFile, "rw");
            handler.seek(offset);
            migrated = false;
        }
        handler.write(b);
        offset++;
        
        /* notify other waiting threads before migrating */
		notify();
		
		/* reset the reading lock to make it ready to enter migrating mode */
		resetWriting();
    }
	
	/* suspend before migrate */
	public synchronized void suspend() 
			throws IOException {
		/* ensure one instance is suspended only once */
		if (!migrated) {
			println("WARNING: try to suspend a suspended out stream!");
			return;
		}
		
		/* ensure no writing operation is working */
		while (_isWriting == true) {
			println("waiting for writing lock");
			try{
				wait();
			} catch(InterruptedException e) { } 
			finally { }
		}
		migrated = false;
		
		// TODO: serialize other stuffs here
		
		println("out stream suspended");
	}
	
	/* resume after migrate */
	public synchronized void resume() 
			throws IOException {
		/* resuming a non-migrating stream is meaningless */
		if (!migrated) {
			println("WARNING: try to resume a non-migrating out stream!");
			return;
		}
		
		// TODO: deserialize other stuffs here
		
		/* mark the end of the migration and notify other waiting threads */
		migrated = true;
		notify();
		
		println("out stream resumed");
	}
	
	/**
     * close the handle
     */
    @Override
    public void close() throws IOException {
    	if(handler != null) {
    		handler.close();
    	}
    }
    
    public void setWriting() {
		_isWriting = true;
	}
	public synchronized void resetWriting() {
		_isWriting = false;
		notify();
	}
	
	private void println(String msg) {
		System.out.println(TAG + ": " + msg);
	}

    /**
     * Set the migrated flag
     *
     * @param migrated the migrated value
     */
    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }
}