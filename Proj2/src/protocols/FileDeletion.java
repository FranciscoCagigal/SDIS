package protocols;

import fileManager.Chunk;
import fileManager.CsvHandler;
import fileManager.HandleFiles;
import listeners.SSL_Client;
import peer.Peer;


public class FileDeletion implements Runnable {
	private String fileName, version;
	
	public FileDeletion(String version,String filename){
		this.fileName=filename;
	}

	@Override
	public void run() {
		
		//apagar o ficheiro local
		if(HandleFiles.fileExists("../Files"+Peer.getPeerId()+"/"+fileName))
			HandleFiles.eraseFile("../Files"+Peer.getPeerId()+"/"+fileName);

		//apagar os chunks da metadata
		//CsvHandler.deleteChunks(hash, "../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		
		//apagar os chunks q estao backed up
		Chunk chunk = new Chunk(fileName,0,null,0);
		Message message = new Message(chunk,version);
		((SSL_Client) Peer.getClientThread()).sendMessage(message.deletePeerSSL());
		
	}
	
}
