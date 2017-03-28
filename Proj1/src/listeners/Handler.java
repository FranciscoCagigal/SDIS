package listeners;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import fileManager.*;

import peer.Peer;
import protocols.Constants;
import protocols.Message;

public class Handler implements Runnable{

	private DatagramPacket packet;
	private Chunk chunk;
	
	public Handler(DatagramPacket packet1){
		packet=packet1;
	}
	
	@Override
	public void run() {
		
		String header[] = getHeader();
		switch(header[0].toUpperCase()){
			case "PUTCHUNK": 
				chunk = new Chunk(header[3],Integer.parseInt(header[4]),null,Integer.parseInt(header[5]));
				putChunkHandler(header);
				break;
			case "STORED" : 
				chunk = new Chunk(header[3],Integer.parseInt(header[4]),null,0);
				storedChunk(header);
				break;
			case "GETCHUNK": 
				getChunk(header);
				break;
			case "DELETE":
				deleteChunks(header);
				break;
			default:
		}
				
	}
	
	private void deleteChunks(String[] header){
		int counter = 1;
		while(true){
			if(HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+counter)){
				HandleFiles.eraseFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+counter);
				counter++;
			}else break;
		}
	}
	
	private void getChunk(String[] header){
		if(HandleFiles.fileExists(header[3]+"."+header[4])){
			Chunk chunk = new Chunk(header[3],Integer.parseInt(header[4]),HandleFiles.readFile(header[3]+"."+header[4]),0);
			
		}
	}
	
	private boolean isMyMessage(String id){
		
		if(Integer.parseInt(id)!=Peer.getPeerId()){
			return false;
		}
		
		return true;		
	}
	
	private void putChunkHandler(String []header){
		if(!isMyMessage(header[2])){
			
			if(!HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"." + header[4])){
				byte[] body=getBody();
				HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4], body);
				Peer.addChunk(chunk);
			}
			
			Message message = new Message(chunk);
			
			Random rnd = new Random();
			try {
				Thread.sleep(rnd.nextInt(401));
				sendToCM(message.createStored());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void storedChunk(String []header){
		if(Peer.isMyChunk(chunk)){
			Peer.addBackup(chunk, header[2]);
		}//else ignore
	}
	
	private void sendToCM(byte[] buffer) throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	private String[] getHeader(){		
		String str = new String(packet.getData(), StandardCharsets.UTF_8);		
		return str.substring(0, str.indexOf(Constants.CRLF)).split(" ");
	}
	
	private byte[] getBody(){
		String str = new String(packet.getData(), StandardCharsets.UTF_8);
		String body = str.substring(str.indexOf(Constants.CRLF)+2, str.length());
		return body.getBytes(StandardCharsets.UTF_8);		
	}
	
}
