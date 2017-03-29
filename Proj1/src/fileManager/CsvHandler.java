package fileManager;

import java.io.FileWriter;
import java.io.IOException;

import peer.Peer;
import protocols.Constants;

public class CsvHandler {

	public static void addChunk(Chunk chunk) {
		
		FileWriter fileWriter = null;
		
		
		try {
			fileWriter = new FileWriter("../Chunks"+Peer.getPeerId()+"/ChunkList.csv", true);
			
			fileWriter.append(chunk.getFileId());
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(Integer.toString(chunk.getChunkNumber()));
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(Integer.toString(chunk.getReplication()));
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(Integer.toString(chunk.getReplication()));
			fileWriter.append(Integer.toString(Peer.getNumberOfPeers(chunk)));
			fileWriter.append(Constants.NEW_LINE_SEPARATOR);

		} 
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			
			try {
				fileWriter.flush();
				fileWriter.close();
			}
			
			catch (IOException e){
				e.printStackTrace();
			}
		}	
	}
}
