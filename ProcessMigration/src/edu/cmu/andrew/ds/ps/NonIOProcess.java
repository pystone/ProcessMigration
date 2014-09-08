package edu.cmu.andrew.ds.ps;


public class NonIOProcess implements MigratableProcess {
	private static final String TAG = NonIOProcess.class.getSimpleName();
	
	public int cnt;
	
	/*
	 * It is safe to assume that the process will limit it’s I/O to files accessed 
     * via the TransactionalFileInputStream and TransactionalFileOutputStream classes
	 */
	
	private volatile boolean suspending;

	/*
	 *  Every class implements MigratableProcess should have a such Constructor.
	 *  
	 *  Doing this cleans up the interface, and is more likely to lead to a 
	 *  general-purpose framework than more complex options.
	 */
	public NonIOProcess(String[] str) {
		this.suspending = false;
	}

	@Override
	public void run() {
		System.out.println(TAG + " : run() begin, cnt = " + cnt);
		
		while(!suspending) {
			try {
				Thread.sleep(100);
				cnt++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		suspending = false;
	}

	@Override
	public void suspend() {
		System.out.println(TAG + " : suspend(), cnt = " + cnt);
		
		suspending = true;
		while (suspending);
	}

	@Override
	public void resume() {
		System.out.println(TAG + " : resume()");
		
		suspending = false;
	}
	
}
