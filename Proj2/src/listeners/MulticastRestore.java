package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Observable;

public class MulticastRestore extends Observable implements Runnable {

	private MulticastSocket socket;
	
	public MulticastRestore(MulticastSocket mdr) {
		setSocket(mdr);
	}

	@Override
	public void run() {
		
		while(true){
			
			byte[] buffer = new byte[65000];
			
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(packet);
				Runnable handler = new Handler(packet);
				new Thread(handler).start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	private void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}

}
