package edu.cmu.andrew.ds.ps;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

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
        	switch(step) {
        	case 0:
        		try {
					while((num = _inputStream.read()) != -1) {
						readByteNum++;
						array.add(num);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				step = 1;
				System.out.println("step 0 -> 1");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
        		break;
        	case 1:
        		Collections.sort(array);
        		step = 2;
        		System.out.println("step 1 -> 2");
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}        		
        		break;
        	case 2:
        		for(Integer i : array) {
					try {
						_outputStream.write(i);
						writeByteNum++;
						Thread.sleep(200);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
        		step = 3;
				System.out.println("step 2 -> 3");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
        		break;
        	case 3:
        		if(_inputStream != null) {
        			try {
						_inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        		if(_outputStream != null) {
        			try {
						_outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        		suspending = true;
        		System.out.println("finished");
        	default:
        		break;
        	}
		}
		suspending = false;
	}

	@Override
	public void suspend() {
		System.out.println(TAG + " : suspend(), readByteNum = " + readByteNum + ", writeByteNum = " + writeByteNum + ", step = " + step);

		suspending = true;
		
		try {
			_inputStream.suspend();
			_outputStream.suspend();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	
    public void migrated(){

        Field f[] = this.getClass().getDeclaredFields();
        for (int i = 0; i < f.length; i++) {
        	if(f[i].getType().getSimpleName().equals("TransactionalFileInputStream"))
        	{
        		f[i].setAccessible(true);
        		TransactionalFileInputStream in = null;
        		try {
					in = (TransactionalFileInputStream) f[i].get(this);
				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				} catch (IllegalAccessException e) {

					e.printStackTrace();
				}
        		if(in != null) {
        			in.setMigrated(true);
        		}

        	}
        	if(f[i].getType().getSimpleName().equals("TransactionalFileOutputStream"))
        	{
        		f[i].setAccessible(true);
        		TransactionalFileOutputStream out = null;
        		try {
					out = (TransactionalFileOutputStream) f[i].get(this);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
        		if(out != null)
        		{
	        		out.setMigrated(true);
        		}
        	}
         }
    }
	
}
