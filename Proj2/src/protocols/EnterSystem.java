package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import listeners.Handler;
import peer.Peer;

public class EnterSystem {
	
	private MulticastSocket mc;
	
	public EnterSystem(MulticastSocket mc){
		this.mc=mc;
	}
	
	public void findMaster(){
		Message message = new Message(null,Peer.getVersion());
		int nrTries=0;
		while(nrTries<5){
			try {
				Handler.sendToMc(message.findMaster());
				Thread.sleep(1000);
				System.out.println("oi");
			} catch (IOException e) {
				System.out.println("Falha ao enviar FINDMASTER");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(Peer.getMasterAddress()!=null){
				break;
			}
			nrTries++;
			//TODO if clause to stop
			
		}
		
		if(Peer.getMasterAddress()==null){
			Election election = new Election(mc);
			election.startElection();
		}
		
	}
	
}
