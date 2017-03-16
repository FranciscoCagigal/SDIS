package peer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import listeners.Multicast;
import listeners.MulticastBackup;
import listeners.MulticastRestore;
import protocols.ChunkBackup;

public class Peer extends UnicastRemoteObject  implements IPeer {

	private int protocolVersion;
	private static int peerId;
	private String remoteObject;
	
	private static InetAddress mcAddress, mdbAddress, mdrAddress;
	private int mcPort;
	private static int mdbPort;
	private int mdrPort;
	
	private MulticastSocket mc,mdb,mdr;
	
	protected Peer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void main(String[] args) throws UnknownHostException, RemoteException, MalformedURLException, AlreadyBoundException{
		
		if(!validateArgs(args)){
			return;
		}
		
		Peer peer= new Peer();
		
		Registry registry = LocateRegistry.getRegistry();
		
		IPeer iserver= (IPeer) UnicastRemoteObject.exportObject(peer, 0);
		
		registry.bind(remoteObject, iserver);
		
		joinGroups();
		
		System.err.println("Server ready");
		
	}
	
	private boolean validateArgs(String[] args) throws UnknownHostException{
		
		if(args.length!=9){
			System.out.println("Usage: Peer <protocol version> <peerID> <service access point> <mc address> <mc port> <mdb address> <mdb port> <mdr address> <mdr port> ");
			return false;
		}
		
		protocolVersion=Integer.parseInt(args[0]);
		setPeerId(Integer.parseInt(args[1]));
		remoteObject=args[2];
		
		mcAddress=InetAddress.getByName(args[3]);
		mcPort=Integer.parseInt(args[4]);
		mdbAddress=InetAddress.getByName(args[5]);
		mcPort=Integer.parseInt(args[6]);
		mdrAddress=InetAddress.getByName(args[7]);
		mdrPort=Integer.parseInt(args[8]);
		
		return true;
		
	}
	
	private void joinGroups(){
		
		try {
			mc = new MulticastSocket(mcPort);
			mc.joinGroup(mcAddress);
			Multicast mc1=new Multicast(mc);
			mc1.run();
			
			mdb = new MulticastSocket(mdbPort);
			mdb.joinGroup(mdbAddress);
			MulticastBackup mc2=new MulticastBackup(mdb);
			mc2.run();
			
			mdr = new MulticastSocket(mcPort);
			mdr.joinGroup(mcAddress);
			MulticastRestore mc3=new MulticastRestore(mdr);
			mc3.run();			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void backup(File file, int replDeg) throws RemoteException {
		new ChunkBackup(file,replDeg).run();
		
	}

	@Override
	public void restore(File file) throws RemoteException{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(File file) throws RemoteException{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reclaim(int space) throws RemoteException{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void state() throws RemoteException{
		// TODO Auto-generated method stub
		
	}

	public static int getPeerId() {
		return peerId;
	}

	private void setPeerId(int peerId) {
		Peer.peerId = peerId;
	}
	
	public static InetAddress getMdbAddress(){
		return mdbAddress;
	}
	
	public static int getMdbPort(){
		return mdbPort;
	}
}
