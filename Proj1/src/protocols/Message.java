package protocols;

import peer.Peer;

import java.nio.charset.StandardCharsets;

import fileManager.Chunk;

public class Message {
	
	private Chunk chunk;
	
	private static String version="1.0";
	
	public Message (Chunk chunk1){
		chunk=chunk1;
	}
	
	protected byte[] createRemoved(){
		String message=Constants.COMMAND_REMOVED+" "+version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= Constants.CRLF + Constants.CRLF;
		byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
		
		return buffer;
	}
	
	protected byte[] createDelete(){
		String message=Constants.COMMAND_DELETE+" "+version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= Constants.CRLF + Constants.CRLF;
		byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
		
		return buffer;
	}
	
	public byte[] createChunk(){
		String message=Constants.COMMAND_CHUNK+" "+version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= chunk.getChunkNumber() + " ";
		message+= Constants.CRLF + Constants.CRLF;
		
		byte[] buffer = concatBytes(message.getBytes(),chunk.getChunkData());
		
		return buffer;	
	}
	
	public byte[] createGetChunk(){
		String message=Constants.COMMAND_GET+" "+version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= chunk.getChunkNumber() + " ";
		message+= Constants.CRLF + Constants.CRLF;
		
		byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
		
		return buffer;	
	}
	
	public byte[] createPutChunk(){
		
		String message = Constants.COMMAND_PUT+ " " + version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= chunk.getChunkNumber() + " ";
		message+= chunk.getReplication() + " ";
		message+= Constants.CRLF + Constants.CRLF;
		
		return message.getBytes(StandardCharsets.UTF_8);		
	}
	
	public byte[] createStored(){
		
		String message=Constants.COMMAND_STORED+" "+version + " ";
		message+=Peer.getPeerId() + " " ;
		message+= chunk.getFileId()+" ";
		message+= chunk.getChunkNumber() + " ";
		message+= Constants.CRLF + Constants.CRLF;
		
		byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
		
		return buffer;		
	}
	
	public static byte[] concatBytes(byte[] a, byte[] b){
		byte[] buffer = new byte[a.length + b.length];
		System.arraycopy(a, 0, buffer, 0, a.length);
		System.arraycopy(b, 0, buffer, a.length, b.length);
		
		return buffer;
	}
	
	public static String getVersion() {
		return version;
	}

}


