package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLSocket;

import fileManager.Chunk;
import fileManager.HandleFiles;
import peer.Peer;
import protocols.Constants;
import protocols.Message;

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
			
			while(true){
				received = in.readLine();
				
				System.out.println("vou ler input" + received);
				
				String[] divided = received.split(" ");
				
				System.out.println("size " + divided.length);
				
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
				
				String chunkSize = "";
				
				String chunkNumber = "";
				
				String originalPeer = "";
				
				byte[] bytesBody;
				
				switch(protocol){
				
				case Constants.COMMAND_BACKUP:
					
					chunkNumber = divided[5];
					filename = divided[4];
					chunkSize = divided[7];
					
					in.readLine();
					
					char[] buffer = new char[64000];
					char[] result = new char[64000];
					
					int counter=0;
					
					if(type.equals("1")){
						
						repDegree = divided[6];	
						
						while((counter+=in.read(buffer))!=-1){
							result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));
							if(counter-2==Integer.parseInt(chunkSize))
								break;
							
							buffer = new char[64000];
						}			
						result=Handler.trim(result);
						result=Arrays.copyOfRange(result, 0, result.length-2);
						
						HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
						Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
						while(it.hasNext()) {
							Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
							String idThread = pair.getKey();
							Runnable thread = pair.getValue();
							Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),new String(result).getBytes(),Integer.parseInt(repDegree));
							if(!idThread.equals(Peer.getPeerId()+"")){
								Message message = new Message(chunk,id);
								if(message.backupMasterSSL()==null)
									System.out.println("mensagem nula");
								((SSL_Client) thread).sendMessage(message.backupMasterSSL());
							}
							
							it.remove();
						}
					}else if(type.equals("2")){
						originalPeer = divided[6];
						while((counter+=in.read(buffer))!=-1){
							result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));
							if(counter-2==Integer.parseInt(chunkSize))
								break;
							
							buffer = new char[64000];
						}	
						
						result=Handler.trim(result);
						result=Arrays.copyOfRange(result, 0, result.length-2);
						
						HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber, new String(result).getBytes());
						
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
				
				out.println("ok");
			}
			
			
		} catch (IOException e) {
			
			System.out.println("SHITTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
			e.printStackTrace();
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
