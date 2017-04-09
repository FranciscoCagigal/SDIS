package protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fileManager.Chunk;
import fileManager.CsvHandler;

public class ReadFile implements Runnable {

	private File file;
	private int replication;
	private String version;
	
	public ReadFile(String versionProtocol,File f, int replication) {
		this.file = f;
		this.replication = replication;
		version=versionProtocol;
	}
	
	public void run() {
		
		try {
			
			BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update((file.getName()+Long.toString(file.lastModified())).getBytes());
			String fileIDbin = new String(messageDigest.digest());
			String fileID = String.format("%040x", new BigInteger(1, fileIDbin.getBytes()));
			
			int chunkNo = 0;
			int fileSize = (int) file.length();
			  
			int totalBytesRead = 0;
			
			while ( totalBytesRead < fileSize ) {
				
				Chunk chunk = new Chunk(fileID, chunkNo, null, replication);
				
				Message message = new Message(chunk,version);
				byte[] msgbytes = message.createPutChunk();
			    byte[] body = new byte[Constants.CHUNKSIZE-msgbytes.length];
			    int bytesRead = bufferInput.read(body, 0, body.length);
			    chunk.setbody(body);
			
				CsvHandler.updateMyChunks(chunk,file.getName(),0);
				
				Runnable run=new ChunkBackup(chunk,version);
				new Thread(run).start();
			    
			    if (bytesRead > 0) {
			    	totalBytesRead += bytesRead;
			    	chunkNo++;
			    }
			}
			
			bufferInput.close();
			
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
