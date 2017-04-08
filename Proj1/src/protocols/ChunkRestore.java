package protocols;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import fileManager.Chunk;
import fileManager.CsvHandler;
import peer.Peer;

public class ChunkRestore implements Runnable {
	
	private String fileName;
	private int chunkNumber;
	private String hash;
	private List<byte[]> chunkData = new ArrayList<byte[]>();
	private String version;
	
	public ChunkRestore(String version,String filename){
		fileName=filename;
		chunkNumber=1;
		hash =CsvHandler.getHash(fileName);
		this.version=version;
	}

	@Override
	public void run() {
		getNumberOfChunks();
		
		int counter=0;
		while (counter<chunkNumber-1){
			Chunk chunk = new Chunk(hash,counter+1,null,0);
			Message message = new Message(chunk,version);
			Peer.wantReceivedChunk(chunk);
			if(version.equals("1.0")){
				try {
					
					while(!Peer.hasChunkBeenReceived(chunk)){
						sendToMC(message.createGetChunk());
						Thread.sleep(1000);
					}
					System.out.println("recebi");
					chunkData.add(Peer.getDataFromReceivedChunk(chunk));
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(version.equals("2.0")){
					try {
						sendToMC(message.createGetChunk());
						DatagramSocket ServerSocket = new DatagramSocket(4004);
						byte[] buffer=new byte[Constants.CHUNKSIZE];
						DatagramPacket p = new DatagramPacket(buffer, buffer.length);
						ServerSocket.receive(p);
						ServerSocket.close();
						System.out.println("recebi : " + p.getData().toString());
						chunkData.add(p.getData());
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
			}
			Peer.removeReceivedChunk(chunk);
			counter++;
		}
		
		//teste
		File file = new File("../Restores" + Peer.getPeerId()+"/"+fileName);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file,false);
			for(byte[] data: chunkData){
				fos.write(new String(data).replaceAll("\0", "").getBytes());
			}
			
			fos.close();
			//end Teste
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	private void sendToMC(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	private void getNumberOfChunks(){
		while(true){
			Chunk chunk = new Chunk(hash,chunkNumber,null,0);
			if(CsvHandler.isMyChunk(chunk,"../metadata"+Peer.getPeerId()+"/MyChunks.csv")){
				chunkNumber++;
			}
			else break;			
		}
	}


}
