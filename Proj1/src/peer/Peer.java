package peer;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Peer extends UnicastRemoteObject  implements IPeer {

	private int protocolVersion,peerId;
	private String remoteObject;
	
	private static InetAddress mcAddress, mdbAddress, mdrAddress;
	private int mcPort, mdbPort, mdrPort;
	
	protected Peer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void main(String[] args) throws UnknownHostException, RemoteException, MalformedURLException{
		
		if(!validateArgs(args)){
			return;
		}
		
		Peer peer= new Peer();
		Naming.rebind("peerRMI", peer);//acho q tem q ter aqui o id do peer
		
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

	@Override
	public void backup(File file, int replDeg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restore(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reclaim(int space) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void state() {
		// TODO Auto-generated method stub
		
	}
}
