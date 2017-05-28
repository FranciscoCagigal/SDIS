package listeners;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import peer.Peer;

public class SSL_Server implements Runnable{

	private SSLServerSocket serverSocket;
	private int port;

	public SSL_Server(int port) {
		this.port = port;
	}


	@Override
	public void run() {
		
		System.setProperty("javax.net.ssl.keyStore", "../server.keys");
        
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        
		System.setProperty("javax.net.ssl.trustStore","../truststore");
        
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();  
		
		try {
			
			serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
        
		serverSocket.setNeedClientAuth(true);
		
        while (true) {
			
			System.out.println("waiting...");
			
			SSLSocket socket = null;
			
			try {
				socket = (SSLSocket)serverSocket.accept();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			Runnable handler= new SSL_Handler(socket);
			new Thread(handler).start();
			
		}		
	}
}
