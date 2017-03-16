package listeners;

import java.net.MulticastSocket;

public class MulticastBackup implements Runnable {

	private MulticastSocket socket;
	
	public MulticastBackup(MulticastSocket mdb) {
		setSocket(mdb);
	}

	@Override
	public void run() {
		
		while(true){
			
		}
		
		//socket.close();
		
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	private void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}

}
