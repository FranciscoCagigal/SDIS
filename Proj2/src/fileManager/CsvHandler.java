package fileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import peer.Peer;
import protocols.Constants;
import user.User;

public class CsvHandler {
	
	public synchronized static List<String> getUsers(){
		File userData = new File("../metadata"+Peer.getPeerId()+"/Users.csv");
		Scanner scanner;
		List<String> listOfNames = new ArrayList<String>();
		if(userData.exists()){
			try {
				scanner = new Scanner(userData);
				scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				while(scanner.hasNext()){
					String str=scanner.next();
					listOfNames.add(str);
				}				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listOfNames;
	}
	
	public synchronized static void createPeer(String id,String pass){
		File userData = new File("../metadata"+Peer.getPeerId()+"/PeerList.csv");
		
		try {
			FileWriter fileWriter = new FileWriter(userData, true);
			fileWriter.append(id);
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(pass);
			fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			fileWriter.close();
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void createUser(User user){
		File userData = new File("../metadata"+Peer.getPeerId()+"/Users.csv");
		System.out.println(Peer.getPeerId());
		try {
			FileWriter fileWriter = new FileWriter(userData, true);
			fileWriter.append(user.getUsername());
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(user.getPassword());
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(user.getPriorityLevel().toString().toLowerCase());
			fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			fileWriter.close();
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static boolean checkPeer(String id){
		File userData = new File("../metadata"+Peer.getPeerId()+"/PeerList.csv");
		Scanner scanner;
		
		if(userData.exists()){
			try {
				scanner = new Scanner(userData);
				scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				while(scanner.hasNext()){
					String str=scanner.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);
					if (divided[0].equals(id)) {
						scanner.close();
						return true;
					}
				}
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public synchronized static String getPeerPassword(String id){
		File userData = new File("../metadata"+Peer.getPeerId()+"/PeerList.csv");
		Scanner scanner;
		
		if(userData.exists()){
			try {
				scanner = new Scanner(userData);
				scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				while(scanner.hasNext()){
					String str=scanner.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);
					if (divided[0].equals(id)) {
						String password = divided[1];
						scanner.close();
						return password;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public synchronized static boolean checkUser(String name){
		File userData = new File("../metadata"+Peer.getPeerId()+"/Users.csv");
		Scanner scanner;
		
		if(userData.exists()){
			try {
				scanner = new Scanner(userData);
				scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				while(scanner.hasNext()){
					String str=scanner.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);
					if (divided[0].equals(name)) {
						scanner.close();
						return true;
					}
				}
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public synchronized static String getUserPassword(String name){
		File userData = new File("../metadata"+Peer.getPeerId()+"/Users.csv");
		Scanner scanner;
		
		if(userData.exists()){
			try {
				scanner = new Scanner(userData);
				scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				while(scanner.hasNext()){
					String str=scanner.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);
					if (divided[0].equals(name)) {
						String password = divided[1];
						scanner.close();
						return password;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public synchronized static String getUserLevel(String name){
		File userData = new File("../metadata"+Peer.getPeerId()+"/Users.csv");
		Scanner scanner;
		
		if(userData.exists()){
			try {
				scanner = new Scanner(userData);
				scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				while(scanner.hasNext()){
					String str=scanner.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);
					if (divided[0].equals(name)) {
						String level = divided[2];
						scanner.close();
						return level;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public synchronized static void replacePeer(Chunk chunk,String oldPeer,String newPeer){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				
				if(divided[0].equals(chunk.getFileId()) &&  Integer.parseInt(divided[1])==chunk.getChunkNumber()){
					int firstIndex = str.indexOf(oldPeer,divided[0].length()+divided[1].length()+divided[2].length()+divided[3].length()+4);
					//int secondIndex = str.indexOf(oldPeer, firstIndex);
					String begin = str.substring(0,firstIndex);
					String end = str.substring(firstIndex+oldPeer.length());
					System.out.println(begin);
					System.out.println(newPeer);
					System.out.println(end);
					System.out.println("funcao louca do andre " + begin+newPeer+end);
					metaArray.add(begin+newPeer+end);
				}else metaArray.add(str);
	        }
			
			FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
			for(String str: metaArray){
				fileWriter.append(str);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
				
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void replacePeerInitiator(Chunk chunk,String oldPeer,String newPeer){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				
				if(divided[0].equals(chunk.getFileId()) &&  Integer.parseInt(divided[1])==chunk.getChunkNumber()){
					int firstIndex = str.indexOf(oldPeer,divided[0].length()+divided[1].length()+divided[2].length()+3);
					String begin = str.substring(0,firstIndex);
					String end = str.substring(firstIndex+oldPeer.length());
					metaArray.add(begin+newPeer+end);
				}else metaArray.add(str);
	        }
			
			FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
			for(String str: metaArray){
				fileWriter.append(str);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
				
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void deleteChunks(String fileId,String path){
		File metaData = new File(path);
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&!divided[0].equals(fileId) || divided.length==1){
					metaArray.add(str);
				}
	        }
			
			FileWriter fileWriter = new FileWriter(path);
			for(String str: metaArray){
				fileWriter.append(str);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
				
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void addMyChunkMeta(Chunk chunk, String listOfPeers, String name){
		
		File metaData = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		Scanner scanner;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			boolean write=true;
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 && divided[0].equals(chunk.getFileId()) && Integer.parseInt(divided[1])==chunk.getChunkNumber()){
					write=false;
				}
	        }
			if(write){
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/MyChunks.csv", true);
				
				fileWriter.append(chunk.getFileId());
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getChunkNumber()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(name);
				String[] divided = listOfPeers.split(" ");
				for(int i=0;i<divided.length;i++){
					fileWriter.append(Constants.COMMA_DELIMITER);
					fileWriter.append(divided[i]);
				}
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
					
				fileWriter.close();
			}				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public synchronized static void addChunkMeta(Chunk chunk, String peerId, String name){
		
	File metaData = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
	Scanner scanner;
	boolean write=true;
	
	try {
		scanner = new Scanner(metaData);
		scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
		while(scanner.hasNext()){
			String str=scanner.next();
			String[] divided = str.split(Constants.COMMA_DELIMITER);
			if(divided.length>1 && divided[0].equals(chunk.getFileId()) && Integer.parseInt(divided[1])==chunk.getChunkNumber()){
				write=false;
			}
        }
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if(write){
		try {
			
			FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/ChunkList.csv", true);
				
			fileWriter.append(chunk.getFileId());
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(Integer.toString(chunk.getChunkNumber()));
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(name);
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(peerId);
			fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				
			fileWriter.close();
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}

	public synchronized static List<String> getChunksByPeers(String peerId){
	
	File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
	Scanner scanner;
	
	List<String> result = new ArrayList<String>();
	
	try {
		scanner = new Scanner(metaData);
		scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
		while(scanner.hasNext()){
			String str=scanner.next();
			String[] divided = str.split(Constants.COMMA_DELIMITER);
			if(divided.length>1 && divided[3].equals(peerId)){
				result.add(str);
			}
        }
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return result;
}

public synchronized static List<String> getPeersChunk(String id){
	
	File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
	Scanner scanner;
	
	List<String> result = new ArrayList<String>();
	
	try {
		scanner = new Scanner(metaData);
		scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
		while(scanner.hasNext()){
			String str=scanner.next();
			String[] divided = str.split(Constants.COMMA_DELIMITER);
			if(divided.length>1 && divided[0].equals(id)){
				for(int i=3;i<divided.length;i++){
					result.add(divided[i]);
				}
			}
        }
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return result;
}

public synchronized static String getInitiatorPeer(String id,int chunkNumber){
	
	File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
	Scanner scanner;

	try {
		scanner = new Scanner(metaData);
		scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
		while(scanner.hasNext()){
			String str=scanner.next();
			String[] divided = str.split(Constants.COMMA_DELIMITER);
			if(divided.length>1 && divided[0].equals(id) && Integer.parseInt(divided[1])==chunkNumber){
				scanner.close();
				return divided[3]+";"+divided[2];
			}
        }
		scanner.close();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

public synchronized static List<String> getPeersChunk(String id,int chunkNumber){
	
	File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
	Scanner scanner;
	
	List<String> result = new ArrayList<String>();
	
	try {
		scanner = new Scanner(metaData);
		scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
		while(scanner.hasNext()){
			String str=scanner.next();
			String[] divided = str.split(Constants.COMMA_DELIMITER);
			if(divided.length>1 && divided[0].equals(id) && Integer.parseInt(divided[1])==chunkNumber){
				for(int i=4;i<divided.length;i++){
					result.add(divided[i]);
				}
			}
        }
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return result;
}
	
	public synchronized static void addMasterMeta(Chunk chunk, String peerId, String name){
		
		File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		Boolean iHaveChunk = false;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 && Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0].equals(chunk.getFileId())){
					iHaveChunk=true;
					metaArray.add(str+Constants.COMMA_DELIMITER+peerId);
				}else metaArray.add(str);
	        }
			
			if(iHaveChunk){
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/AllChunks.csv", false);
				for(String str: metaArray){
					fileWriter.append(str);
					fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				}
				
				fileWriter.close();
			}else{
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/AllChunks.csv", true);
				
				fileWriter.append(chunk.getFileId());
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getChunkNumber()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(name);
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(peerId);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				
				fileWriter.close();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static int updateNegative(Chunk chunk, String path){
		File metaData = new File(path);
		Scanner scanner;		
		List<String> metaArray=new ArrayList<String>();	
		int replicationDegree=-1;
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);

			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0].equals(chunk.getFileId())){				
					if(divided.length==5)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+(Integer.parseInt(divided[3])-1)+Constants.COMMA_DELIMITER+divided[4]+Constants.COMMA_DELIMITER);
					else if(divided.length==4){
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+(Integer.parseInt(divided[3])-1)+Constants.COMMA_DELIMITER);
					}
					if(Integer.parseInt(divided[3])-1<Integer.parseInt(divided[2])){
						replicationDegree=Integer.parseInt(divided[2]);
					}
				}else metaArray.add(str);
	        }
			
			FileWriter fileWriter = new FileWriter(path, false);
			for(String str: metaArray){
				fileWriter.append(str);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
				
			fileWriter.close();				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return replicationDegree;
	}
	
	public synchronized static String getHash(String name){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
		Scanner scanner;
		try {
			scanner = new Scanner(metaData);	
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&divided[2].equals(name)){
					scanner.close();
					return divided[0];
				}
	        }
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static String getRealName(String name){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/AllChunks.csv");
		Scanner scanner;
		try {
			scanner = new Scanner(metaData);	
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&divided[0].equals(name)){
					scanner.close();
					return divided[2];
				}
	        }
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static int repliMyChunk(Chunk chunk,String path){
		File metaData = new File(path);
		Scanner scanner;
		try {
			scanner = new Scanner(metaData);
			
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0].equals(chunk.getFileId())){
					scanner.close();
					return Integer.parseInt(divided[3]);
				}
	        }
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public synchronized static int numberOfChunks(String fileId){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		Scanner scanner;
		int counter=0;
		try {
			scanner = new Scanner(metaData);
			
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&divided[0].equals(fileId)){
					counter++;
				}
	        }
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return counter;
	}
	
	public synchronized static boolean isMyChunk(Chunk chunk,String path){
		File metaData = new File(path);
		Scanner scanner;
		Boolean iHaveChunk = false;
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 &&Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0].equals(chunk.getFileId())){
					iHaveChunk=true;
				}
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return iHaveChunk;
	}
	
	public synchronized static void updateMyChunks(Chunk chunk,String name,int update){
		
		File metaData = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		Boolean iHaveChunk = false;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(divided.length>1 && Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0].equals(chunk.getFileId())){
					iHaveChunk=true;
					if(update==1)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+(Integer.parseInt(divided[3])+1)+Constants.COMMA_DELIMITER+divided[4]+Constants.COMMA_DELIMITER);
					else if(update==0)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+0+Constants.COMMA_DELIMITER+divided[4]+Constants.COMMA_DELIMITER);
				}else metaArray.add(str);
	        }
			
			if(iHaveChunk){
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/MyChunks.csv", false);
				for(String str: metaArray){
					fileWriter.append(str);
					fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				}
				
				fileWriter.close();
			}else{
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/MyChunks.csv", true);
				
				fileWriter.append(chunk.getFileId());
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getChunkNumber()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getReplication()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(0));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(name);
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				
				fileWriter.close();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized static Chunk eliminateBadChunk(){
		
		File metaData = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		Scanner scanner;
		List<String> metaArray=new ArrayList<String>();
		Boolean iHaveChunk = false;		
		Chunk chunk=null;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(!iHaveChunk){
					iHaveChunk=true;
					chunk=new Chunk(divided[0],Integer.parseInt(divided[1]),HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+divided[0]+"."+divided[1]),0);
					HandleFiles.eraseFile("../Chunks"+Peer.getPeerId()+"/"+divided[0]+"."+divided[1]);
				}else metaArray.add(str);
	        }
			
			if(iHaveChunk){
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/ChunkList.csv", false);
				for(String str: metaArray){
					fileWriter.append(str);
					fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				}
				
				fileWriter.close();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chunk;
	}
	
	public synchronized static Chunk eliminateGoodChunk(){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		Boolean iHaveChunk = false;
		
		Chunk chunk=null;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				System.out.println(Integer.parseInt(divided[2]));
				System.out.println(Integer.parseInt(divided[3]));
				if(Integer.parseInt(divided[2])<Integer.parseInt(divided[3]) && !iHaveChunk){
					iHaveChunk=true;
					System.out.println("vou apagar");
					chunk=new Chunk(divided[0],Integer.parseInt(divided[1]),null,0);
					HandleFiles.eraseFile("../Chunks"+Peer.getPeerId()+"/"+divided[0]+"."+divided[1]);
				}else metaArray.add(str);
	        }
			
			if(iHaveChunk){
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/ChunkList.csv", false);
				for(String str: metaArray){
					fileWriter.append(str);
					fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				}
				
				fileWriter.close();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chunk;
	}
	
	public synchronized static int getMemory(){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		Scanner scanner;
		int memory=0;
		try {
			scanner = new Scanner(metaData);
			if(scanner.hasNext()){
				String str=scanner.next();
				memory=Integer.parseInt(str);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memory;
	}
	
	public synchronized static void updateMemory(int memory){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		Scanner scanner;		
		List<String> metaArray=new ArrayList<String>();
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			String str;
			if(scanner.hasNext()){
				str=scanner.next();
			}
			metaArray.add(String.valueOf(memory));
			while(scanner.hasNext()){
				str=scanner.next();
				metaArray.add(str);
	        }

			FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/MyChunks.csv", false);
			for(String str1: metaArray){
				fileWriter.append(str1);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized static void updateChunkRepl(Chunk chunk,int update,int count){
		
		File metaData = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		Boolean iHaveChunk = false;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0].equals(chunk.getFileId())){
					iHaveChunk=true;
					if(update==1)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+(Integer.parseInt(divided[3])+1)+Constants.COMMA_DELIMITER);
					else if(update==0)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+1+Constants.COMMA_DELIMITER);
					else if(update==2){
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+count+Constants.COMMA_DELIMITER);
					}
				}else metaArray.add(str);
	        }
			
			if(iHaveChunk){
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/ChunkList.csv", false);
				for(String str: metaArray){
					fileWriter.append(str);
					fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				}
				
				fileWriter.close();
			}else{
				FileWriter fileWriter = new FileWriter("../metadata"+Peer.getPeerId()+"/ChunkList.csv", true);
				
				fileWriter.append(chunk.getFileId());
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getChunkNumber()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getReplication()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(String.valueOf(count));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				
				fileWriter.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public synchronized static List<Chunk> getBadChunks(){
		File metaData = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		Scanner scanner;
		List<Chunk> badchunks= new ArrayList<Chunk>();
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(Integer.parseInt(divided[3])<Integer.parseInt(divided[2])){
					badchunks.add(new Chunk(divided[0],Integer.parseInt(divided[1]),HandleFiles.readFile("../Chunks"+Peer.getPeerId()+"/"+divided[0]+"."+divided[1]),Integer.parseInt(divided[2])));
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return badchunks;
	}
}
