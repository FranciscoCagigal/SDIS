package listeners;


import java.io.File;
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
			case Constants.COMMAND_PUT: 
				chunk = new Chunk(header[3],Integer.parseInt(header[4]),null,Integer.parseInt(header[5]));
				putChunkHandler(header);
				break;
			case Constants.COMMAND_STORED: 
				chunk = new Chunk(header[3],Integer.parseInt(header[4]),null,0);
				storedChunk(header);
				break;
			case Constants.COMMAND_GET: 
				getChunk(header);
				break;
			case Constants.COMMAND_CHUNK: 
				restoreChunks(header);
				break;
			case Constants.COMMAND_DELETE:
				deleteChunks(header);
				break;
			case Constants.COMMAND_REMOVED:
				chunk = new Chunk(header[3],Integer.parseInt(header[4]),null,0);
				removed(header);
				break;
			default:
		}
				
	}
	
	private void restoreChunks(String[] header){
		Chunk chunkRestored = new Chunk(header[3],Integer.parseInt(header[4]),getBody(),0);
		if(Peer.askedForChunk(chunkRestored)){
			Peer.addReceivedChunk(chunkRestored);
		} else if(Peer.iWantToSendChunk(chunkRestored)){
			Peer.chunkTobeSentWasRcvd(chunkRestored);
		}
		
	}
	
	private void deleteChunks(String[] header){
		int counter = 1;
		CsvHandler.deleteChunks(header[3], "../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		while(true){
			if(HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+counter)){
				HandleFiles.eraseFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+counter);
				counter++;
			}else break;
		}
	}
	
	private void getChunk(String[] header){
		if(!isMyMessage(header[2]) && HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4])){

			Chunk chunkRestored = new Chunk(header[3],Integer.parseInt(header[4]),HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4]),0);
			Message msg = new Message (chunkRestored,Peer.getVersion());
			Peer.askedToSendChunk(chunkRestored);
			try {
				Random rnd = new Random();
				Thread.sleep(rnd.nextInt(401));
				if(!Peer.wasChunkAlreadySent(chunkRestored)){
					System.out.println("Mandei pro MDR!!!!!!");
					sendToMDR(msg.createChunk());
				}
				Peer.removeChunkSent(chunkRestored);				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean isMyMessage(String id){
		
		if(Integer.parseInt(id)!=Peer.getPeerId()){
			return false;
		}
		
		return true;		
	}
	
	private long directorySize(){
		long length = 0;
		File directory = new File("../Chunks"+Peer.getPeerId());
		for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	    }
		return length;
	}
	
	private void putChunkHandler(String []header){
		System.out.println("entrei aqui: " +header[0]);
		byte[] body=getBody();
		if(header[1].equals("1.0")){
			System.out.println("entrei aqui");
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				CsvHandler.updateMyChunks(chunk,null,0);
			}
			else if(Peer.iHaveSpace(directorySize()+(long)body.length)){
				if(!HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"." + header[4])){
					
					HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4], body);		
				}
				CsvHandler.updateChunkRepl(chunk,0,1);
				Message message = new Message(chunk,header[1]);
				
				Random rnd = new Random();
				try {
					Thread.sleep(rnd.nextInt(401));
					sendToCM(message.createStored());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if(header[1].equals("2.0")){
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				CsvHandler.updateMyChunks(chunk,null,0);
			}else if(Peer.iHaveSpace(directorySize()+(long)body.length)){
				Peer.addToBackupEnhancement(header[3]+"."+header[4]);
				Random rnd = new Random();
				try {
					Thread.sleep(rnd.nextInt(401));
					int repl;
					if((repl=Peer.getBackupEnhancement(header[3]+"."+header[4]))<Integer.parseInt(header[5])){
						Peer.removeBackupEnhancement(header[3]+"."+header[4]);
						Message message = new Message(chunk,header[1]);
						sendToCM(message.createStored());
						System.out.println("vou guardar - " + chunk.getChunkNumber() + " - "+ (repl+1));
						CsvHandler.updateChunkRepl(chunk,2,repl+1);
						if(!HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"." + header[4])){							
							HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4], body);		
						}
					}
					if(Peer.containsBackupEnhancement(header[3]+"."+header[4])){
						Peer.removeBackupEnhancement(header[3]+"."+header[4]);
						System.out.println("removi - " + chunk.getChunkNumber());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		
	}
	
	private void removed(String []header){
		if(!isMyMessage(header[2])){
			int repl=0;
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				CsvHandler.updateNegative(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv");
			}
			else if(HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4])){
				repl=CsvHandler.updateNegative(chunk,"../metadata"+Peer.getPeerId()+"/ChunkList.csv");
				chunk.setReplication(repl);
				chunk.setbody(HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4]));
				Message message = new Message(chunk,Peer.getVersion());
				try {
					sendToMDB(message.createPutChunk());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void storedChunk(String []header){
		if(header[1].equals("1.0")){
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				CsvHandler.updateMyChunks(chunk, null, 1);
			}else if(!isMyMessage(header[2])) {
				CsvHandler.updateChunkRepl(chunk,1,1);
			}
		}else if(header[1].equals("2.0")){
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				CsvHandler.updateMyChunks(chunk, null, 1);
			}else if(Peer.containsBackupEnhancement(header[3]+"."+header[4])&&!isMyMessage(header[2])) {
				System.out.println("vou adicionar 1 no - " + chunk.getChunkNumber());
				Peer.changeBackupEnhancement(header[3]+"."+header[4]);
			}else if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/ChunkList.csv")&&!isMyMessage(header[2])){
				System.out.println("Entrei aqui - " + chunk.getChunkNumber());
				CsvHandler.updateChunkRepl(chunk,1,1);
			}
		}
	}
	
	private void sendToCM(byte[] buffer) throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	private void sendToMDR(byte[] buffer) throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdrAddress(),Peer.getMdrPort());
		MulticastSocket socket = new MulticastSocket();		
		socket.send(packet);
		socket.close();
	}
	
	private void sendToMDB(byte[] buffer) throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdrAddress(),Peer.getMdrPort());
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
		String body = str.substring(str.indexOf(Constants.CRLF)+4, str.length());
		return body.getBytes(StandardCharsets.UTF_8);		
	}
	
}
