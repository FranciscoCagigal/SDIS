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
			
			while(Peer.getMasterAddress()==null && !Peer.amIMaster()){
				Thread.sleep(1000);
			}
			
		} catch (IOException e) {
			System.out.println("erro ao enviar BEGINELECTION");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
