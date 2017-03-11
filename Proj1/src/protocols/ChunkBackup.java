package protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fileManager.Chunk;

public class ChunkBackup implements Runnable{
	
	private File file;
	//acho q o replication degree é passado aqui
	
	public ChunkBackup(File f){
		file=f;
	}

	@Override
	public void run() {
		int chunkNo = 1;
		byte[] buffer = new byte[Constants.CHUNKSIZE];
		
		try {
			BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
			int bytesRead;
			 while ((bytesRead = bufferInput.read(buffer)) > 0) {
	             
				 MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
				 messageDigest.update((file.getName()+Long.toString(file.lastModified())).getBytes());
				 String fileID = new String(messageDigest.digest());
				 
				 
	             Chunk chunk = new Chunk(fileID,chunkNo,buffer);
	             
	             //guardar o chunk em alguma lado e mandar para os outros peers
	                
	            }
			 bufferInput.close();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

}
