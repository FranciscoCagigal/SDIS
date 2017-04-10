package protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import fileManager.CsvHandler;
import peer.Peer;

public class ServiceState {

	public static synchronized void getServiceState() {
		
		File metaDataInit = new File("../metadata"+Peer.getPeerId()+"/MyChunks.csv");
		File metaDataStored = new File("../metadata"+Peer.getPeerId()+"/ChunkList.csv");
		
		System.out.println();
		System.out.println(" -- SERVICE STATE PEER "+Peer.getPeerId()+" -- ");
		System.out.println();
		System.out.println("This Peer has initiated the backup of the following files:");
		
		int counter = 0;
		String nextChunk;
		String lastChunk = null;
		Scanner scannerInit;
		try {
			scannerInit = new Scanner(metaDataInit);
			scannerInit.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			if (!scannerInit.hasNext()) {
				System.out.println();
				System.out.println("No backups initiated");
				System.out.println();
			}
			while(scannerInit.hasNext()){
				String str=scannerInit.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				nextChunk = divided[0];
				if (!nextChunk.equals(lastChunk)){
					if (counter > 0) {
						System.out.println("  ----------  ");
					}
					counter++;
					System.out.println();
					System.out.println("File "+counter+":");
					System.out.println("Name:" + divided[4]);
					System.out.println("Backup-ID: "+divided[0]);
					System.out.println("Desired Replication Degree: "+divided[2]);
					System.out.println();
					System.out.println("With the chunks:");
					System.out.println();
					System.out.println(divided[0]+"."+divided[1]);
					System.out.println("Percieved Replication Degree: "+ divided[3]);
					System.out.println();
		
				}
				else if (nextChunk.equals(lastChunk)) {
					System.out.println(divided[0]+"."+divided[1]);
					System.out.println("Percieved Replication Degree: "+ divided[3]);
					System.out.println();
				}
				lastChunk = divided[0];	
			}
			System.out.println("  ---------  ");
			System.out.println();
			scannerInit.close();
			
			System.out.println("This Peer has stored the following chunks:");
			
			Scanner scannerChunks;
				scannerChunks = new Scanner(metaDataStored);
				scannerChunks.useDelimiter(Constants.NEW_LINE_SEPARATOR);
				if (!scannerChunks.hasNext()) {
					System.out.println();
					System.out.println("No chunks stored");
					System.out.println();
				}
				while(scannerChunks.hasNext()){
					String str=scannerChunks.next();
					String[] divided = str.split(Constants.COMMA_DELIMITER);
					System.out.println();
					System.out.println(divided[0]+"."+divided[1]);
					String fileName = divided[0]+"."+divided[1];
					File temp = new File("../chunks"+Peer.getPeerId()+"/"+fileName);
					int tempSize = ((int) temp.length())/1000;
					System.out.println("File size: "+tempSize);
					System.out.println("Percieved Replication Degree: "+divided[3]);
					System.out.println();
					
				}
				System.out.println("  ---------  ");
				System.out.println();
				scannerChunks.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (CsvHandler.getMemory() == 0){
			System.out.println("This Peer's capacity to store chunks is the total available disk space.");
		}
		else {
			System.out.println("This Peer has "+CsvHandler.getMemory()+" KBytes of capacity to store chunks.");
		}
		System.out.println("This Peer uses "+ ((int) SpaceReclaiming.directorySize())/1000+" KBytes of capacity to store chunks.");
		System.out.println();
		System.out.println("  ---------  ");
		System.out.println();
		
		System.out.println(" -- END OF SERVICE STATE PEER "+Peer.getPeerId()+" -- ");
	}
}
