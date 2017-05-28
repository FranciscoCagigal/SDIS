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
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import javax.net.ssl.SSLSocket;

import fileManager.Chunk;
import fileManager.CsvHandler;
import fileManager.HandleFiles;
import peer.Peer;
import protocols.Constants;
import protocols.Election;
import protocols.EnterSystem;
import protocols.Message;
import user.User;

public class SSL_Handler implements Runnable {

	private SSLSocket socket;
	private PrintWriter out=null;
	private BufferedReader in=null;
	private boolean awaitedAnswer=false;
	private String answer="";
	private final Semaphore sem = new Semaphore(0, true);
	
	public SSL_Handler(SSLSocket socket) {
		
		this.socket = socket;
	}


	@Override
	public void run() {
		
		String received="";
		
		try {
		
			out = new PrintWriter(socket.getOutputStream(), true);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			while(true){
				received = in.readLine();
				
				if(awaitedAnswer){
					System.out.println("resposta em espera " +received );
					answer=received;
					sem.release();
					continue;
				}
				
				System.out.println("vou ler input" + received);
				
				String[] divided = received.split(" ");
				
				System.out.println("size " + divided.length);
				
				//TODO verificar autenticacao do peer
				
				String type = divided[0];
				
				String id = divided[1];
				
				if(divided[1].equals("oi")){
					Peer.getPeers().put(divided[0], this);
					continue;
				}
				
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
						Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),new String(result).getBytes(),Integer.parseInt(repDegree));
						CsvHandler.addMasterMeta(chunk, id, realName);
						HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
						Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
						
						int replication = Integer.parseInt(repDegree);
						int counterRepl=0;
						answer="PEERBACKUP ";
						while(it.hasNext()) {
							Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
							String idThread = pair.getKey();
							Runnable thread = pair.getValue();
							if(thread==this){
								Message message = new Message(chunk,id,realName);
								out.println(new String(message.backupMasterSSL()));
								if(in.readLine().equals("ok")){
									answer+=idThread+" ";
									CsvHandler.addMasterMeta(chunk, idThread, realName);
									counterRepl++;
								}
							}
							else if(!idThread.equals(Peer.getPeerId()+"")){
								Message message = new Message(chunk,id,realName);
								if(((SSL_Handler) thread).sendMessage(message.backupMasterSSL()).equals("ok")){
									answer+=idThread+" ";
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
								((SSL_Handler) thread).sendMessageNoRspns(message.deleteMasterSSL());
								CsvHandler.deleteChunks(filename, "../metadata"+Peer.getPeerId()+"/AllChunks.csv");
							}
							
							it.remove();
						}
					}
					
					break;
				case Constants.COMMAND_REMOVED:{
					
					answer = "ok";
					
					char[] chunkData = new char[64000];
					char[] chunkDataTotal = new char[64000];
					
					filename=divided[4];
					chunkSize = divided[6];
					chunkNumber = divided[5];
					counter=0;
					if(type.equals("1")){
						while((counter+=in.read(chunkData))!=-1){
							chunkDataTotal=Message.concatBytes(Handler.trim(chunkDataTotal),Handler.trim(chunkData));
							chunkDataTotal=Handler.trim(chunkDataTotal);
							if(counter-2==Integer.parseInt(chunkSize))
								break;
							
							chunkData = new char[64000];
						}	
					}
					chunkDataTotal=Handler.trim(chunkDataTotal);
					chunkDataTotal=Arrays.copyOfRange(chunkDataTotal, 0, chunkDataTotal.length-2);

					HashMap<String,Runnable> copy = new HashMap<String,Runnable>(Peer.getPeers());
					Iterator<Entry<String,Runnable>> it = copy.entrySet().iterator();
					String newPeerId="";
					Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),new String(chunkDataTotal).getBytes(),0);
					while(it.hasNext()) {
						Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
						String idThread = pair.getKey();
						Runnable thread = pair.getValue();
						
						if(!idThread.equals(id) && !idThread.equals(Peer.getPeerId()+"")){
							Message message = new Message(chunk,id,realName);
							if(((SSL_Handler) thread).sendMessage(message.backupMasterSSL()).equals("ok")){
								CsvHandler.replacePeer(chunk,idThread,Peer.getPeerId()+"");
								newPeerId=idThread;
								break;
							}
						}else if(idThread.equals(Peer.getPeerId()+"")){
							HandleFiles.writeFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber, new String(chunkDataTotal).getBytes());
							CsvHandler.addChunkMeta(chunk, id, CsvHandler.getRealName(chunk.getFileId()));
							CsvHandler.replacePeer(chunk,id,Peer.getPeerId()+"");
							newPeerId=Peer.getPeerId()+"";
							break;
						}
						
