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
	private String version;
	
	public ChunkBackup(Chunk chunk,String version){		
		this.chunk=chunk;
		waitingTime=1000;
		numTries=0;
		this.version=version;
	}

	@Override
	public void run() {
		
		Message message = new Message(chunk,version);
		
		while(numTries < 5 && CsvHandler.repliMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv") < chunk.getReplication()){
			try {
				
				sendToMDB(Message.concatBytes(message.createPutChunk(),chunk.getChunkData()));
				
				Thread.sleep(waitingTime);
				numTries++;
				waitingTime*=2;
				
			} 
			catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}  
	}
		
	//deixa de existir
	private void sendToMDB(byte[] buffer) throws IOException {
		//DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdbAddress(),Peer.getMdbPort());
		MulticastSocket socket = new MulticastSocket();
		//socket.send(packet);
		socket.close();
	}
}
