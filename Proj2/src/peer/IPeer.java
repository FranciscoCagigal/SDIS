package peer;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

import user.User;

public interface IPeer extends Remote{
	
	public void backup(User user,File file,int replDeg) throws RemoteException;
	public void restore(User user,String filename) throws RemoteException;
	public void delete(User user,String filename) throws RemoteException;
	public void reclaim(User user, int space) throws RemoteException;
	public String state() throws RemoteException;
	public void createUser(String nome, String password, String level) throws RemoteException;
}
