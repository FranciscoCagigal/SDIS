import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	
	private static String name,request,plate,host;
	private static int port;
	
	public static void main(String[] args) throws IOException{
		if(args.length < 4 || args.length > 5){
			System.out.println("Incorrect number of arguments");
			return;
		}
		
		String message="";
		
		host = args[0];
		port = Integer.parseInt(args[1]);
		request = args[2].toUpperCase();
		plate = args[3];

		if(request.equals(RequestType.REGISTER.toString())){
			name = args[4];
			message = request + " " + plate + " " + name;
		}
		else if(request.equals(RequestType.LOOKUP.toString())){
			message = request + " " + plate;
		}
		
		System.out.println("Request: " + message);
		
		// send request
		
		Socket socket = new Socket(host,port);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		out.println(message);
		
		System.out.println("Request sent!");
		

		// display response
		String received = in.readLine();
		System.out.println("Request answer: " + received);
		
		socket.close();
	}

}
