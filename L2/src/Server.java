import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {
	
	private static int port;
	private static ArrayList<Entry> entries = new ArrayList<Entry>();
	
	public static void main(String[] args) throws IOException{
		
		if(args.length!=1){
			System.out.println("Incorrect number of arguments");
			return;
		}
		
		port=Integer.parseInt(args[0]);
		
		DatagramSocket socket = new DatagramSocket(port);
		
		while (true) {
			System.out.println("waiting...");
			byte[] buffer=new byte[300];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			socket.receive(p);
			System.out.println("received");
			String received = new String(p.getData());
			String[] divided = received.split(" ");
			String name="";
			String plate="";
			String answer="";
			
			System.out.println(divided[0]);
			
			if(divided[0].equals(RequestType.REGISTER.toString())){
				for(int i=2;i<divided.length;i++){
					name+=divided[i]+" ";
				}
								
				name=name.trim();
				plate = divided[1];
				
				System.out.println("plate: " + plate);
				System.out.println("name: " + name);
				
				if(findEntryByPlate(plate)!=null){
					answer = "-1";
				}
				else{
					Entry entry=new Entry(name,plate);
					entries.add(entry);
					answer = Integer.toString(entries.size());
				}
				
			}
			else if(divided[0].equals(RequestType.LOOKUP.toString())){
				plate = divided[1].trim();
				System.out.println("plate: " + plate);
				if((name=findEntryByPlate(plate))!=null){
					answer = plate + " " + name;
				}
				else answer = "NOT_FOUND";
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
	
	private static String findEntryByPlate(String plateSearch){
		for (Entry entry : entries) {
			if(entry.getPlate().equals(plateSearch)){
				return entry.getName();
			}
		}
		return null;
	}
}
