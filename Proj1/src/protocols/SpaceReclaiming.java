package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import fileManager.Chunk;
import fileManager.CsvHandler;
import peer.Peer;

public class SpaceReclaiming implements Runnable{
	
	public SpaceReclaiming(){}

	@Override
	public void run() {
		
		while(!Peer.iHaveSpace(directorySize())){
			Chunk chunk;
			if((chunk=CsvHandler.eliminateGoodChunk())!=null){
				Message message = new Message(chunk);
				try {
					sendToMC(message.createRemoved());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if((chunk=CsvHandler.eliminateGoodChunk())!=null){
				Message message = new Message(chunk);
				try {
					sendToMC(message.createRemoved());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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

	private void sendToMC(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}

	
}
