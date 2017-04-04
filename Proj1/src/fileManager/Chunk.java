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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + chunkNo;
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if (chunkNo != other.chunkNo)
			return false;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		return true;
	}

}
