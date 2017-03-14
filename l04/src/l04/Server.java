package l04;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server implements IServer{
	
	private static ArrayList<Entry> entries = new ArrayList<Entry>();

	public Server(){}
	
	public static void main(String args[]) throws RemoteException, AlreadyBoundException {
		
		if(args.length!=1){
			System.out.println("Wrong usage");
			return;
		}
		
		Server server = new Server();
		
		Registry registry = LocateRegistry.getRegistry();
		IServer iserver= (IServer) UnicastRemoteObject.exportObject(server, 0);
		
		
		registry.bind(args[0], iserver);
		
		System.err.println("Server ready");
		
		
	}

	@Override
	public String request(String request) {
		
		String[] divided = request.split(" ");
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
		
		
		return answer;
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
