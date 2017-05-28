package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.regex.Pattern;

import fileManager.CsvHandler;
import peer.IPeer;
import peer.Peer;
import user.User;

public class TestApp {
	
	private static IPeer peer;
	static Scanner in = new Scanner(System.in);
	static BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	private static final Pattern LEVEL_PATTERN = Pattern.compile("((l|L)(o|O)(w|W))|((h|H)(i|I)(g|G)(h|H))");
	
	private static String fileName;
	private static int replicationDegree;
	private static int spaceToReclaim;
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException{
		
		peer = new Peer();
		
		Registry registry = LocateRegistry.getRegistry("localhost");
		peer= (IPeer)registry.lookup(args[0]); 
		
		chooseMenu();
		
	}
	
	private static void chooseMenu() {
		int optMenu;
		do{
			showMenu();
			optMenu = in.nextInt();
			
			switch (optMenu) {
			case 1:
				createUser();
				break;
			case 2:
				loginUser();
				break;
			case 3:
				System.out.println("Leaving!!!\n");
				break;
			default:
				System.out.println("Wrong option choosed!\n");
				break;
			}
		}while(optMenu != 3);
	}

	private static void userMenu(User user) {
		int optMenu;

		if(user.getPriorityLevel().toString().equals("LOW")){
			do{
				showMenuUserLOW();
				optMenu = in.nextInt();
				try {
					switch (optMenu) {
					case 1:
						System.out.print("Enter File Name: ");
						fileName = inFromUser.readLine();
						File f = new File(fileName);
						
						if(!f.exists() || f.isDirectory()) {
							System.out.println("Incorrect File Name - Press Enter");
							System.in.read();
							System.out.println("\n\n");
							break;
						}
						System.out.print("Enter Desired Replication Degree: ");
						replicationDegree = in.nextInt();
						
						peer.backup(f.getAbsoluteFile(), replicationDegree);
						break;
					case 2:
						System.out.print("Enter File Name: ");
						fileName = inFromUser.readLine();
						
						peer.restore(fileName);
						break;
					case 3:
						System.out.println("Leaving!!!\n");
						break;
					default:
						System.out.println("Wrong option choosed!\n");
						break;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}while(optMenu != 3);
		}else{
			do{
				showMenuUserHIGH();
				optMenu = in.nextInt();
				try {
					switch (optMenu) {
					case 1:
						System.out.print("Enter File Name: ");
						fileName = inFromUser.readLine();
						File f = new File("Files1/test.txt");
						System.out.println(f.getAbsolutePath());
						if(!f.exists() || f.isDirectory()) {
							System.out.println("Incorrect File Name - Press Enter");
							System.in.read();
							System.out.println("\n\n");
							break;
						}
						System.out.print("Enter Desired Replication Degree: ");
						replicationDegree = in.nextInt();
						
						peer.backup( f.getAbsoluteFile(), replicationDegree);
						break;
					case 2:
						System.out.print("Enter File Name: ");
						fileName = inFromUser.readLine();
						
						peer.restore( fileName);
						break;
					case 3:
						System.out.print("Enter Desired Space to Reclaim: ");
						spaceToReclaim = in.nextInt();
						peer.reclaim(spaceToReclaim);
						break;
					case 4:
						System.out.print("Enter File Name: ");
						fileName = inFromUser.readLine();
						peer.delete(fileName);
						break;
					case 5:
						System.out.println("Leaving!!!\n");
						break;
					default:
						System.out.println("Wrong option choosed!\n");
						break;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}while(optMenu != 5);
		}
		
	}
	

	private static void createUser() {
		String name = null, password = null, level = null;
		Boolean flag = false;
		
		try {
			System.out.print("Please enter a name for user > ");
			name = inFromUser.readLine();
			if (CsvHandler.checkUser(name)){
				System.out.println("User already exists - Press enter");
				System.in.read();
				System.out.println("\n\n");
				return;
			}
			System.out.print("Please enter a password for user > ");
			password = inFromUser.readLine();
			
			while(!flag){
				System.out.print("Please enter a lever for user (low or high) > ");
				level = inFromUser.readLine().toLowerCase();
				if (LEVEL_PATTERN.matcher(level).matches())
					flag = true;
			}
			peer.createUser(name, password, level);
			
			
			System.out.println("Register successful - Press enter");
			System.in.read();
			System.out.println("\n\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private static void loginUser() {
		String name = null, password = null;
		
		try {
			System.out.print("Please enter a username> ");
			name = inFromUser.readLine();

			System.out.print("Please enter a password for user > ");
			password = inFromUser.readLine();
			String level = peer.LoginUser(name,password);
			if (level==null){
				System.out.println("Wrong password - Press enter");
				System.in.read();
				System.out.println("\n\n");
				return;
			}			
			User user = new User(name, password, level);
			
			System.out.println("Login successful - Press enter");
			System.in.read();
			System.out.println("\n\n");
			
			userMenu(user);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void showMenuUserHIGH() {
		System.out.println("============= MENU =============");
		System.out.println(" 1 - Backup");
		System.out.println(" 2 - Restore");
		System.out.println(" 3 - Reclaim");
		System.out.println(" 4 - Delete");
		System.out.println(" 5 - Exit");
		System.out.print("Please choose an option (1 to 5) >");
	}
	
	private static void showMenuUserLOW() {
		System.out.println("============= MENU =============");
		System.out.println(" 1 - Backup");
		System.out.println(" 2 - Restore");
		System.out.println(" 3 - Exit");
		System.out.print("Please choose an option (1 to 3) >");
	}
	
	private static void showMenu() {
		System.out.println("============= MENU =============");
		System.out.println(" 1 - Register a new User");
		System.out.println(" 2 - Login a User");
		System.out.println(" 3 - Exit");
		System.out.print("Please choose an option (1 to 3) >");
	}
	
}
