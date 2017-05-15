package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

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
				sendToMC(message.findMaster());
				Thread.sleep(1000);
			} catch (IOException e) {
				System.out.println("Falha ao enviar FINDMASTER");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nrTries++;
			//TODO if clause to stop
			
		}
		
		if(Peer.getMasterAddress()==null){
			//ELEIÇOES CARALHO
		}
		
	}
	
	private void sendToMC(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		mc.send(packet);
		mc.close();
	}
	
}
