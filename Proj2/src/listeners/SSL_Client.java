package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSL_Client {

	private static SSLSocket socket;
	private static int port;
	private static String host;
	

	public SSL_Client(SSLSocket socket) {
		
		this.socket = socket;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		
		System.setProperty("javax.net.ssl.keyStore","client.keys");
		
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		
		System.setProperty("javax.net.ssl.trustStore","truststore");
		
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();  
		
		socket = (SSLSocket) ssf.createSocket(host, port);
		
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
		System.out.println("Request sent!");
		
		// display response
		String received = in.readLine();
		
		System.out.println("Request answer: " + received);
		
		socket.close();
	}
	
}
