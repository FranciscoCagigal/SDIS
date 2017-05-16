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
import java.util.HashMap;
import java.util.List;

import fileManager.Chunk;
import fileManager.CsvHandler;
import listeners.Multicast;
import listeners.MulticastBackup;
import listeners.MulticastRestore;
import protocols.ChunkBackup;
import protocols.ChunkRestore;
import protocols.EnterSystem;
import protocols.FileDeletion;
import protocols.ReadFile;
import protocols.ServiceState;
import protocols.SpaceReclaiming;

public class Peer extends UnicastRemoteObject  implements IPeer {

	private static final long serialVersionUID = 1L;
	private static String protocolVersion;
	private static int peerId;
	private static String remoteObject;
	
	private static InetAddress mcAddress;
	private static int mcPort;
	
	private static MulticastSocket mc;
	
	//proj2
	private static Boolean imMaster = false;
	private static InetAddress masterIP=null;
	private static int masterPort=0;
	private static long startTime;
	
	//restore	
	private static HashMap<Chunk,Boolean>waitingToBeReceived = new HashMap<Chunk,Boolean>();
	private static HashMap<Chunk,Boolean>waitingToBeSent = new HashMap<Chunk,Boolean>();
	
	//reclaim
	private static int diskSpace;
	private static HashMap<Chunk,Boolean>reclaimToBeSent = new HashMap<Chunk,Boolean>();
	
	//backupenh
	private static HashMap<String,Integer> backupenh = new  HashMap<String,Integer>();
	
	//deleteenh
	private static HashMap<String,Integer> deleteenh = new  HashMap<String,Integer>();
	
	//threads
	private static Runnable mc1;
	
	protected Peer() throws RemoteException {
		super();
		startTime= System.currentTimeMillis();
	}

	public static void main(String[] args) throws UnknownHostException, RemoteException, MalformedURLException, AlreadyBoundException{
		
		if(!validateArgs(args)){
			return;
		}
		
		//Peer peer= new Peer();
		
		//Registry registry = LocateRegistry.getRegistry();
		
		//IPeer iserver= (IPeer) peer;
		
		//registry.rebind(remoteObject, iserver);
		
		startTime= System.currentTimeMillis();
		
		joinGroups();
		
		System.out.println("Server ready");
		
		EnterSystem entry = new EnterSystem(mc);
		entry.findMaster();
		
		//if(!protocolVersion.equals("1.0")){
			//reclaimEnh();
		//}
		
	}
	
	public static void setImMaster(Boolean value){
		imMaster=value;
	}
	
	private static boolean validateArgs(String[] args) throws UnknownHostException{
		
		if(args.length!=5){
			System.out.println("Usage: Peer <protocol version> <peerID> <service access point> <mc address> <mc port>");
			return false;
		}
		
		protocolVersion=args[0];
		setPeerId(Integer.parseInt(args[1]));
		remoteObject=args[2];
		
		mcAddress=InetAddress.getByName(args[3]);
		mcPort=Integer.parseInt(args[4]);
		
		return true;
		
	}
	
	public static long getTime(){
		return startTime;
	}
	
	public static void setMasterAddress(InetAddress ip){
		masterIP=ip;
	}
	
	public static void setMasterPort(int port){
		masterPort=port;
	}
	
	public static InetAddress getMasterAddress(){
		return masterIP;
	}
	
	public static int getMasterPort(){
		return masterPort;
	}
	
