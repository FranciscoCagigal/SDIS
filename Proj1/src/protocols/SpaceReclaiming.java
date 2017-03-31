package protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import peer.Peer;

public class SpaceReclaiming implements Runnable{
	
	public SpaceReclaiming(){}

	@Override
	public void run() {
		long spaceUsed = directorySize();
		if(!Peer.iHaveSpace(spaceUsed)){
			
		}
	}
	
	private long directorySize(){
		long length = 0;
		File directory = new File("../Chunks"+Peer.getPeerId());
		for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	    }
		return length;
	}
	
	private void readCsv(){
		File metaData = new File("../Chunks"+Peer.getPeerId()+"/ChunkList.csv");
		
		try {
			Scanner scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
	            System.out.print(scanner.next()+"|");
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}
