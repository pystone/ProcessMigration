/**
 * 
 */
package edu.cmu.andrew.ds.network;

import java.io.IOException;

import edu.cmu.andrew.ds.ps.MigratableProcess;

/**
 * @author PY
 *
 */
public abstract class NetworkManager {
	
	public abstract MigratableProcess receive() throws ClassNotFoundException, IOException;
	
	public abstract void send(MigratableProcess mp) throws IOException;

}