	private static void joinGroups(){
		
		try {
			
			mc = new MulticastSocket(mcPort);
			mc.joinGroup(mcAddress);
			mc1=new Multicast(mc);
			new Thread(mc1).start();
			
			createDirs();
			
			System.out.println("Added to multicast group");
			
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
		
		f = new File("../metadata"+peerId);
		if (!f.exists()) {
			f.mkdir();
		}
		
		f = new File("../metadata"+peerId+"/ChunkList.csv");
		if(!f.exists()){
			try {
				f.createNewFile();				
				f = new File("../metadata"+peerId+"/MyChunks.csv");
				if(!f.exists()){
					f.createNewFile();
					CsvHandler.updateMemory(0);
					diskSpace=0;
				}else diskSpace=CsvHandler.getMemory();
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void backup(String version,File file, int replDeg) throws RemoteException {
		Runnable run=new ReadFile(version,file,replDeg);
		new Thread(run).start();
	}

	@Override
	public void restore(String version,String filename) throws RemoteException{
		Runnable run=new ChunkRestore(version,filename);
		new Thread(run).start();		
	}

	@Override
	public void delete(String version,String filename) throws RemoteException{
		Runnable run=new FileDeletion(version,filename);
		new Thread(run).start();
	}

	@Override
	public void reclaim(int space) throws RemoteException{
		diskSpace=space*1000;
		CsvHandler.updateMemory(space);
		Runnable run=new SpaceReclaiming();
		new Thread(run).start();
	}

	@Override
	public String state() throws RemoteException{
		// TODO Auto-generated method stub
		String result = ServiceState.getServiceState();
		return result;
		
	}

	public static boolean iHaveSpace(long space){
		System.out.println("entrei aqui : " + diskSpace + " - " + space);
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
	
	public static InetAddress getMcAddress(){
		return mcAddress;
	}
	
	public static int getMcPort(){
		return mcPort;
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
	
	public static void chunkTobeSentWasRcvd(Chunk chunk){
		waitingToBeSent.remove(chunk);
		waitingToBeSent.put(chunk,true);
	}
	
	public static void askedToSendChunk(Chunk chunk){
		waitingToBeSent.put(chunk,false);
	}
	
	public static void removeChunkSent(Chunk chunk){
		waitingToBeSent.remove(chunk);
	}
	
	public static String getVersion(){
		return protocolVersion;
	}
	//end
	
	//backup enh : start
	public static void addToBackupEnhancement(String str){
		backupenh.put(str, 0);
	}
	//backup enh : add 1 to key
	public static void changeBackupEnhancement(String str){
		int i= backupenh.get(str);
		backupenh.remove(str);
		backupenh.put(str, i+1);
	}
	//backup enh : get replication
	public static int getBackupEnhancement(String str){
		return backupenh.get(str);
	}
	//backup enh : contain key
	public static Boolean containsBackupEnhancement(String str){
		return backupenh.containsKey(str);
	}
	//backup enh : remove key
	public static void removeBackupEnhancement(String str){
		backupenh.remove(str);
	}
	
	//delete enh : start
	public static void addToDeleteEnhancement(String str){
		deleteenh.put(str, 0);
	}
	//delete enh : add 1 to key
	public static void changeDeleteEnhancement(String str){
		int i= deleteenh.get(str);
		deleteenh.remove(str);
		deleteenh.put(str, i+1);
	}
	//delete enh : get replication
	public static int getDeleteEnhancement(String str){
		return deleteenh.get(str);
	}
	//delete enh : contain key
	public static Boolean containsDeleteEnhancement(String str){
		return deleteenh.containsKey(str);
	}
	//delete enh : remove key
	public static void removeDeleteEnhancement(String str){
		deleteenh.remove(str);
	}
	
	//reclaim
	public static boolean reclaimHasChunk(Chunk chunk){
		return reclaimToBeSent.containsKey(chunk);
	}
	
	public static boolean reclaimChunkAlreadySent(Chunk chunk){
		return reclaimToBeSent.get(chunk);
	}
	
	public static void reclaimToSendChunk(Chunk chunk){
		reclaimToBeSent.put(chunk,false);
	}
	
	public static void removeReclaimSent(Chunk chunk){
		reclaimToBeSent.remove(chunk);
	}
	
	public static void reclaimTobeSentWasRcvd(Chunk chunk){
		reclaimToBeSent.remove(chunk);
		reclaimToBeSent.put(chunk,true);
	}
	
	private static void reclaimEnh(){
		List<Chunk> badChunks = CsvHandler.getBadChunks();
		for(Chunk chunk : badChunks){
			Runnable run=new ChunkBackup(chunk,"1.0");
			new Thread(run).start();
		}
	}
	
	public static Boolean amIMaster(){
		return imMaster;
	}
	
}
