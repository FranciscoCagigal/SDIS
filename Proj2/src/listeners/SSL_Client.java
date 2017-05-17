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

import protocols.Message;

public class SSL_Client implements Runnable {

	private static SSLSocket socket;
	
	private String sourcePeer;
	private InetAddress address;
	private int port;
	byte[] message;

	public SSL_Client(String sourcePeer, InetAddress address, int port) {
		this.sourcePeer = sourcePeer;
		this.address = address;
		this.port = port;
	}
	
	public SSL_Client(InetAddress address, int port,byte[] message) {
		this.address = address;
		this.port = port;
		this.message=message;
	}

	@Override
	public void run() {
		System.setProperty("javax.net.ssl.keyStore","../client.keys");
		
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		
		System.setProperty("javax.net.ssl.trustStore","../truststore");
		
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();  
		
		try {
			
			socket = (SSLSocket) ssf.createSocket(address.getHostName(), port);
			
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println(new String(message, StandardCharsets.UTF_8));
			
			// display response
			String received = in.readLine();
			
			System.out.println("Request answer: " + received);
			
			System.out.println("vou fechar " + new String(message, StandardCharsets.UTF_8));
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
