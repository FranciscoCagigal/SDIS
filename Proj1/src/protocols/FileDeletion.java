package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import fileManager.Chunk;
import fileManager.HandleFiles;
import peer.Peer;

public class FileDeletion implements Runnable {
	private String fileName, hash;
	
	public FileDeletion(String filename){
		fileName=filename;
		hash =Peer.getHashTranslation(fileName);
	}

	@Override
	public void run() {
		
		//apagar o ficheiro local
		HandleFiles.eraseFile("../Files"+Peer.getPeerId()+"/"+fileName);
		System.out.println("../Files/"+Peer.getPeerId()+fileName);
		
		//apagar os chunks q estao backed up
		Chunk chunk = new Chunk(hash,0,null,0);
		Message message = new Message(chunk);
		try {
			sendToMC(message.createDelete());
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
	
}
