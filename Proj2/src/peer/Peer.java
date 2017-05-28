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
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;

import fileManager.Chunk;
import fileManager.CsvHandler;
import listeners.Multicast;
import listeners.MulticastBackup;
import listeners.MulticastRestore;
import listeners.SSL_Client;
import listeners.SSL_Server;
import protocols.ChunkBackup;
import protocols.ChunkRestore;
import protocols.EnterSystem;
import protocols.FileDeletion;
import protocols.ReadFile;
import protocols.ServiceState;
import protocols.ShareDatabase;
import protocols.SpaceReclaiming;
import user.User;

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
	private static int SSLport;
	private static Runnable clientThread;
	private static HashMap<String,Runnable> peerThreads = new HashMap<String,Runnable>();
	
	private static SSL_Server server;
	
	//reclaim
	private static int diskSpace;
	
	//threads
	private static Runnable mc1;
	
	public Peer() throws RemoteException {
		super();
		startTime= System.currentTimeMillis();
		//createDirs();
	}

	public static void main(String[] args) throws UnknownHostException, RemoteException, MalformedURLException, AlreadyBoundException{
		
		if(!validateArgs(args)){
			return;
		}
		
		Peer peer= new Peer();
		
		
		
		//Registry registry = LocateRegistry.getRegistry();
		
		//IPeer iserver= (IPeer) peer;
		
		//registry.rebind(remoteObject, iserver);
		
		joinGroups();
		
		Runnable server= new SSL_Server(SSLport);
		new Thread(server).start();
		
		System.out.println("Server ready");
		
		EnterSystem entry = new EnterSystem(mc);
		entry.findMaster();
		
		if(!imMaster){
			clientThread = new SSL_Client(Peer.getMasterAddress().getHostName(),Peer.getMasterPort());
			new Thread(clientThread).start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((SSL_Client)clientThread).sendStart((Peer.getPeerId()+" oi").getBytes());
		}else{
			peerThreads.put(peerId+"", null);
		}
		
		System.out.println("encontrei o master");

		
		if(!imMaster){
			
			
			diskSpace=100*1000;
			
			Runnable op = new ReadFile(new File("../Files1/test.txt"),2);
			new Thread(op).start();
			
			//Runnable op = new ShareDatabase();
			//new Thread(op).start();
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			op = new SpaceReclaiming();
			//new Thread(op).start();
			
			//op = new FileDeletion("test.txt");
			//new Thread(op).start();
			
			//op = new ChunkRestore("test.txt");
			//new Thread(op).start();
			
			
		}
		
	}
	
	public static void setClientThread(SSL_Client newClientConnection){
		clientThread=newClientConnection;
	}
	
	public static Runnable getClientThread(){
		return clientThread;
	}
	
	public static void setImMaster(Boolean value){
		imMaster=value;
	}
	
	public static HashMap<String,Runnable> getPeers(){
		return peerThreads;
	}
	
	private static boolean validateArgs(String[] args) throws UnknownHostException{
		
		if(args.length!=6){
			System.out.println("Usage: Peer <peerID> <password> <service access point> <mc address> <mc port> <sslport>");
			return false;
		}
		
		protocolVersion=args[1]; //mudar pra password
		setPeerId(Integer.parseInt(args[0]));
		remoteObject=args[2];
		
		mcAddress=InetAddress.getByName(args[3]);
		mcPort=Integer.parseInt(args[4]);
		setSSLport(Integer.parseInt(args[5]));
		
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
		
		f = new File("../metadata"+Peer.getPeerId()+"/Users.csv");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		f = new File("../metadata"+peerId+"/AllChunks.csv");
		try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	public void backup(User user,File file, int replDeg) throws RemoteException {
		Runnable run=new ReadFile(file,replDeg);
		new Thread(run).start();
	}

	@Override
	public void restore(User user,String filename) throws RemoteException{
		Runnable run=new ChunkRestore(filename);
		new Thread(run).start();		
	}

	@Override
	public void delete(User user,String filename) throws RemoteException{
		Runnable run=new FileDeletion(filename);
		new Thread(run).start();
	}

	@Override
	public void reclaim(User user, int space) throws RemoteException{
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

	public static String getVersion(){
		return protocolVersion;
	}
	
	public static Boolean amIMaster(){
		return imMaster;
	}

	@Override
	public void createUser(String name, String password, String level) throws RemoteException {
		User user = new User(name, password, level);
		CsvHandler.createUser(user);
	}

	public static int getSSLport() {
		return SSLport;
	}

	public static void setSSLport(int sSLport) {
		SSLport = sSLport;
	}
	
	public static MulticastSocket getMCSocket(){
		return mc;
	}
	
}
