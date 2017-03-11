package fileManager;

public class Chunk {
	private String fileId;
	private int chunkNo;
	private byte[] data;
	
	public Chunk(String id, int number, byte[] d){
		fileId=id;
		chunkNo=number;
		data=d;
	}
	
	public String getId(){
		return fileId;
	}
	
	public int getChunkNumber(){
		return chunkNo;
	}
	
	public byte[] getChunkData(){
		return data;
	}
}
