package protocols;

import fileManager.CsvHandler;
import listeners.SSL_Client;
import peer.Peer;

public class ShareDatabase implements Runnable {

	public ShareDatabase(){
		
	}
	
	public void run() {
		Message message = new Message(CsvHandler.getUsers());
		String result=((SSL_Client) Peer.getClientThread()).sendMessage(message.shareNamesSSL());
		System.out.println(result);
		result=((SSL_Client) Peer.getClientThread()).sendMessage(message.shareMyChunksSSL());
		System.out.println(result);
		result=((SSL_Client) Peer.getClientThread()).sendMessage(message.getChunksStoredSSL());
		System.out.println(result);
	}
}
