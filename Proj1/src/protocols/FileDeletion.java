package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import fileManager.Chunk;
import fileManager.CsvHandler;
import fileManager.HandleFiles;
import peer.Peer;


public class FileDeletion implements Runnable {
	private String fileName, hash, version;
	private int nrTries=0;
	private long waiting=1000;
	
	public FileDeletion(String version,String filename){
		fileName=filename;
		hash =CsvHandler.getHash(fileName);
		this.version=version;
	}

	@Override
	public void run() {
		
		//apagar o ficheiro local
		if(HandleFiles.fileExists("../Files"+Peer.getPeerId()+"/"+fileName))
			HandleFiles.eraseFile("../Files"+Peer.getPeerId()+"/"+fileName);
		
		
		int sumOfRepl=0;
		if(!version.equals("1.0")){
			
			for(int i=0;i<=CsvHandler.numberOfChunks(hash);i++){
				Chunk chunk=new Chunk(hash,i,null,0);
				sumOfRepl+=CsvHandler.repliMyChunk(chunk);
			}
			Peer.addToDeleteEnhancement(hash);
		}
			
		
		//apagar os chunks da metadata
		CsvHandler.deleteChunks(hash, "../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		
		//apagar os chunks q estao backed up
		Chunk chunk = new Chunk(hash,0,null,0);
		Message message = new Message(chunk,version);
		try {
			if(version.equals("1.0")){
				sendToMC(message.createDelete());
			}else {
				while(Peer.getDeleteEnhancement(hash)<sumOfRepl &&nrTries<5){
					sendToMC(message.createDelete());
					Thread.sleep(waiting*(nrTries+1));
					nrTries++;
					System.out.println(Peer.getDeleteEnhancement(hash)+" - "+ sumOfRepl);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(version.equals("1.0")){
			Peer.removeDeleteEnhancement(hash);
		}
		
	}
	
	private void sendToMC(byte[] buffer) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
}
