package protocols;

import java.io.File;

public class ChunkRestore implements Runnable {
	
	private File file;
	private String fileId;
	
	public ChunkRestore(String fileID){
		fileId=fileID;
	}

	@Override
	public void run() {
		
	}

}
