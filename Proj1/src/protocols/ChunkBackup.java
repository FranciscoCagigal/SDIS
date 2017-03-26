package protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fileManager.Chunk;
import peer.Peer;

public class ChunkBackup implements Runnable{
	
	private File file;
	private int replication;
	private int waitingTime;
	private int numTries;
	
	public ChunkBackup(File f, int repl){		
		file=f;
		replication=repl;
		waitingTime=1000;
		numTries=0;
	}

	@Override
	public void run() {
		int chunkNo = 1;
		byte[] buffer = new byte[Constants.CHUNKSIZE];
		
		try {
						
			BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update((file.getName()+Long.toString(file.lastModified())).getBytes());
			String fileID = new String(messageDigest.digest());
			Peer.addToTranslations(file.getName(), fileID);
			
			@SuppressWarnings("unused")
			int bytesRead;
			
			 while ((bytesRead = bufferInput.read(buffer)) > 0) {
	             
				 
				 	
	             Chunk chunk = new Chunk(String.format("%040x", new BigInteger(1, fileID.getBytes())),chunkNo,buffer,replication);
	             Peer.addBackup(chunk,null);
	             Message message = new Message(chunk);
				 while(numTries<5 && Peer.getNumberOfPeers(chunk)<chunk.getReplication()){
					 sendToMDB(message.createPutChunk());
					 try {
						Thread.sleep(waitingTime);
						numTries++;
						waitingTime*=2;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
	             
	             
	            }
			 
			 bufferInput.close();
			 
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

	private void sendToMDB(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdbAddress(),Peer.getMdbPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	

	public int getReplication() {
		return replication;
	}

}
