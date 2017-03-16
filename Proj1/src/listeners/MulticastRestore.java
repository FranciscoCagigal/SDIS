package listeners;

import java.net.MulticastSocket;

public class MulticastRestore implements Runnable {

	private MulticastSocket socket;
	
	public MulticastRestore(MulticastSocket mdr) {
		setSocket(mdr);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	private void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}

}
