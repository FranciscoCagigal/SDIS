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
import java.util.Arrays;
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
import protocols.Constants;
import protocols.EnterSystem;
import protocols.FileDeletion;
import protocols.Message;
import protocols.ReadFile;
import protocols.ServiceState;
import protocols.ShareDatabase;
import protocols.SpaceReclaiming;
import user.User;

public class Peer extends UnicastRemoteObject  implements IPeer {

	private static final long serialVersionUID = 1L;
	private static String password;
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
		
		Runnable server= new SSL_Server(SSLport);
		new Thread(server).start();
		
		System.out.println("Server ready");
		
		EnterSystem entry = new EnterSystem(mc);
		entry.findMaster();
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
			
			ShareDatabase share = new ShareDatabase();
			new Thread(share).start();
			
		}else{
			peerThreads.put(peerId+"", null);
			List <String> list1 = CsvHandler.getChunkListFile();
			List <String> list2 = CsvHandler.getMyChunksFile();
			for(int i =0;i<list1.size();i++){
				String[] dividedName= list1.get(i).substring(0,list1.get(i).length()).split(Constants.COMMA_DELIMITER);
				if(CsvHandler.getInitiatorPeer(dividedName[0],Integer.parseInt(dividedName[1]))==null)
					CsvHandler.addMasterMeta(new Chunk(dividedName[0],Integer.parseInt(dividedName[1]),null,0),dividedName[3] , dividedName[2]);
				CsvHandler.addMasterMeta(new Chunk(dividedName[0],Integer.parseInt(dividedName[1]),null,0),Peer.getPeerId()+"", dividedName[2]);
			}
			for(int i =0;i<list2.size();i++){
				String[] dividedName =list2.get(i).split(Constants.COMMA_DELIMITER);
				if(CsvHandler.getInitiatorPeer(dividedName[0],Integer.parseInt(dividedName[1]))==null){
					CsvHandler.addMasterMeta(new Chunk(dividedName[0],Integer.parseInt(dividedName[1]),null,0), Peer.getPeerId()+"", dividedName[2]);
				}
				for(int j=3;j<dividedName.length;j++){
					CsvHandler.addMasterMeta(new Chunk(dividedName[0],Integer.parseInt(dividedName[1]),null,0), dividedName[j], dividedName[2]);
				}
			}
		}
		
		System.out.println("encontrei o master");		
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
		
		password=args[1]; //mudar pra password
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
		
		f = new File("../metadata"+peerId+"/PeerList.csv");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		f = new File("../metadata"+peerId+"/ChunkList.csv");
		if(!f.exists()){
			try {
				f.createNewFile();									
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		f = new File("../metadata"+peerId+"/MyChunks.csv");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CsvHandler.updateMemory(0);
			diskSpace=0;
		}else diskSpace=CsvHandler.getMemory();
	}

	@Override
	public void backup(File file, int replDeg) throws RemoteException {
		if(!amIMaster()){
			Runnable run=new ReadFile(file,replDeg);
			new Thread(run).start();
		}
	}

	@Override
	public void restore(String filename) throws RemoteException{
		if(!amIMaster()){
			Runnable run=new ChunkRestore(filename);
			new Thread(run).start();
		}
	}

	@Override
	public void delete(String filename) throws RemoteException{
		if(!amIMaster()){
			Runnable run=new FileDeletion(filename);
			new Thread(run).start();
		}
	}

	@Override
	public void reclaim( int space) throws RemoteException{
		diskSpace=space*1000;
		CsvHandler.updateMemory(space);
		if(!amIMaster()){
			Runnable run=new SpaceReclaiming();
			new Thread(run).start();
		}
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

	
	public static Boolean amIMaster(){
		return imMaster;
	}

	@Override
	public void createUser(String name, String password, String level) throws RemoteException {
		User user = new User(name, password, level);
		CsvHandler.createUser(user);
		Message message = new Message(user);
		((SSL_Client)clientThread).sendStart(message.createPeerUser());
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
	
	public static String getPassword(){
		return password;
	}

	@Override
	public String LoginUser(String name, String password) throws RemoteException {
		if(password.equals(CsvHandler.getUserPassword(name))){
			return CsvHandler.getUserLevel(name);
		}else return null;
		
	}
	
}
