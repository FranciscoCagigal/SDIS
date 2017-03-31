package peer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fileManager.Chunk;
import fileManager.CsvHandler;
import listeners.Multicast;
import listeners.MulticastBackup;
import listeners.MulticastRestore;
import protocols.ChunkRestore;
import protocols.FileDeletion;
import protocols.ReadFile;
import protocols.SpaceReclaiming;

public class Peer extends UnicastRemoteObject  implements IPeer {

	private static final long serialVersionUID = 1L;
	private static int protocolVersion;
	private static int peerId;
	private static String remoteObject;
	
	private static InetAddress mcAddress, mdbAddress, mdrAddress;
	private static int mcPort;
	private static int mdbPort;
	private static int mdrPort;
	
	private static MulticastSocket mc;
	private static MulticastSocket mdb;
	private static MulticastSocket mdr;
	
	private static HashMap<Chunk,List<String>> backedUp = new HashMap<Chunk,List<String>>();
	private static HashMap<String,String> hashTranslations = new HashMap<String,String>();
	
	//restore	
	private static HashMap<Chunk,Boolean>waitingToBeReceived = new HashMap<Chunk,Boolean>();
	private static HashMap<Chunk,Boolean>waitingToBeSent = new HashMap<Chunk,Boolean>();
	
	//reclaim
	private static int diskSpace;
	
	//threads
	private static Runnable mc1,mc2,mc3;
	
	protected Peer() throws RemoteException {
		super();
	}

	public static void main(String[] args) throws UnknownHostException, RemoteException, MalformedURLException, AlreadyBoundException{
		
		if(!validateArgs(args)){
			return;
		}
		
		Peer peer= new Peer();
		
		Registry registry = LocateRegistry.getRegistry();
		
		IPeer iserver= (IPeer) peer;
		
		registry.rebind(remoteObject, iserver);		
		
		joinGroups();
		
		System.out.println("Server ready");
		
	}
	
	private static boolean validateArgs(String[] args) throws UnknownHostException{
		
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
		mdbPort=Integer.parseInt(args[6]);
		mdrAddress=InetAddress.getByName(args[7]);
		mdrPort=Integer.parseInt(args[8]);
		
		return true;
		
	}
	
	private static void joinGroups(){
		
		try {
			
			mc = new MulticastSocket(mcPort);
			mc.joinGroup(mcAddress);
			mc1=new Multicast(mc);
			new Thread(mc1).start();
			
			mdb = new MulticastSocket(mdbPort);
			mdb.joinGroup(mdbAddress);
			mc2=new MulticastBackup(mdb);
			new Thread(mc2).start();
			
			mdr = new MulticastSocket(mdrPort);
			mdr.joinGroup(mdrAddress);
			mc3=new MulticastRestore(mdr);
			new Thread(mc3).start();
			
			createDirs();
			
			System.out.println("Added to all multicast groups");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void createDirs(){
		File f = new File("../Files"+peerId);
		if (!f.exists()) {
			f.mkdir();
		}
		f = new File("../Chunks"+peerId);
		if (!f.exists()) {
			f.mkdir();
		}
		f = new File("../Restores"+peerId);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	@Override
	public void backup(File file, int replDeg) throws RemoteException {
		Runnable run=new ReadFile(file,replDeg);
		new Thread(run).start();
	}

	@Override
	public void restore(String filename) throws RemoteException{
		Runnable run=new ChunkRestore(filename);
		new Thread(run).start();		
	}

	@Override
	public void delete(String filename) throws RemoteException{
		Runnable run=new FileDeletion(filename);
		new Thread(run).start();
	}

	@Override
	public void reclaim(int space) throws RemoteException{
		diskSpace=space;
		Runnable run=new SpaceReclaiming();
		new Thread(run).start();
	}

	@Override
	public void state() throws RemoteException{
		// TODO Auto-generated method stub
		
	}

	public static boolean iHaveSpace(long space){
		if(diskSpace==0)
			return true;
		else if(diskSpace>space)
			return true;		
		return false;
	}
	
	public static int getPeerId() {
		return peerId;
	}

	private static void setPeerId(int peerId) {
		Peer.peerId = peerId;
	}
	
	public static InetAddress getMdbAddress(){
		return mdbAddress;
	}
	
	public static int getMdbPort(){
		return mdbPort;
	}
	
	public static InetAddress getMcAddress(){
		return mcAddress;
	}
	
	public static int getMcPort(){
		return mcPort;
	}
	
	public static InetAddress getMdrAddress(){
		return mdrAddress;
	}
	
	public static int getMdrPort(){
		return mdrPort;
	}
	
	public static void addBackup(Chunk chunk, String peerId){
		if(peerId==null){
			List<String> peerList = new ArrayList<String>();
			backedUp.put(chunk,peerList);
		}else{
			List<String> peerList =backedUp.get(chunk);
			peerList.add(peerId);
			backedUp.replace(chunk, peerList);
		}
		
	}
	
	//restore: initiator host
	public static boolean askedForChunk(Chunk chunk){
		return waitingToBeReceived.containsKey(chunk);
	}
	
	public static boolean hasChunkBeenReceived(Chunk chunk){
		return waitingToBeReceived.get(chunk);
	}
	
	public static void wantReceivedChunk(Chunk chunk){
		waitingToBeReceived.put(chunk,false);
	}
	
	public static byte[] getDataFromReceivedChunk(Chunk chunk){
		for(Chunk chunkReturned : waitingToBeReceived.keySet()){
			System.out.println("Vou buscar: " + chunkReturned.getChunkData());
			if(chunkReturned.equals(chunk))
				return chunkReturned.getChunkData();
		}		
		return null;
	}
	
	public static void addReceivedChunk(Chunk chunk){
		waitingToBeReceived.remove(chunk);
		waitingToBeReceived.put(chunk,true);
	}
	
	public static void removeReceivedChunk(Chunk chunk){
		waitingToBeReceived.remove(chunk);
	}
	
	//restore: other peers
	
	public static boolean iWantToSendChunk(Chunk chunk){
		return waitingToBeSent.containsKey(chunk);
	}
	
	public static boolean wasChunkAlreadySent(Chunk chunk){
		return waitingToBeSent.get(chunk);
	}
	
	public static void askedToSendChunk(Chunk chunk){
		waitingToBeSent.put(chunk,false);
	}
	
	public static void chunkTobeSentWasRcvd(Chunk chunk){
		waitingToBeSent.remove(chunk);
		waitingToBeSent.put(chunk,true);
	}
	
	public static void removeChunkSent(Chunk chunk){
		waitingToBeSent.remove(chunk);
	}
	//end
	
	public static boolean isMyChunk(Chunk chunk){
		return backedUp.containsKey(chunk);
	}
	
	public static int getNumberOfPeers(Chunk chunk){
		return backedUp.get(chunk).size();
	}
	
	/*public static void addChunk(Chunk chunk){
		CsvHandler.addChunk(chunk);
	}*/
	
	public static void addToTranslations(String filename, String hash){
		hashTranslations.put(filename, hash);
	}
	
	public static String getHashTranslation(String filename){
		return hashTranslations.get(filename);
	}
	
	public static Runnable getMDRlistener(){
		return mc3;
	}
}
