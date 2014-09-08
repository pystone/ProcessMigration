package edu.cmu.andrew.ds.ps;

import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.andrew.ds.io.TransactionalFileInputStream;
import edu.cmu.andrew.ds.io.TransactionalFileOutputStream;


public class KcProcess implements MigratableProcess {

	private static final String TAG = KcProcess.class.getSimpleName();
	
	private int readByteNum;
	private int writeByteNum;
	
	private ArrayList<Integer> array = new ArrayList<Integer>();
	private int step = 0;
	
	/*
	 * It is safe to assume that the process will limit it’s I/O to files accessed 
     * via the TransactionalFileInputStream and TransactionalFileOutputStream classes
	 */
	TransactionalFileInputStream _inputStream = null;
	TransactionalFileOutputStream _outputStream = null;
	
	private volatile boolean suspending;

	/*
	 *  Every class implements MigratableProcess should have a such Constructor.
	 *  
	 *  Doing this cleans up the interface, and is more likely to lead to a 
	 *  general-purpose framework than more complex options.
	 */
	public KcProcess(String[] str) {
		this.suspending = false;
		try {
			this._inputStream = new TransactionalFileInputStream(str[0]);
			this._outputStream = new TransactionalFileOutputStream(str[1]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println(TAG + " : run() begin, readByteNum = " + readByteNum + ", writeByteNum = " + writeByteNum + ", step = " + step);
		Integer num = 0;
		while(!suspending) {
			try {
				try {
					if(step == 0) {
						while((num = _inputStream.read()) != -1) {
							System.out.println("read " + num);
							readByteNum++;
							array.add(num);
							Thread.sleep(200);
						}
						step = 1;
						System.out.println("step 0 -> 1");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Thread.sleep(2000);
				
				try {
					if(step == 1) {
						for(Integer i : array) {
							 System.out.println("write " + i);
							_outputStream.write(num);
							writeByteNum++;
							Thread.sleep(200);
						}
						System.out.println("step 1 -> 2");
						step = 2;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		suspending = false;
	}

	@Override
	public void suspend() {
		System.out.println(TAG + " : suspend(), readByteNum = " + readByteNum + ", writeByteNum = " + writeByteNum + ", step = " + step);
		try {
			_inputStream.suspend();
			_outputStream.suspend();
		} catch (IOException e) {
			e.printStackTrace();
		}
		suspending = true;
		while (suspending);
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
		
		suspending = false;
	}
	
}
