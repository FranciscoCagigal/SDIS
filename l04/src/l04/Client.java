package l04;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
	
	 private Client() {}
	 
	 public static void main(String[] args) {
		 
		 if(args.length < 4 || args.length > 5){
				System.out.println("Incorrect number of arguments");
				return;
			}

	        String host = args[0];
	        
	        try {
	            Registry registry = LocateRegistry.getRegistry(host);
	            IServer server = (IServer) registry.lookup(args[1]);
	            
	            //read args
	            
	            String type = args[2].toUpperCase();
	    		String plate = args[3];
	    		
	    		String message=null, name;

	    		if(type.equals(RequestType.REGISTER.toString())){
	    			name = args[4];
	    			message = type + " " + plate + " " + name;
	    		}
	    		else if(type.equals(RequestType.LOOKUP.toString())){
	    			message = type + " " + plate;
	    		}
	            
	            String response = server.request(message);
	            
	            System.out.println("response: " + response);
	            
	            try {
	    			Thread.sleep(20000);
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	            
	        } catch (Exception e) {
	            System.err.println("Client exception: " + e.toString());
	            e.printStackTrace();
	        }
	    }
	
}
