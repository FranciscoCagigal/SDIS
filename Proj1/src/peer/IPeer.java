package peer;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPeer extends Remote{
	
	public void backup(File file,int replDeg) throws RemoteException;
	public void restore(File file) throws RemoteException;
	public void delete(File file) throws RemoteException;
	public void reclaim(int space) throws RemoteException;
	public void state() throws RemoteException; //é muito provavel q este nao seja void

}
