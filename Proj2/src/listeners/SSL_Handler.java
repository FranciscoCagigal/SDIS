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
import user.User;

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
				
				String yolo=in.readLine();
				
				System.out.println("yolo" + yolo);
				
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
						Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),new String(result).getBytes(),Integer.parseInt(repDegree));
						CsvHandler.addMasterMeta(chunk, id, realName);
						HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
						Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
						
						int replication = Integer.parseInt(repDegree);
						int counterRepl=0;
						
						while(it.hasNext()) {
							Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
							String idThread = pair.getKey();
							Runnable thread = pair.getValue();
							
							if(!idThread.equals(Peer.getPeerId()+"")){
								answer+=idThread+" ";
								Message message = new Message(chunk,id,realName);
								if(((SSL_Client) thread).sendMessage(message.backupMasterSSL()).equals("ok")){
									CsvHandler.addMasterMeta(chunk, idThread, realName);
									counterRepl++;
								}
							}/*else{
								HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber, new String(result).getBytes());
								CsvHandler.addChunkMeta(chunk, id, realName);
								CsvHandler.addMasterMeta(chunk, Peer.getPeerId()+"", realName);
								counterRepl++;
								answer+=Peer.getPeerId()+" ";
							}*/
							
							if(replication==counterRepl)
								break;
							
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
					
				case Constants.COMMAND_REMOVED:{
					
					answer = "ok";
					
					char[] chunkData = new char[64000];
					char[] chunkDataTotal = new char[64000];
					
					filename=divided[3];
					chunkSize = divided[5];
					chunkNumber = divided[4];
					counter=0;
					if(type.equals("1")){
						while((counter+=in.read(chunkData))!=-1){
							chunkDataTotal=Message.concatBytes(Handler.trim(chunkDataTotal),Handler.trim(chunkData));
							chunkDataTotal=Handler.trim(chunkDataTotal);
							chunkDataTotal=Arrays.copyOfRange(chunkDataTotal, 0, chunkDataTotal.length-2);
							
							if(counter-2==Integer.parseInt(chunkSize))
								break;
							
							chunkData = new char[64000];
						}	
					}
					

					HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
					Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
					while(it.hasNext()) {
						Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
						String idThread = pair.getKey();
						Runnable thread = pair.getValue();
						Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),new String(chunkDataTotal).getBytes(),0);
						if(!idThread.equals(id) && !idThread.equals(Peer.getPeerId()+"")){
							answer+=idThread;
							Message message = new Message(chunk,id,realName);
							if(((SSL_Client) thread).sendMessage(message.backupMasterSSL()).equals("ok")){
								CsvHandler.addMasterMeta(chunk, idThread, realName);
							}
						}else if(idThread.equals(Peer.getPeerId()+"")){
							HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber, new String(chunkDataTotal).getBytes());
							CsvHandler.addChunkMeta(chunk, id, realName);
							CsvHandler.addMasterMeta(chunk, Peer.getPeerId()+"", realName);
							answer=Peer.getPeerId()+"";
						}
						
						it.remove();
					}
					
					
				break;
				}
					
				case Constants.COMMAND_GETMYCHUNKS:
						List<String> list = CsvHandler.getChunksByPeers(id);
						answer= Constants.COMMAND_GETMYCHUNKS + " " + list.size() + " "+ Constants.CRLF + Constants.CRLF;
						for(int i =0;i<list.size();i++){
							answer+= " " + list.get(i);//.substring(0,  names.get(i).length()-1);
						}
						break;
					
				case "RESTORE":
					
					if(type.equals("1")){
						realName = divided[4];
						filename = CsvHandler.getHash(realName);
						int chunkNr = 0;
						out.println("RESTOREANSWER");
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
									
									Message message = new Message(chunk,"");
									String chunkMsg = ((SSL_Client) thread).sendMessage(message.restoreMasterSSL());
									out.print(chunkMsg);
									out.flush();
									break;
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
						Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),chunkData,0);
						Message message = new Message(chunk);
						answer = new String(message.answerRestoreSSL());
					}
					in.readLine();
					
					
					break;
					
				case "CREATE":
					
					username = divided[4];
					newPassword = divided[5];
					level = divided[6];
					
					break;
				
				case  Constants.COMMAND_GETOTHERCHUNKS: 
										
					if(type.equals("1")){
						
						String numberOfNames = divided[4];
						
						char[] bufferNames = new char[64000];
						char[] resultNames = new char[64000];
						
						while((in.read(bufferNames))!=-1){
							resultNames=Message.concatBytes(Handler.trim(resultNames),Handler.trim(bufferNames));
							resultNames=Handler.trim(resultNames);
							resultNames=Arrays.copyOfRange(resultNames, 0, resultNames.length-2);
							
							if(new String(resultNames).split(" ").length==Integer.parseInt(numberOfNames)+1)
								break;
							
							bufferNames = new char[64000];
						}			
						
						
						
						if(resultNames.length>0)
							resultNames=Arrays.copyOfRange(resultNames, 1, resultNames.length);
						
						String[] dividedNames = new String(resultNames).split(" ");
						
						String peer;
						String listOfPeers="";
						
						for(int i=0;i<dividedNames.length;i++){
							String[] dividedName = dividedNames[i].split("\\.");
							if(dividedName.length==2 && (peer=CsvHandler.getInitiatorPeer(dividedName[0],Integer.parseInt(dividedName[1])))!=null){
								listOfPeers+= dividedNames[i]+";"+peer + " ";
							}
						}
						
						answer=Constants.COMMAND_GETOTHERCHUNKS + " " + listOfPeers.split(" ").length + " ";
						answer+= Constants.CRLF + Constants.CRLF;
						answer+=listOfPeers;
						
						System.out.println(answer);
						
					}
					
					break;
					
				case  Constants.COMMAND_NAMES: 
					
					String numberOfNames = divided[4];
					
					char[] bufferNames = new char[64000];
					char[] resultNames = new char[64000];
					
					if(type.equals("1")){
						while((in.read(bufferNames))!=-1){
							resultNames=Message.concatBytes(Handler.trim(resultNames),Handler.trim(bufferNames));
							resultNames=Handler.trim(resultNames);
							resultNames=Arrays.copyOfRange(resultNames, 0, resultNames.length-2);
							
							if(new String(resultNames).split(" ").length==Integer.parseInt(numberOfNames)+1)
								break;
							
							bufferNames = new char[64000];
						}			
						
						System.out.println(new String(resultNames) + " " + (new String(resultNames)).length());
						
						if(resultNames.length>0)
							resultNames=Arrays.copyOfRange(resultNames, 1, resultNames.length);
						
						String[] dividedNames = new String(resultNames).split(" ");
						
						for(int i=0;i<dividedNames.length;i++){
							String[] dividedName = dividedNames[i].split(";");
							if(dividedName.length==3 && !CsvHandler.checkUser(dividedName[0])){
								User user = new User(dividedName[0],dividedName[1],dividedName[2]);
								CsvHandler.createUser(user);
							}
						}
						
						List<String> names = CsvHandler.getUsers();
						answer=Constants.COMMAND_NAMES + " " + names.size() + " ";
						answer+= Constants.CRLF + Constants.CRLF;
						for(int i=0;i<names.size();i++){
							if(i+1!=names.size())
								answer+=names.get(i).substring(0, names.get(i).length()-1) + " ";
							else answer+=names.get(i);
						}
						
					}
					
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
