package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Observable;
import java.util.Observer;

import fileManager.Chunk;
import peer.Peer;

public class ChunkRestore implements Runnable,Observer {
	
	private String fileName;
	private int chunkNumber;
	private String hash;
	private boolean stored=false;
	private String chunkString;
	
	public ChunkRestore(String filename){
		fileName=filename;
		chunkNumber=1;
		hash =Peer.getHashTranslation(fileName);
	}

	@Override
	public void run() {
		getNumberOfChunks();
		
		int counter=0;
		while (counter<chunkNumber){
			Chunk chunk = new Chunk(hash,counter+1,null,0);
			Message message = new Message(chunk);
			try {
				sendToMC(message.createGetChunk());
				((Observable) Peer.getMDRlistener()).addObserver(this);
				chunkString=hash+" "+counter+1;
				while(!stored){
					Thread.sleep(400);
					sendToMC(message.createGetChunk());
				}
				counter++;
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	private void sendToMC(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	private void getNumberOfChunks(){
		while(true){
			Chunk chunk = new Chunk(hash,chunkNumber,null,0);
			if(Peer.isMyChunk(chunk)){
				chunkNumber++;
			}
			else break;			
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(!arg0.equals(Peer.getMDRlistener()))
			return;
		String compareChunk = (String) arg1;
		if(compareChunk.equals(chunkString)){
			stored=true;
		}		
	}


}
