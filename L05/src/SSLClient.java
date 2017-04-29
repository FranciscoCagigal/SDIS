import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {
	
	private static String name,request,plate,host;
	private static int port;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		if(args.length < 4 || args.length > 5){
			System.out.println("Incorrect number of arguments");
			return;
		}
		
		String message="";
		
		host = args[0];
		port = Integer.parseInt(args[1]);
		request = args[2].toUpperCase();
		plate = args[3];

		String[] list =null;
		
		if(request.equals(RequestType.REGISTER.toString())){
			name = args[4];
			message = request + " " + plate + " " + name;
			list = new String[args.length-5];
			for(int i=5;i< args.length;i++){
				list[i-1]=args[i];
			}
		}
		else if(request.equals(RequestType.LOOKUP.toString())){
			message = request + " " + plate;
			list = new String[args.length-4];			
			for(int i=4;i< args.length;i++){
				list[i-1]=args[i];
			}
		}
		
		
		
		System.out.println("Request: " + message);
		
		// send request
		
		System.setProperty("javax.net.ssl.keyStore","C:/Users/Not Miguel/Desktop/FEUP/SDIS/L05/client.keys");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.ssl.trustStore","C:/Users/Not Miguel/Desktop/FEUP/SDIS/L05/truststore");
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		SSLSocketFactory ssf = (SSLSocketFactory ) SSLSocketFactory.getDefault();  
		SSLSocket sslsocket=(SSLSocket) ssf.createSocket(host,port);
		
		if(list !=null && list.length>0){
			sslsocket.setEnabledCipherSuites(list);
		}else sslsocket.setEnabledCipherSuites(sslsocket.getSupportedCipherSuites());
		
		PrintWriter out = new PrintWriter(sslsocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));

		System.out.println(Arrays.toString(sslsocket.getSupportedCipherSuites()));
		
		out.println(message);
		
		System.out.println("Request sent!");
		

		// display response
		String received = in.readLine();
		System.out.println("Request answer: " + received);
		
		sslsocket.close();
	}

}
