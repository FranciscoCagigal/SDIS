package protocols;

import fileManager.Chunk;
import fileManager.CsvHandler;
import fileManager.HandleFiles;
import listeners.SSL_Client;
import peer.Peer;

public class ChunkBackup implements Runnable{
	
	private Chunk chunk;
	private String realname;
	
	public ChunkBackup(Chunk chunk,String realname){		
		this.chunk=chunk;
		this.realname=realname;
	}

	@Override
	public void run() {
		
		Message message = new Message(chunk,realname);
		
		String result=((SSL_Client) Peer.getClientThread()).sendMessage(message.backupPeerSSL());
		CsvHandler.addMyChunkMeta(chunk, result, realname);
	}
}
