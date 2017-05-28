package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import fileManager.Chunk;
import fileManager.CsvHandler;
import fileManager.HandleFiles;
import peer.Peer;
import protocols.Constants;
import protocols.Message;
import user.User;

public class SSL_Client implements Runnable {

	private static SSLSocket socket;

	private static PrintWriter out;
	private static BufferedReader in;
	private String host;
	private int port;
	String answer="";
	private final Semaphore sem = new Semaphore(0, true);
	
	public SSL_Client(String host, int port) {
		this.host=host;
		this.port=port;
	}

	@Override
	public void  run() {
		System.setProperty("javax.net.ssl.keyStore","../client.keys");
		
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		
		System.setProperty("javax.net.ssl.trustStore","../truststore");
		
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();  
		
		try {
			
			socket = (SSLSocket) ssf.createSocket(host,port);			
			out = new PrintWriter(socket.getOutputStream(), true);			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			System.out.println("vou iniciar cliente ");
			
			while(true){
				String received = "";
					received = in.readLine();
					if(received.length()<200)
						System.out.println("recebi isto cliente " + received);
					if(!received.equals("ok")){
						String[] divided = received.split(" ");
						if(divided.length==4 && divided[2].equals(Constants.COMMAND_RESTORE)){
							
							char[] buffer = new char[64000];
							char[] result = new char[64000];
							
							int counter=0;
							while((counter+=in.read(buffer))!=-1){
								
								result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));
								if(counter-2>=Integer.parseInt(divided[3]))
									break;
								
								buffer = new char[64000];
							}
							result=Handler.trim(result);
							result=Arrays.copyOfRange(result, 2, result.length-2);
							
						}else if(received.equals("RESTOREANSWER")){					
							char[] buffer = new char[64000];
							char[] result = new char[64000];
							String file="";
							while(in.read(buffer)!=-1){
								buffer = Handler.trim(buffer);
								
								if(new String(Arrays.copyOfRange(buffer, 0, buffer.length-2)).equals("ok")){
									break;	
								}				
								file+=new String(buffer);
								buffer = new char[64000];					
							}
							answer=file.substring(0,file.length()-2);
							sem.release();
						}else if(divided[0].equals("PEERBACKUP")){
							
							for(int i=1;i <divided.length;i++){
								answer+=divided[i]+" ";
							}
							sem.release();							
						}else if(divided[0].equals(Constants.COMMAND_NAMES)){
							char[] bufferNames = new char[64000];
							char[] resultNames = new char[64000];
							
							while((in.read(bufferNames))!=-1){
								resultNames=Message.concatBytes(Handler.trim(resultNames),Handler.trim(bufferNames));
								resultNames=Handler.trim(resultNames);
								resultNames=Arrays.copyOfRange(resultNames, 0, resultNames.length-2);
								System.out.println(new String(resultNames) + " " + resultNames.length);
								if(new String(resultNames).split(" ").length==Integer.parseInt(divided[1]))
									break;
									
								bufferNames = new char[64000];
							}
							
							
							if(resultNames.length>1)
								resultNames=Arrays.copyOfRange(resultNames, 2, resultNames.length);
							
							String[] dividedNames = new String(resultNames).split(" ");
							
							for(int i=0;i<dividedNames.length;i++){
								String[] dividedName = dividedNames[i].split(";");
								if(dividedName.length==3&&!CsvHandler.checkUser(dividedName[0])){
									User user = new User(dividedName[0],dividedName[1],dividedName[2]);
									CsvHandler.createUser(user);
								}
							}
							received="ok";
						} else if(divided[0].equals(Constants.COMMAND_GETMYCHUNKS)){
							char[] bufferNames = new char[64000];
							char[] resultNames = new char[64000];
							
							while((in.read(bufferNames))!=-1){
								resultNames=Message.concatBytes(Handler.trim(resultNames),Handler.trim(bufferNames));
								resultNames=Handler.trim(resultNames);
								resultNames=Arrays.copyOfRange(resultNames, 0, resultNames.length-2);
									
								if(new String(resultNames).split(" ").length==Integer.parseInt(divided[1])+1)
									break;
									
								bufferNames = new char[64000];
							}
								if(resultNames.length>2)
									resultNames=Arrays.copyOfRange(resultNames, 3, resultNames.length);
								
								String[] dividedNames = new String(resultNames).split(" ");
								
								for(int i=0;i<dividedNames.length;i++){
									String[] dividedName = dividedNames[i].split(";");
									if(dividedName.length>4){
										String listOfPeers="";
										for(int j=4;j<dividedName.length;j++){
											listOfPeers+=dividedName[j] + " ";
										}
										CsvHandler.addMyChunkMeta(new Chunk(dividedName[0],Integer.parseInt(dividedName[1]),null,0), listOfPeers, dividedName[2]);
									}
								}
								received="ok";
							
							
						}else if(divided[0].equals(Constants.COMMAND_GETOTHERCHUNKS)){
							char[] bufferNames = new char[64000];
							char[] resultNames = new char[64000];
							
							while((in.read(bufferNames))!=-1){
								resultNames=Message.concatBytes(Handler.trim(resultNames),Handler.trim(bufferNames));
								resultNames=Handler.trim(resultNames);
								resultNames=Arrays.copyOfRange(resultNames, 0, resultNames.length-2);
								System.out.println(new String(resultNames));
								if(new String(resultNames).split(" ").length==Integer.parseInt(divided[1]))
									break;
									
								bufferNames = new char[64000];
							}
								if(resultNames.length>2)
									resultNames=Arrays.copyOfRange(resultNames, 3, resultNames.length);
								
								String[] dividedNames = new String(resultNames).split(" ");
								
								for(int i=0;i<dividedNames.length;i++){
									String[] dividedName = dividedNames[i].split(";");
									if(dividedName.length==3){
										String[] dividedfile = dividedName[0].split("\\.");
										CsvHandler.addChunkMeta(new Chunk(dividedfile[0],Integer.parseInt(dividedfile[1]),null,0), dividedName[1], dividedName[2]);
									}
								}
							
						}else if(divided[0].equals(Constants.COMMAND_BACKUP)){
							String originalPeer = divided[3];
							String realName=divided[5];
							String chunkNumber = divided[2];
							String filename = divided[1];
							String chunkSize = divided[4];
							int counter=0;
							
							char[] buffer = new char[64000];
							char[] result = new char[64000];
							while((counter+=in.read(buffer))!=-1){
								result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));

								if(counter<50)
									System.out.println("lol " + new String(buffer).substring(0, counter));
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
						else if(divided[0].equals(Constants.COMMAND_DELETE)){
							 String filename = divided[1];
							 HandleFiles.eraseFile( "../Chunks"+Peer.getPeerId()+"/", filename);
							 CsvHandler.deleteChunks(filename, "../metadata"+Peer.getPeerId()+"/MyChunks.csv");
							 CsvHandler.deleteChunks(filename, "../metadata"+Peer.getPeerId()+"/ChunkList.csv");
							 received="ok";
						}else if(divided[0].equals(Constants.COMMAND_RESTORE)){
							String chunkNumber = divided[2];
							String filename = divided[1];
							byte[] chunkData = HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+filename+"."+chunkNumber);
							chunkData=Handler.trim(chunkData);
							Chunk chunk = new Chunk(filename,Integer.parseInt(chunkNumber),chunkData,0);
							Message message1 = new Message(chunk);
							out.println(new String(message1.answerRestoreSSL()));
						}
					}		
			}
			
			
			
		} catch (UnknownHostException e) {
			System.out.println("vou sair do ciclo ");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("vou sair do ciclo ");
			System.out.println("vou fechar ");
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	public synchronized void sendStart(byte[] message){
		
		out.println(new String(message));

	}
	
	public synchronized String sendMessage(byte[] message){
		
		out.println(new String(message));
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return answer;

	}
	
}
