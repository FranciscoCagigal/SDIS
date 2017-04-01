package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import fileManager.Chunk;
import fileManager.CsvHandler;
import peer.Peer;

public class ChunkBackup implements Runnable{
	
	private Chunk chunk;
	private int waitingTime;
	private int numTries;
	
	public ChunkBackup(Chunk chunk){		
		this.chunk=chunk;
		waitingTime=1000;
		numTries=0;
	}

	@Override
	public void run() {
		
		Message message = new Message(chunk);
		
		while(numTries < 5 && CsvHandler.repliMyChunk(chunk) < chunk.getReplication()){
			try {
				
				sendToMDB(message.createPutChunk());
				
				Thread.sleep(waitingTime);
				numTries++;
				waitingTime*=2;
				
			} 
			catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}  
	}
			 
	private void sendToMDB(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdbAddress(),Peer.getMdbPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
}
