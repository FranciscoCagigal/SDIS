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
import listeners.Handler;

public class ReadFile implements Runnable {

	private File file;
	private int replication;
	
	public ReadFile(File f, int replication) {
		this.file = f;
		this.replication = replication;
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
				
		
			    byte[] body = new byte[Constants.CHUNKSIZE];
			    int bytesRead = bufferInput.read(body, 0, body.length);
			    body=Handler.trim(body);
			    Chunk chunk = new Chunk(fileID, chunkNo, body, replication);
				
				Runnable run=new ChunkBackup(chunk,file.getName());
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
