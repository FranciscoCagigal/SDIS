package protocols;

import peer.Peer;
import fileManager.Chunk;

public class Message {
	
	private Chunk chunk;
	
	private String version="1.0";
	
	public Message (Chunk chunk1){
		chunk=chunk1;
	}
	
	protected byte[] createPutChunk(){
		
		String message="PUTCHUNK "+version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= chunk.getChunkNumber() + " ";
		message+= chunk.getReplication() + " ";
		message+= Constants.CRLF + Constants.CRLF;
		
		byte[] buffer = concatBytes(message.getBytes(),chunk.getChunkData());
		
		return buffer;		
	}
	
	private byte[] concatBytes(byte[] a, byte[] b){
		byte[] buffer = new byte[a.length + b.length];
		System.arraycopy(a, 0, buffer, 0, a.length);
		System.arraycopy(b, 0, buffer, a.length, b.length);
		
		return buffer;
	}

}