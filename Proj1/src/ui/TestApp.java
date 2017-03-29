package ui;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import peer.IPeer;

public class TestApp {
	
	private static IPeer peer;
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException{
		
		if(!validateArgs(args)){
				System.out.println("non valid args");

			return;
		}
		
		callServer(args);
		
		
	}
	
	private static boolean validateArgs(String[] args) throws MalformedURLException, RemoteException, NotBoundException{

		
		if(args.length>4||args.length<2){
			System.out.println("Usage: TestApp <peer_ap> <operation> <opnd_1> <opnd_2>");
			return false;
		}
		
		Registry registry = LocateRegistry.getRegistry("localhost");
		peer= (IPeer)registry.lookup(args[0]); 
		
		switch(args[1].toLowerCase()){
		
			case "backup":{
				File f = new File(args[2]);
				if(args.length!=4 || !f.exists() || f.isDirectory()||!args[3].matches("^-?\\d+$"))
					return false;
				break;
			}
			case "restore":{
				if(args.length!=3){
					return false;
				}
				break;
			}
			case "delete":{
				if(args.length!=3){
					return false;
				}
				break;
			}
			case "reclaim":{
				if(args.length!=3 || !args[2].matches("^-?\\d+$")){
					return false;
				}
				break;
			}
			case "state":{
				if(args.length!=2){
					return false;
				}
				break;
			}
			default: return false;
		
		}		
		return true;
	}
	
	private static void callServer(String[] args) throws NumberFormatException, RemoteException{		
		
		
		switch(args[1].toLowerCase()){
		
			case "backup":{
				peer.backup(new File(args[2]).getAbsoluteFile(), Integer.parseInt(args[3]));
				break;
			}
			case "restore":{
				peer.restore(new File(args[2]).getAbsoluteFile().getName());
				break;
			}
			case "delete":{
				peer.delete(new File(args[2]).getAbsoluteFile().getName());
				break;
			}
			case "reclaim":{
				peer.reclaim(Integer.parseInt(args[2]));
				break;
			}
			case "state":{
				peer.state();
				break;
			}
		
		}
		
	}
}
