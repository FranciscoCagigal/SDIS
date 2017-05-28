package protocols;

import fileManager.CsvHandler;
import listeners.SSL_Client;
import peer.Peer;

public class ShareDatabase implements Runnable {

	public ShareDatabase(){}
	
	public void run() {
		Message message = new Message(CsvHandler.getUsers());
		((SSL_Client) Peer.getClientThread()).sendStart(message.shareNamesSSL());
		((SSL_Client) Peer.getClientThread()).sendStart(message.shareMyChunksSSL());
		((SSL_Client) Peer.getClientThread()).sendStart(message.getChunksStoredSSL());
	}
}
