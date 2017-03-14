package l04;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	
	String request(String request)  throws RemoteException; 
}
