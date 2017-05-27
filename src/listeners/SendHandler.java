package listeners;

import java.net.InetAddress;

public class SendHandler implements Runnable{

	private String sourcePeer;
	private String protocol;
	private InetAddress address;
	private int port;
	
	public SendHandler(String sourcePeer, String protocol, InetAddress address, int port) {
		super();
		this.sourcePeer = sourcePeer;
		this.protocol = protocol;
		this.address = address;
		this.port = port;
	}

	

	@Override
	public void run() {
		
		
		
	}

	
}
