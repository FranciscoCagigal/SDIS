package listeners;


import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import fileManager.*;

import peer.Peer;
import protocols.Constants;
import protocols.Message;

public class Handler implements Runnable{

	private DatagramPacket packet;
	private Chunk chunk;
	private static Long masterTime = Long.MAX_VALUE;
	private static boolean electionStarted=false;
	
	public static boolean isElectionStarted() {
		return electionStarted;
	}

	public static void setElectionStarted(boolean value) {
		electionStarted = value;
	}

	public Handler(DatagramPacket packet1){
		packet=packet1;
	}
	
	@Override
	public void run() {
		
		String header[] = getHeader();
		
		System.out.println(Arrays.toString(header));

		switch(header[0].toUpperCase()){
			case Constants.COMMAND_FINDMASTER:
				findMaster(header);
				break;
			case Constants.COMMAND_IMMASTER:
				foundMaster(header);
				break;		
			case Constants.COMMAND_BEGINELECTION:
				election(header);
				break;
			case Constants.COMMAND_DISPUTEMASTER:
				electionCandidate(header);
				break;
			default:
		}
				
	}
	
	private void electionCandidate(String[] header){
		if(Long.parseLong(header[2])<Peer.getTime() && Long.parseLong(header[2])<masterTime){
			Peer.setMasterAddress(packet.getAddress());
			Peer.setMasterPort(packet.getPort());
			masterTime=Long.parseLong(header[2]);
			Peer.setImMaster(false);
		}else if(masterTime>Peer.getTime() && Long.parseLong(header[2])==Peer.getTime()){
			Peer.setImMaster(true);
		}
	}
	
	private void election(String[] header){
		electionStarted=true;
		masterTime = Long.MAX_VALUE;
		
		Random rnd = new Random();
		try {
			Thread.sleep(rnd.nextInt(1000));
			if(masterTime>Peer.getTime()){
				Message message = new Message(null,Peer.getVersion());
				sendToMc(message.disputeMaster());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void foundMaster(String[] header){
		Peer.setMasterAddress(packet.getAddress());
		Peer.setMasterPort(Integer.parseInt(header[2]));
	}
	
	private void findMaster(String header[]){
		if(Peer.amIMaster()){
			Message message = new Message(null,Peer.getVersion());
			try {
				sendToMc(message.acknowledgeMaster());
			} catch (IOException e) {
				System.out.println("erro ao enviar IMMASTER");
				e.printStackTrace();
			}
		}
	}
	
	public static void sendToMc(byte[] buffer) throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Peer.getMcAddress(),Peer.getMcPort());
		MulticastSocket socket = new MulticastSocket();
		socket.send(packet);
		socket.close();
	}
	
	private String[] getHeader(){
		String str = new String(packet.getData(), StandardCharsets.UTF_8);	
		return str.substring(0, str.indexOf(Constants.CRLF)).split(" ");
	}
	
	public static byte[] trim(byte[] bytes)
	{
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
	}
	
	public static char[] trim(char[] bytes)
	{
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
	}
	
	private byte[] getBody(){
		String packetStr = new String(packet.getData(), StandardCharsets.UTF_8);
		return Arrays.copyOfRange(trim(packet.getData()), 4+packetStr.substring(0, packetStr.indexOf(Constants.CRLF)).length(), trim(packet.getData()).length);	
	}
	
}
