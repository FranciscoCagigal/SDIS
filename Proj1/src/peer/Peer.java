package peer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class Peer implements IPeer {
	
	private int protocolVersion,peerId;
	private String remoteObject;
	
	private static InetAddress mcAddress, mdbAddress, mdrAddress;
	private int mcPort, mdbPort, mdrPort;

	public void main(String[] args) throws UnknownHostException{
		
		if(!validateArgs(args)){
			return;
		}
		
	}
	
	private boolean validateArgs(String[] args) throws UnknownHostException{
		
		if(args.length!=9){
			System.out.println("Usage: Peer <protocol version> <peerID> <service access point> <mc address> <mc port> <mdb address> <mdb port> <mdr address> <mdr port> ");
			return false;
		}
		
		protocolVersion=Integer.parseInt(args[0]);
		peerId=Integer.parseInt(args[1]);
		remoteObject=args[2];
		
		mcAddress=InetAddress.getByName(args[3]);
		mcPort=Integer.parseInt(args[4]);
		mdbAddress=InetAddress.getByName(args[5]);
		mcPort=Integer.parseInt(args[6]);
		mdrAddress=InetAddress.getByName(args[7]);
		mdrPort=Integer.parseInt(args[8]);
		
		return true;
		
	}
}
