import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import protocols.Constants;

public class Test {

	public static void main(String[] args){

		File metaData = new File("Chunks2/ChunkList.csv");
		List<String> yolo=new ArrayList<String>();
		Scanner scanner;
		try {
			
			scanner = new Scanner(metaData);
			scanner.useDelimiter(Constants.NEW_LINE_SEPARATOR);
			while(scanner.hasNext()){
				String str=scanner.next();
				String[] divided = str.split(Constants.COMMA_DELIMITER);
				if(Integer.parseInt(divided[1])!=1){
					System.out.println(divided[1]);
					yolo.add(str);
				}
	        }
			
			FileWriter fileWriter = new FileWriter("Chunks2/ChunkList.csv", false);
			for(String str: yolo){
				fileWriter.append(str);
				fileWriter.append(Constants.NEW_LINE_SEPARATOR);
			}
			
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
