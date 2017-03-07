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

public class Server {
	
	private static int port;
	
	public static void main(String[] args) throws IOException{
		
		if(args.length!=1){
			System.out.println("Incorrect number of arguments");
			return;
		}
		
		port=Integer.parseInt(args[0]);
		
		ServerSocket socket= new ServerSocket(port);
		Socket echoSocket = null;
		
		while (true) {
			System.out.println("waiting...");
			
			echoSocket=socket.accept();
			
			ServerProtocol protocol= new ServerProtocol(echoSocket);
			protocol.run();
		}
		
		//socket.close();
	}
	
	
}
