package listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;

public class SSL_Handler implements Runnable {

	private SSLSocket socket;
	
	
	public SSL_Handler(SSLSocket socket) {
		
		this.socket = socket;
	}


	@Override
	public void run() {
		
		PrintWriter out=null;
		
		BufferedReader in=null;
		
		String received="";
		
		try {
		
			out = new PrintWriter(socket.getOutputStream(), true);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
			received = in.readLine();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
				
		String[] divided = received.split(" ");
		
		//TODO verificar autenticacao do peer
		
		String type = divided[0];
		
		String id = divided[1];
		
		String pass = divided[2];
		
		String protocol = divided[3];
		
		String filename ="";
		
		String repDegree = "";
		
		String spaceReclaim = "";
		
		String username = "";
		
		String newPassword = "";
		
		String level = "";
		
		byte[] bytesBody;
				
		switch(protocol){
		
		case "BACKUP":
			
			filename = divided[4];
			repDegree = divided[5];
			
			ReadBytesHandler rbh = null;
			
			try {
				rbh = new ReadBytesHandler(in);
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
						
			bytesBody = rbh.getBody().getBytes();
			
			SendHandler sh = new SendHandler(id, protocol);
			
			sh.run();
			
			break;
			
		case "RESTORE":
			
			filename = divided[4];
			
			break;
			
		case "RECLAIM":
			
			spaceReclaim = divided[4];
			
			break;
			
		case "DELETE":
			
			filename = divided[4];
			
			break;
			
		case "CREATE":
			
			username = divided[4];
			newPassword = divided[5];
			level = divided[6];
			
			break;
			
		}

		out.close();
		try {
			socket.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
