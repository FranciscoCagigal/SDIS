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
				String received= new String(buffer,0,buffer.length).trim();
				String header =received.substring(0, received.indexOf(Constants.CRLF));
				String body = received.substring(received.indexOf(Constants.CRLF)+2, received.length());   
				
				String[] dividedHeader= header.split(" ");
				
				if(!isMyMessage(dividedHeader[2])){
					
					System.out.println(dividedHeader[3]);
					
					File file= new File("../Files/"+ dividedHeader[3] +"."+dividedHeader[4]);
					file.createNewFile();
					BufferedWriter out = new BufferedWriter(new FileWriter(file));
					out.write(body);
					out.close();
				}
				
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

	private boolean isMyMessage(String id){
		
		
		
		if(Integer.parseInt(id)!=Peer.getPeerId()){
			return false;
		}
		
		return true;		
	}

}
