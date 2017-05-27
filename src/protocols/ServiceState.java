package protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import fileManager.CsvHandler;
import peer.Peer;

public class ServiceState {

	public static synchronized String getServiceState() {
		
		File metaDataInit = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		File metaDataStored = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		
		String result = "/";
		result+=" -- SERVICE STATE PEER "+Peer.getPeerId()+" -- ";
		result+="/";
		result+="/This Peer has initiated the backup of the following files:";
		result+="/";
		
		int counter = 0;
		String nextChunk;
		String lastChunk = null;
		Scanner scannerInit;
		try {
			scannerInit = new Scanner(metaDataInit);
			scannerInit.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			if (!scannerInit.hasNext()) {
				result+="/No backups initiated";
			}
			while(scannerInit.hasNext()){
				String str=scannerInit.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				nextChunk = divided[0];
				if (!nextChunk.equals(lastChunk)){
					if (counter > 0) {
						result+="/";
						result+="/  ----------  ";
					}
					counter++;
					result+="/File "+counter+":";
					result+="/Name:" + divided[4];
					result+="/Backup-ID: "+divided[0];
					result+="/Desired Replication Degree: "+divided[2];
					result+="/With the chunks:";
					result+="/"+divided[0]+"."+divided[1];
					result+="/Percieved Replication Degree: "+ divided[3];
		
				}
				else if (nextChunk.equals(lastChunk)) {
					result+="/"+divided[0]+"."+divided[1];
					result+="/Percieved Replication Degree: "+ divided[3];
				}
				lastChunk = divided[0];	
			}
			result+="/";
			result+="/  ----------  ";
			result+="/";
			scannerInit.close();
			
			result+="/This Peer has stored the following chunks:";
			result+="/";
			
			Scanner scannerChunks;
				scannerChunks = new Scanner(metaDataStored);
				scannerChunks.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				if (!scannerChunks.hasNext()) {
					System.out.println();
					result+="/No chunks stored";
				}
				while(scannerChunks.hasNext()){
					String str=scannerChunks.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);

					result+="/"+divided[0]+"."+divided[1];
					String fileName = divided[0]+"."+divided[1];
					File temp = new File("../chunks"+Peer.getPeerId()+"/"+fileName);
					int tempSize = ((int) temp.length())/1000;
					result+="/File size: "+tempSize;
					result+="/Percieved Replication Degree: "+divided[3];
					result+="/";
					
				}
				result+="/  ---------  ";
				result+="/";
				scannerChunks.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result+="/";
		if (CsvHandler.getMemory() == 0){
			result+="/This Peer's capacity to store chunks is the total available disk space.";
		}
		else {
			result+="/This Peer has "+CsvHandler.getMemory()+" KBytes of capacity to store chunks.";
		}
		result+="/This Peer uses "+ ((int) SpaceReclaiming.directorySize())/1000+" KBytes to store chunks.";
		result+="/";
		result+="/  ---------  ";
		result+="/";
		result+="/ -- END OF SERVICE STATE PEER "+Peer.getPeerId()+" -- ";
		result+="/";
		
		
		return result;
	}
}
