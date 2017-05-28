package peer;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

import user.User;

public interface IPeer extends Remote{
	
	public void backup(File file,int replDeg) throws RemoteException;
	public void restore(String filename) throws RemoteException;
	public void delete(String filename) throws RemoteException;
	public void reclaim(int space) throws RemoteException;
	public String state() throws RemoteException;
	public void createUser(String nome, String password, String level) throws RemoteException;
	public String LoginUser(String nome, String password) throws RemoteException;
}
