package protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fileManager.Chunk;
import peer.Peer;

public class ChunkBackup implements Runnable{
	
	private File file;
	private int replication;
	
	public ChunkBackup(File f, int repl){		
		file=f;
		replication=repl;
	}

	@Override
	public void run() {
		int chunkNo = 1;
		byte[] buffer = new byte[Constants.CHUNKSIZE];
		
		try {
			BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
			
			@SuppressWarnings("unused")
			int bytesRead;
			
			 while ((bytesRead = bufferInput.read(buffer)) > 0) {
	             
				 MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
				 messageDigest.update((file.getName()+Long.toString(file.lastModified())).getBytes());
				 String fileID = new String(messageDigest.digest());
				 
	             Chunk chunk = new Chunk(fileID,chunkNo,buffer,replication);
	             
	             Message message = new Message(chunk);
	    
	             sendToMDB(message.createPutChunk(),chunk.getChunkData());             
	             
	            }
			 
			 bufferInput.close();
			 
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

	private void sendToMDB(byte[] a, byte[] b) throws IOException {
		byte[] buffer = new byte[a.length + b.length];
		System.arraycopy(a, 0, buffer, 0, a.length);
		System.arraycopy(b, 0, buffer, a.length, b.length);
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdbAddress(),Peer.getMdbPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}

	public int getReplication() {
		return replication;
	}

}
