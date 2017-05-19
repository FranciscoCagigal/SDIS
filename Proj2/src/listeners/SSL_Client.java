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

import peer.Peer;
import protocols.Constants;
import protocols.Message;

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
					received=new String(Handler.trim(buffer));
				}else if(received.equals("RESTOREANSWER")){
					System.out.print("entrei no restoreanswer");
					
					char[] buffer = new char[64000];
					char[] result = new char[64000];
					
					int counter=0;
					String file="";
					while((counter+=in.read(buffer))!=-1){
						System.out.println("counter " + counter);
						if(new String(Handler.trim(buffer)).equals("ok"))
							break;						
						result=Message.concatBytes(Handler.trim(result),Handler.trim(buffer));
						buffer = new char[64000];
						file+=new String(result);
					}
					
					received = file;
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
