package edu.cmu.andrew.ds.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

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
	private boolean migrated;
	
	@Override
	public void write(int b) throws IOException {
	}
}
