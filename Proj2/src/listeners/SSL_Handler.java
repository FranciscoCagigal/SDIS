package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.net.ssl.SSLSocket;

import peer.Peer;
import protocols.Constants;

public class SSL_Handler implements Runnable {

	private SSLSocket socket;
	
	
	public SSL_Handler(SSLSocket socket) {
		
		this.socket = socket;
	}


	@Override
	public void run() {
		
		PrintWriter out=null;
		
		BufferedReader in=null;
		
		String received="";
		
		try {
		
			out = new PrintWriter(socket.getOutputStream(), true);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//System.out.println("pronto");
			received = in.readLine();
			
			System.out.println("vou ler input" + received);
			
			out.println("ok");
			
		} catch (IOException e) {
			
			System.out.println("SHITTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
			e.printStackTrace();
		}
				
		String[] divided = received.split(" ");
		
		//TODO verificar autenticacao do peer
		
		String type = divided[0];
		
		String id = divided[1];
		
		String pass = divided[2];
		
		String protocol = divided[3];
		
		String filename ="";
		
		String repDegree = "";
		
		String spaceReclaim = "";
		
		String username = "";
		
		String newPassword = "";
		
		String level = "";
		
		byte[] bytesBody;
				
		switch(protocol){
		
		case Constants.COMMAND_BACKUP:
			
			filename = divided[4];
			repDegree = divided[5];
			
			ReadBytesHandler rbh = null;
			
			try {
				rbh = new ReadBytesHandler(in);
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
						
			bytesBody = rbh.getBody().getBytes();
			
			HashMap<String,SimpleEntry<InetAddress,Integer>> copy = new HashMap<String,SimpleEntry<InetAddress,Integer>>(Peer.getPeers());
			Iterator<Entry<String,SimpleEntry<InetAddress,Integer>>> it = copy.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String,SimpleEntry<InetAddress,Integer>> pair = (Map.Entry<String,SimpleEntry<InetAddress,Integer>>)it.next();
				SimpleEntry<InetAddress,Integer> entry = new SimpleEntry<InetAddress,Integer>((SimpleEntry<InetAddress,Integer>) pair.getValue());
				
				//Runnable client = new SSL_Client(id, entry.getKey(), entry.getValue());
				//new Thread(client).start();
				
				it.remove();
			}
			
			break;
			
		case "RESTORE":
			
			filename = divided[4];
			
			break;
			
		case "RECLAIM":
			
			spaceReclaim = divided[4];
			
			break;
			
		case "DELETE":
			
			filename = divided[4];
			
			break;
			
		case "CREATE":
			
			username = divided[4];
			newPassword = divided[5];
			level = divided[6];
			
			break;
			
		}

		System.out.println("vou fechar");
		out.close();
		try {
			socket.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
