package fileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import protocols.Constants;

public class HandleFiles {
		
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
