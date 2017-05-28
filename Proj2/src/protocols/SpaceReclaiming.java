package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import fileManager.Chunk;
import fileManager.CsvHandler;
import listeners.SSL_Client;
import peer.Peer;

public class SpaceReclaiming implements Runnable{
	
	public SpaceReclaiming(){}

	@Override
	public void run() {
		
		while(!Peer.iHaveSpace(directorySize())){
			Chunk chunk;
			if((chunk=CsvHandler.eliminateBadChunk())!=null){
				Message message = new Message(chunk,Peer.getVersion());
				((SSL_Client) Peer.getClientThread()).sendStart(message.createRemoved());
			}
		}
	}
	
	public static long directorySize(){
		long length = 0;
		File directory = new File("../Chunks"+Peer.getPeerId());
		for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	    }
		return length;
	}	
}
