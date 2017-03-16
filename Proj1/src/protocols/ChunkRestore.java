package protocols;

import java.io.File;

public class ChunkRestore implements Runnable {
	
	private File file;
	private String fileId;
	
	public ChunkRestore(String fileID){
		setFileId(fileID);
	}

	@Override
	public void run() {
		
	}

	public String getFileId() {
		return fileId;
	}

	private void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
