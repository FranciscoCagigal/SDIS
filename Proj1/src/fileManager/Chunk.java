package fileManager;

public class Chunk {
	private String fileId;
	private int chunkNo;
	private byte[] data;
	private int replication;
	
	public Chunk(String id, int number, byte[] d, int repl){
		fileId=id;
		chunkNo=number;
		data=d;
		replication=repl;
	}
	
	public String getFileId(){
		return fileId;
	}
	
	public int getChunkNumber(){
		return chunkNo;
	}
	
	public byte[] getChunkData(){
		return data;
	}

	public int getReplication() {
		return replication;
	}

}
