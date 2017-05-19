package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLSocket;

import fileManager.Chunk;
import fileManager.CsvHandler;
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
				
				String realName = "";
				
				String answer = "ok";
				
				byte[] bytesBody;
				
				in.readLine();
				
				switch(protocol){
				
				case Constants.COMMAND_BACKUP:
					
					chunkNumber = divided[5];
					filename = divided[4];
					chunkSize = divided[7];
					
					char[] buffer = new char[64000];
					char[] result = new char[64000];
					
					int counter=0;
					
					if(type.equals("1")){
						
						repDegree = divided[6];	
						realName=divided[8];
						
						while((counter+=in.read(buffer))!=-1){
							result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));
							if(counter-2==Integer.parseInt(chunkSize))
								break;
							
							buffer = new char[64000];
						}			
						result=Handler.trim(result);
						result=Arrays.copyOfRange(result, 0, result.length-2);
						
						answer="";
						
						HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
						Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
						while(it.hasNext()) {
							Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
							String idThread = pair.getKey();
							Runnable thread = pair.getValue();
							Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),new String(result).getBytes(),Integer.parseInt(repDegree));
							if(!idThread.equals(Peer.getPeerId()+"")){
								answer+=idThread+" ";
								Message message = new Message(chunk,id,realName);
								if(((SSL_Client) thread).sendMessage(message.backupMasterSSL()).equals("ok")){
									CsvHandler.addMasterMeta(chunk, idThread, realName);
								}
							}
							
							it.remove();
						}
					}else if(type.equals("2")){
						originalPeer = divided[6];
						realName=divided[8];
						while((counter+=in.read(buffer))!=-1){
							result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));
							if(counter-2==Integer.parseInt(chunkSize))
								break;
							
							buffer = new char[64000];
						}	
						
						result=Handler.trim(result);
						result=Arrays.copyOfRange(result, 0, result.length-2);
						
						HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber, new String(result).getBytes());
						Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),null,0);
						CsvHandler.addChunkMeta(chunk, originalPeer, realName);
					}
					
					
					break;
					
				case "DELETE":
					
					if(type.equals("1")){
						realName = divided[4];
						filename = CsvHandler.getHash(realName);
						HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
						Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
						List<String> peersContained = CsvHandler.getPeersChunk(filename);
						while(it.hasNext()) {
							Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
							String idThread = pair.getKey();
							Runnable thread = pair.getValue();
							Chunk chunk = new Chunk(filename,0,null,0);
												
							if(!idThread.equals(Peer.getPeerId()+"") && peersContained.contains(idThread)){
								Message message = new Message(chunk,"");
								((SSL_Client) thread).sendMessage(message.deleteMasterSSL());
								CsvHandler.deleteChunks(filename, "../metadata"+Peer.getPeerId()+"/AllChunks.csv");
							}
							
							it.remove();
						}
					}else if(type.equals("2")){
						 filename = divided[4];
						 HandleFiles.eraseFile( "../Chunks"+Peer.getPeerId()+"/", filename);
						 CsvHandler.deleteChunks(filename, "../metadata"+Peer.getPeerId()+"/MyChunks.csv");
						 CsvHandler.deleteChunks(filename, "../metadata"+Peer.getPeerId()+"/ChunkList.csv");
					}
					
					break;
					
				case "RECLAIM":
					
					
					
					break;
					
				case "RESTORE":
					
					if(type.equals("1")){
						realName = divided[4];
						filename = CsvHandler.getHash(realName);
						int chunkNr = 0;
						while(true){
							HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
							Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
							List<String> peers = CsvHandler.getPeersChunk(filename,chunkNr);
							if(peers.size()==0)
								break;
							while(it.hasNext()) {
								Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
								String idThread = pair.getKey();
								Runnable thread = pair.getValue();
								Chunk chunk = new Chunk(filename,chunkNr,null,0);
													
								if(!idThread.equals(Peer.getPeerId()+"") && peers.contains(idThread)){
									out.println("RESTOREANSWER");
									Message message = new Message(chunk,"");
									out.println(((SSL_Client) thread).sendMessage(message.restoreMasterSSL()));
								}
								
								it.remove();
							}
							chunkNr++;
						}
						answer="ok";
					}else if(type.equals("2")){
						chunkNumber = divided[5];
						filename = divided[4];
						byte[] chunkData = HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber);
						chunkData=Handler.trim(chunkData);
						System.out.println(chunkData.length);
						Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),chunkData,0);
						Message message = new Message(chunk);
						answer = new String(message.answerRestoreSSL()); 
						System.out.println("vou manda o chunk");
					}
					in.readLine();
					
					break;
					
				case "CREATE":
					
					username = divided[4];
					newPassword = divided[5];
					level = divided[6];
					
					break;
					
				}
				
				out.println(answer);
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
