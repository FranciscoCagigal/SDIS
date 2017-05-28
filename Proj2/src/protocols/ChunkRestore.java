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
import listeners.Handler;
import listeners.SSL_Client;
import peer.Peer;

public class ChunkRestore implements Runnable {
	
	private String fileName;
	private List<byte[]> chunkData = new ArrayList<byte[]>();
	
	public ChunkRestore(String filename){
		fileName=filename;
	}

	@Override
	public void run() {
		
		Chunk chunk = new Chunk(fileName,0,null,0);
		Message message = new Message(chunk,"");
		String result=((SSL_Client) Peer.getClientThread()).sendMessage(message.restorePeerSSL());
		
		
		File file = new File("../Restores" + Peer.getPeerId()+"/"+fileName);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file,false);
			fos.write(result.getBytes());
			
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
