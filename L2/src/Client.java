import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
	
	private static String name,request,plate,MultiCastAddresss;
	private static int MultiCastPort;
	
	public static void main(String[] args) throws IOException{
		if(args.length < 4 || args.length > 5){
			System.out.println("Incorrect number of arguments");
			return;
		}
		
		String message="";
		
		MultiCastAddresss = args[0];
		MultiCastPort = Integer.parseInt(args[1]);
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
		
		InetAddress addr = InetAddress.getByName(MultiCastAddresss);
		
		byte[] adbuf = new byte[256];
		
		MulticastSocket clientSocket = new MulticastSocket(MultiCastPort);
		clientSocket.joinGroup(addr);
		
		DatagramPacket adPacket = new DatagramPacket(adbuf, adbuf.length);
		clientSocket.receive(adPacket);
		
		String admsg = new String(adbuf, 0, adbuf.length);
		String[] addivided = admsg.split(" ");
		String ServiceAddress = addivided[0];
		int ServicePort = Integer.parseInt(addivided[1].trim());
		
		
		// send request
		DatagramSocket socket = new DatagramSocket();
		byte[]sbuf = message.getBytes();		
		InetAddress address = InetAddress.getByName(ServiceAddress);
		DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, ServicePort);
		socket.send(packet);
		
		System.out.println("Request sent!");
		
		// get response
		byte[] rbuf = new byte[300];
		packet = new DatagramPacket(rbuf, rbuf.length);
		socket.receive(packet);
		// display response
		String received = new String(packet.getData());
		System.out.println("Request answer: " + received);
		socket.close();
	}

}
