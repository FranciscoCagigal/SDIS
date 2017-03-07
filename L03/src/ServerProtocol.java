import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerProtocol extends Thread{
	
	private Socket echoSocket;

	private static ArrayList<Entry> entries = new ArrayList<Entry>();
	
	public ServerProtocol(Socket s){
		echoSocket=s;
	}
	
	private static String findEntryByPlate(String plateSearch){
		for (Entry entry : entries) {
			if(entry.getPlate().equals(plateSearch)){
				return entry.getName();
			}
		}
		return null;
	}
	
	public void run(){
		PrintWriter out=null;
		BufferedReader in=null;
		String received="";
		try {
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			received = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
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
		

		out.println(answer);
		out.close();
		try {
			echoSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
