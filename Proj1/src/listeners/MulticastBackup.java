package listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import peer.Peer;
import protocols.Constants;

public class MulticastBackup implements Runnable {

	private MulticastSocket socket;
	
	public MulticastBackup(MulticastSocket mdb) {
		setSocket(mdb);
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
		
		//socket.close();
		
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	private void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}

}
