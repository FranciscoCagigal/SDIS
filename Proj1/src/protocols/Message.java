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
		
		return message.getBytes();		
	}

}
