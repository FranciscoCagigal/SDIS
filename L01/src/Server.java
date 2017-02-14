import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class Server {
	
	private static int port;
	
	public static void main(String[] args) throws IOException{
		
		DatagramSocket socket = new DatagramSocket(port);
		
		byte[] buffer=null;
		DatagramPacket p = new DatagramPacket(buffer, buffer.length);
		
		while (true) {
			socket.receive(p);
			String received = new String(p.getData());
			String[] divided = received.split(" ");
			String name="";
			String plate="";
			String answer="";
			if(divided[0]== RequestType.REGISTER.toString()){
				name=divided[2];
				plate = divided[1];
				
				answer = "-1";
			}
			else if(divided[0]== RequestType.LOOKUP.toString()){
				plate = divided[1];
				
				answer = "123456 joao silva";
			}
			else {
				throw new IllegalArgumentException();
			}
			
			byte[]sbuf = answer.getBytes();
			InetAddress responseAddress = p.getAddress();
			int responsePort = p.getPort();
			p = new DatagramPacket(sbuf, sbuf.length, responseAddress, responsePort);
			socket.send(p);
			
		}
		
		//socket.close();
		
	}	
}
