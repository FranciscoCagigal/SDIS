import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLServer {
	
	private static int port;
	
	public static void main(String[] args) throws IOException{
		
		if(args.length<1){
			System.out.println("Incorrect number of arguments");
			return;
		}
		
		port=Integer.parseInt(args[0]);
		
		String[] list = new String[args.length-1];
		
		for(int i=1;i< args.length;i++){
			list[i-1]=args[i];
		}
		
		System.setProperty("javax.net.ssl.keyStore", "C:/Users/Not Miguel/Desktop/FEUP/SDIS/L05/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore","C:/Users/Not Miguel/Desktop/FEUP/SDIS/L05/truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		SSLServerSocketFactory   ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();  
		SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
        
        if(list.length>0){
        	sslServerSocket.setEnabledCipherSuites(list);
		}else sslServerSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());
		
		
       sslServerSocket.setNeedClientAuth(true);
       System.out.println(System.getProperty("javax.net.ssl.keyStore"));
        while (true) {
			
			System.out.println("waiting...");
			SSLSocket fodasse = (SSLSocket)sslServerSocket.accept();
			ServerProtocol protocol= new ServerProtocol(fodasse);
			protocol.run();
		}
		
		//socket.close();
	}
	
	
}
