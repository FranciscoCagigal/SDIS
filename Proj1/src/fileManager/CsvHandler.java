package fileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import peer.Peer;
import protocols.Constants;

public class CsvHandler {
	
	/*
	public static void cont(Chunk chunk){
		File metaData = new File("../Chunks"+Peer.getPeerId()+"/ChunkList.csv");
		Scanner scanner;
		List<String> metaArray=new ArrayList<String>();
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0]==chunk.getFileId()){				
					metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+0+Constants.COMMA_DELIMITER);
				}
	        }
			
			FileWriter fileWriter = new FileWriter("Chunks2/ChunkList.csv", false);
			for(String str: metaArray){
				fileWriter.append(str);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
			
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	//update =0 for reset  || update = 1 for adding 1
	public static void updateChunkRepl(Chunk chunk,int update){
		File metaData = new File("../Chunks"+Peer.getPeerId()+"/ChunkList.csv");
		Scanner scanner;
		
		List<String> metaArray=new ArrayList<String>();
		
		Boolean iHaveChunk = false;
		
		try {
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(Integer.parseInt(divided[1])==chunk.getChunkNumber() && divided[0]==chunk.getFileId()){
					iHaveChunk=true;
					if(update==1)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+(Integer.parseInt(divided[3])+1)+Constants.COMMA_DELIMITER);
					else if(update==0)
						metaArray.add(divided[0]+Constants.COMMA_DELIMITER+divided[1]+Constants.COMMA_DELIMITER+divided[2]+Constants.COMMA_DELIMITER+1+Constants.COMMA_DELIMITER);
				}
	        }
			
			if(iHaveChunk){
				FileWriter fileWriter = new FileWriter("../Chunks"+Peer.getPeerId()+"/ChunkList.csv", false);
				for(String str: metaArray){
					fileWriter.append(str);
					fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				}
				
				fileWriter.close();
			}else{
				FileWriter fileWriter = new FileWriter("../Chunks"+Peer.getPeerId()+"/ChunkList.csv", true);
				
				fileWriter.append(chunk.getFileId());
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getChunkNumber()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(chunk.getReplication()));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Integer.toString(1));
				fileWriter.append(Constants.COMMA_DELIMITER);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
				
				fileWriter.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*public static void addChunk(Chunk chunk) {
		
		FileWriter fileWriter = null;
		
		
		try {
			fileWriter = new FileWriter("../Chunks"+Peer.getPeerId()+"/ChunkList.csv", true);
			
			fileWriter.append(chunk.getFileId());
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(Integer.toString(chunk.getChunkNumber()));
			fileWriter.append(Constants.COMMA_DELIMITER);
			fileWriter.append(Integer.toString(chunk.getReplication()));
			fileWriter.append(Constants.COMMA_DELIMITER);
			//fileWriter.append(Integer.toString(Peer.getNumberOfPeers(chunk)));
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
	}*/
}
