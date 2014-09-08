package edu.cmu.andrew.ds.ps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import edu.cmu.andrew.ds.io.TransactionalFileInputStream;
import edu.cmu.andrew.ds.io.TransactionalFileOutputStream;

/**
 * IOProcess
 * 
 * Read input file(shuffled alphabet) by one byte a time, put the character into an array and sort,
 * and then output to a file, in order to test the kind of process using the TransactionIO library.
 *
 * @author KAIILANG CHEN(kailianc)
 * @author YANG PAN(yangpan)
 * @version 1.0
 * 
 */
public class IOProcess implements MigratableProcess {	
	private static final String TAG = IOProcess.class.getSimpleName();
	
	private int _readCharNum;
	private int _writeCharNum;
	
	private ArrayList<Integer> _buffer = new ArrayList<Integer>();
	
	private enum PROCESS { READ, SORT, WRITE, FINISH };	
	private PROCESS _proc  = PROCESS.READ;
	
	/*
	 * It is safe to assume that the process will limit it’s I/O to files accessed 
     * via the TransactionalFileInputStream and TransactionalFileOutputStream classes
	 */
	private TransactionalFileInputStream _inputStream = null;
	private TransactionalFileOutputStream _outputStream = null;
	
	private volatile boolean _suspending;

	/*
	 *  Every class implements MigratableProcess should have a such Constructor.
	 *  
	 *  Doing this cleans up the interface, and is more likely to lead to a 
	 *  general-purpose framework than more complex options.
	 */
	public IOProcess(String[] str) {
		this._suspending = false;
		try {
			this._inputStream = new TransactionalFileInputStream(str[0]);
			this._outputStream = new TransactionalFileOutputStream(str[1]);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println(TAG + " : run() begin, readCharNum = " + _readCharNum + 
				", writeCharNum = " + _writeCharNum + ", proc = " + _proc);
		Integer num = 0;
		while(!_suspending) {
        	switch(_proc) {
        	case READ:
        		try {
					while((num = _inputStream.read()) != -1) {
						_readCharNum++;
						_buffer.add(num);
					}
					_proc = PROCESS.SORT;
					System.out.println("READ -> SORT");
					Thread.sleep(500);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
        		break;
        	case SORT:
        		try {
        			Collections.sort(_buffer);
            		_proc = PROCESS.WRITE;
            		System.out.println("SORT -> WRITE");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}        		
        		break;
        	case WRITE:
        		try {
					for(Integer i : _buffer) {
						_outputStream.write(i);
						_writeCharNum++;
						Thread.sleep(200);
					}
					_proc = PROCESS.FINISH;
					System.out.println("WRITE -> FINISH");
					Thread.sleep(500);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				} 
        		break;
        	case FINISH:
        	default:
        		suspend();
        		break;
        	}
		}
		_suspending = false;
	}

	@Override
	public void suspend() {
		System.out.println(TAG + " : suspend(), readCharNum = " + _readCharNum + 
				", writeCharNum = " + _writeCharNum + ", proc = " + _proc);

		_suspending = true;		
		try {
			_inputStream.suspend();
			_outputStream.suspend();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (_suspending);
	}

	@Override
	public void resume() {
		System.out.println(TAG + " : resume()");
		
		try {
			_inputStream.resume();
			_outputStream.resume();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		_suspending = false;
	}
}