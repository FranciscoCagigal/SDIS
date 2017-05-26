package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import fileManager.Chunk;
import fileManager.CsvHandler;
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
					
			// display response
			String received;
			
			System.out.println("vou iniciar cliente ");
			
			while(true){
			//	received = in.readLine();
				//System.out.println("Request answer: " + received);
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
	
	public synchronized String sendMessage(byte[] message){
		out.println(new String(message));
		String received = "";
		try {
			received = in.readLine();
			
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
					received=new String(result);
				}else if(received.equals("RESTOREANSWER")){					
					char[] buffer = new char[64000];
					char[] result = new char[64000];
					String file="";
					while(in.read(buffer)!=-1){
						buffer = Handler.trim(buffer);
						
						System.out.println("tamanho before send " + buffer.length);
						if(new String(Arrays.copyOfRange(buffer, 0, buffer.length-2)).equals("ok")){
							break;	
						}				
						file+=new String(buffer);
						buffer = new char[64000];					
					}
					
					received = file;
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
							System.out.println(dividedNames[i] + " " + dividedNames[i].length());
							System.out.println(dividedName[0] + " " + dividedName[1]);
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
						received="ok";
					
				}
			}
			//System.out.println("Request answer: " + received);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return received;
	}
	
}