						it.remove();
					}
					copy = new HashMap<String,Runnable>(Peer.getPeers());
					it = copy.entrySet().iterator();
					while(it.hasNext()) {
						Map.Entry<String,Runnable> pair = (Map.Entry<String,Runnable>)it.next();
						String idThread = pair.getKey();
						Runnable thread = pair.getValue();
						if(idThread.equals(CsvHandler.getInitiatorPeer(chunk.getFileId(), chunk.getChunkNumber()).split(Constants.COMMA_DELIMITER)[0])){
							Message message = new Message(chunk,id,newPeerId);
							((SSL_Handler) thread).sendMessageNoRspns(message.createReplace());
							break;
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
						String file="";

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
									((SSL_Handler) thread).sendMessageNoRspns(message.restoreMasterSSL());
									
									char[] chunkData = new char[64000];
									char[] chunkDataTotal = new char[64000];
									//String chunkReceived=in.readLine();
									//
									String chunkReceived=in.readLine();
									System.out.println("fodasse " + chunkReceived);
									counter=0;
									while((counter+=in.read(chunkData))!=-1){
										System.out.println("counter " + counter + " " + new String(chunkData).substring(0,10));
										chunkDataTotal=Message.concatBytes(Handler.trim(chunkDataTotal),Handler.trim(chunkData));
										chunkDataTotal=Handler.trim(chunkDataTotal);
										System.out.println("counter " +counter);
										System.out.println("fodasse " + chunkReceived.split(" ")[0]);
										if(counter-2==Integer.parseInt(chunkReceived.split(" ")[0]))
											break;
										
										chunkData = new char[64000];
									}	
									System.out.println("fodasse " + file.length());
									file+=new String(chunkDataTotal).substring(0,new String(chunkDataTotal).length()-2);
									break;
								}
								
								it.remove();
							}
							chunkNr++;
						}
						out.println("RESTOREANSWER");
						out.println(file);
						out.println("ok");
						System.out.println("o file tem " + file.length());
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
			System.out.println("foi abaixo a socket server");
			
			/*if(!Peer.amIMaster()){
				Random rnd = new Random();
				try {
					Thread.sleep(rnd.nextInt(1000));
					if(!Handler.isElectionStarted()){
						Election election = new Election(Peer.getMCSocket());
						election.startElection();
					}
					Thread.sleep(3000);
					Handler.setElectionStarted(false);
					if(Peer.amIMaster()){
						
					}else{
						EnterSystem entry = new EnterSystem(Peer.getMCSocket());
						entry.findMaster();
						Thread.sleep(3000);
						SSL_Client clientThread = new SSL_Client(Peer.getMasterAddress().getHostName(),Peer.getMasterPort());
						new Thread(clientThread).start();
						Peer.setClientThread(clientThread);
					}
					
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}*/
			
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
	
	private void sendMessageNoRspns(byte[] message){
		
		out.println(new String(message));
	}
	
	private String sendMessage(byte[] message){
		awaitedAnswer=true;
		System.out.println("vou mandar msg ");
		out.println(new String(message));
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		awaitedAnswer=false;
		return answer;
	}
}
