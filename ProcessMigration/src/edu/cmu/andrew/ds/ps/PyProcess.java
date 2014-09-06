package edu.cmu.andrew.ds.ps;

import java.io.IOException;

import edu.cmu.andrew.ds.io.TransactionalFileInputStream;
import edu.cmu.andrew.ds.io.TransactionalFileOutputStream;

public class PyProcess implements MigratableProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private static final String TAG = PyProcess.class.getSimpleName();
	
	/*
	 * It is safe to assume that the process will limit it’s I/O to files accessed 
     * via the TransactionalFileInputStream and TransactionalFileOutputStream classes
	 */
	TransactionalFileInputStream _inputStream = null;
	TransactionalFileOutputStream _outputStream = null;
	private int _id;
	private Thread t = null;
	
	private volatile boolean suspending;
	
	/*
	 *  Every class implements MigratableProcess should have a such Constructor.
	 *  
	 *  Doing this cleans up the interface, and is more likely to lead to a 
	 *  general-purpose framework than more complex options.
	 */
	public PyProcess(String[] str) throws IOException {
		_inputStream = new TransactionalFileInputStream("./input.txt");
		_outputStream = new TransactionalFileOutputStream("./output.txt");
		_id = 0;
	}
	
	public PyProcess(int id) throws IOException {
		_inputStream = new TransactionalFileInputStream("./input.txt");
		_outputStream = new TransactionalFileOutputStream("./output.txt");
		_id = id;
	}
	
	public PyProcess() throws IOException {
		_inputStream = new TransactionalFileInputStream("./input.txt");
		_outputStream = new TransactionalFileOutputStream("./output.txt");
		_id = 0;
	}
	
	public void readAndPrintOneInt() throws IOException {
		System.out.println(_id + ": " + Integer.toHexString(_inputStream.read()));
	}
	
	public void writeOneInt(int b) throws IOException {
		_outputStream.write(b);
	}
	
	public void start() {
		System.out.println(TAG + "Starting " +  _id );
		if (t == null)
		{
			t = new Thread (this, String.valueOf(_id));
			t.start ();
		}
	}

	@Override
	public void run() {
		System.out.println(TAG + " : run()");
		
		try {
			readAndPrintOneInt();
			_outputStream.write(_inputStream.read());
			_outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* TEST for reading lock
		 * before test, add a sleep function in read()
		if (_id == 0) {
			try {
				readAndPrintOneInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (_id == 1) {
			try {
				_inputStream.suspend();
				_inputStream.resume();
				readAndPrintOneInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
		/* TEST for migration lock
		if (_id == 0) {
			try {
				readAndPrintOneInt();
				readAndPrintOneInt();
				_inputStream.suspend();
				Thread.sleep(2000);
				_inputStream.resume();
				readAndPrintOneInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (_id == 1) {
			try {
				readAndPrintOneInt();
				_inputStream.suspend();
				Thread.sleep(3000);
				_inputStream.resume();
				readAndPrintOneInt();
				readAndPrintOneInt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/

		suspending = false;
	}

	@Override
	public void suspend() {
		System.out.println(TAG + " : suspend()");
		
		suspending = true;
		while (suspending);
	}
	
	@Override
	public void resume() {
		System.out.println(TAG + " : resume()");
		
		suspending = false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
