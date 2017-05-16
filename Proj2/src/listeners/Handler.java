package listeners;


import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import fileManager.*;

import peer.Peer;
import protocols.Constants;
import protocols.Message;

public class Handler implements Runnable{

	private DatagramPacket packet;
	private Chunk chunk;
	private static Long masterTime = Long.MAX_VALUE;
	
	public Handler(DatagramPacket packet1){
		packet=packet1;
	}
	
	@Override
	public void run() {
		
		String header[] = getHeader();
		
		System.out.println(Arrays.toString(header));

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
			case Constants.COMMAND_DELETED:
				deleted(header);
				break;
			case Constants.COMMAND_FINDMASTER:
				findMaster();
				break;
			case Constants.COMMAND_IMMASTER:
				foundMaster();
				break;		
			case Constants.COMMAND_BEGINELECTION:
				election(header);
				break;
			case Constants.COMMAND_DISPUTEMASTER:
				electionCandidate(header);
				break;
			default:
		}
				
	}
	
	private void electionCandidate(String[] header){
		if(Long.parseLong(header[2])<Peer.getTime()){
			Peer.setMasterAddress(packet.getAddress());
			Peer.setMasterPort(packet.getPort());
			masterTime=Long.parseLong(header[2]);
			System.out.println("mastertime " + masterTime);
			Peer.setImMaster(false);
		}else if(masterTime==Long.MAX_VALUE && Long.parseLong(header[2])==Peer.getTime()){
			System.out.println("sou peer master " + masterTime + " " + Peer.getTime());
			Peer.setImMaster(true);
		}
	}
	
	private void election(String[] header){
		System.out.println("entrei");
		//masterTime = Long.MAX_VALUE;
		
		Random rnd = new Random();
		try {
			Thread.sleep(rnd.nextInt(1000));
			System.out.println("vou enviar o meu master " + masterTime + " " + Peer.getTime());
			
			if(masterTime>Peer.getTime()){
				Message message = new Message(null,Peer.getVersion());
				sendToMc(message.disputeMaster());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void foundMaster(){
		Peer.setMasterAddress(packet.getAddress());
		Peer.setMasterPort(packet.getPort());
	}
	
	private void findMaster(){
		if(Peer.amIMaster()){
			Message message = new Message(null,Peer.getVersion());
			try {
				sendToMc(message.acknowledgeMaster());
			} catch (IOException e) {
				System.out.println("erro ao enviar IMMASTER");
				e.printStackTrace();
			}
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
	
	private void deleted(String[] header){
		if(Peer.containsDeleteEnhancement(header[3])){
			Peer.changeDeleteEnhancement(header[3]);
		}
	}
	
	private void deleteChunks(String[] header){
		if(!isMyMessage(header[2])){
			int counter = 0;
			CsvHandler.deleteChunks(header[3], "../metadata"+Peer.getPeerId()+"/ChunkList.csv");
			while(true){
				if(HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+counter)){
					HandleFiles.eraseFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+counter);
					if(header[1].equals("2.0")){
						Message message = new Message(new Chunk(header[3],counter,null,0),"2.0");
						try {
							sendToMc(message.createDeleted());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					counter++;
				}else break;
			}
		}	
	}
	
	private void getChunk(String[] header){
		if(!isMyMessage(header[2]) && HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4])){

			Chunk chunkRestored = new Chunk(header[3],Integer.parseInt(header[4]),HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4]),0);
			Message msg = new Message (chunkRestored,header[1]);
			Peer.askedToSendChunk(chunkRestored);
			try {
				Random rnd = new Random();
				Thread.sleep(rnd.nextInt(401));
				if(!Peer.wasChunkAlreadySent(chunkRestored)){
					sendToMDR(msg.createChunk());
					if(header[1].equals("2.0")){
						DatagramSocket tcpSocket = new DatagramSocket();
						byte data[] = HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4]);
						DatagramPacket p = new DatagramPacket(data, data.length, packet.getAddress(), 4004);
						tcpSocket.send(p);
						tcpSocket.close();
					}
				}
				Peer.removeChunkSent(chunkRestored);				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean isMyMessage(String id){
		
		if(!id.equals(Peer.getPeerId()+"")){
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
		
		if(Peer.reclaimHasChunk(chunk)){
			Peer.reclaimTobeSentWasRcvd(chunk);
		}		
		byte[] body=getBody();
		if(header[1].equals("1.0")){
			System.out.println("saquei : " + body.length);
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				System.out.println("encontri o meu chunk");
				CsvHandler.updateMyChunks(chunk,null,0);
			}
			else if(Peer.iHaveSpace(directorySize()+(long)body.length) || HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"." + header[4])){
				if(!HandleFiles.fileExists("../Chunks"+Peer.getPeerId()+"/"+header[3]+"." + header[4])){
					HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4], body);		
				}
				System.out.println("fiz update do " + header[4]);
				CsvHandler.updateChunkRepl(chunk,0,1);
				Message message = new Message(chunk,header[1]);
				
				Random rnd = new Random();
				try {
					Thread.sleep(rnd.nextInt(401));
					sendToMc(message.createStored());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else{
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
						sendToMc(message.createStored());
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
				if(repl!=-1){
					chunk.setReplication(repl);
					chunk.setbody(HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+header[3]+"."+header[4]));
					Peer.reclaimToSendChunk(chunk);
					Random rnd = new Random();
					try {
						Thread.sleep(rnd.nextInt(401));
						if(!Peer.reclaimChunkAlreadySent(chunk)){
							Message message = new Message(chunk,header[1]);
							sendToMDB(message.createPutChunk());
						}
						Peer.removeReclaimSent(chunk);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void storedChunk(String []header){
		if(header[1].equals("1.0")){
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				CsvHandler.updateMyChunks(chunk, null, 1);
			}else if(!isMyMessage(header[2])&&CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/ChunkList.csv")) {
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
	
	public static void sendToMc(byte[] buffer) throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	//deixa de existir
	private void sendToMDR(byte[] buffer) throws IOException{
		//DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdrAddress(),Peer.getMdrPort());
		MulticastSocket socket = new MulticastSocket();		
		socket.send(packet);
		socket.close();
	}
	
	//deixa de existir
	private void sendToMDB(byte[] buffer) throws IOException{
	//	DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMdrAddress(),Peer.getMdrPort());
		MulticastSocket socket = new MulticastSocket();		
		socket.send(packet);
		socket.close();
	}
	
	private String[] getHeader(){
		String str = new String(packet.getData(), StandardCharsets.UTF_8);	
		return str.substring(0, str.indexOf(Constants.CRLF)).split(" ");
	}
	
	public static byte[] trim(byte[] bytes)
	{
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
	}
	
	private byte[] getBody(){
		String packetStr = new String(packet.getData(), StandardCharsets.UTF_8);
		return Arrays.copyOfRange(trim(packet.getData()), 4+packetStr.substring(0, packetStr.indexOf(Constants.CRLF)).length(), trim(packet.getData()).length);	
	}
	
}
