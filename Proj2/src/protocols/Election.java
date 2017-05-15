package protocols;

import java.io.IOException;
import java.net.MulticastSocket;

import listeners.Handler;
import peer.Peer;

public class Election {
	
	private MulticastSocket mc;
	
	public Election(MulticastSocket mc){
		this.mc=mc;
	}
	
	public void startElection(){
		Message message = new Message(null,Peer.getVersion());
		try {
			Handler.sendToMc(message.beginElection());
		} catch (IOException e) {
			System.out.println("erro ao enviar BEGINELECTION");
			e.printStackTrace();
		}
	}
	
}
