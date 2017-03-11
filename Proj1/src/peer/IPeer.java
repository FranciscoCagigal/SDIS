package peer;

import java.io.File;
import java.rmi.Remote;

public interface IPeer extends Remote{
	
	public void backup(File file,int replDeg);
	public void restore(File file);
	public void delete(File file);
	public void reclaim(int space);
	public void state(); //é muito provavel q este nao seja void

}
