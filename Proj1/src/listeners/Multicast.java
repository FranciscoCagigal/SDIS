package listeners;

import java.net.MulticastSocket;

public class Multicast implements Runnable {

	private MulticastSocket socket;
	
	public Multicast(MulticastSocket mc) {
		setSocket(mc);
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
