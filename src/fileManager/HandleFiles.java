package fileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import peer.Peer;
import protocols.Constants;

public class HandleFiles {
		
	public static List<String> getChunks(){
	    File folderToScan = new File("../Chunks"+Peer.getPeerId()+"/"); 
	    List<String> list =new ArrayList<String>();
	    File[] listOfFiles = folderToScan.listFiles();

	     for (int i = 0; i < listOfFiles.length; i++) {
	            if (listOfFiles[i].isFile()) {
	                list.add(listOfFiles[i].getName());
	           }
	     } 
	     return list;
	}
	
	public static void eraseFile(String path, String regex){
	    String target_file ;
	    File folderToScan = new File(path); 


	    File[] listOfFiles = folderToScan.listFiles();

	     for (int i = 0; i < listOfFiles.length; i++) {
	            if (listOfFiles[i].isFile()) {
	            	System.out.println(listOfFiles[i].getName());
	                target_file = listOfFiles[i].getName();
	                if (target_file.contains(regex)) {
	                	listOfFiles[i].delete();
	                }
	           }
	     } 
		
		File file = new File(path);
		if(file.exists() && !file.isDirectory()){
			file.delete();
		}
	}
	
	public static boolean fileExists(String path){
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
			return true;
		}
		return false;
	}
	
	public static void eraseFile(String path){
		File file = new File(path);
		if(file.exists() && !file.isDirectory()){
			file.delete();
		}
	}
	
	public static void writeFile(String path, byte[] data){
		File file = new File(path);
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] readFile(String path){
		byte[] buffer = new byte[Constants.CHUNKSIZE];
		File file = new File(path);
		try {
			if(file.exists() && !file.isDirectory()){
				FileInputStream in= new FileInputStream(file);
				in.read(buffer);
				in.close();
				return buffer;
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
