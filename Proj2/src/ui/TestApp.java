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
	private static final Pattern LEVEL_PATTERN = Pattern.compile("(low)|(high)");
	
	private static String fileName;
	private static int replicationDegree;
	private static int spaceToReclaim;
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException{
		
		peer = new Peer();
		chooseMenu();
		
		if(!validateArgs(args)){
				System.out.println("non valid args");
			return;
		}
		
		//callServer(args);
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
		do{
			showMenuUser();
			optMenu = in.nextInt();
			try {
				switch (optMenu) {
				case 1:
					System.out.print("Enter File Name: ");
					fileName = inFromUser.readLine();
					File f = new File(fileName);
					if(!f.exists() || !f.isDirectory()) {
						System.out.println("Incorrect File Name - Press Enter");
						System.in.read();
						System.out.println("\n\n");
						break;
					}
					System.out.print("Enter Desired Replication Degree: ");
					replicationDegree = in.nextInt();
					
					peer.backup(user, f.getAbsoluteFile(), replicationDegree);
					break;
				case 2:
					System.out.print("Enter File Name: ");
					fileName = inFromUser.readLine();
					
					peer.restore(user, fileName);
					break;
				case 3:
					System.out.print("Enter Desired Space to Reclaim: ");
					spaceToReclaim = in.nextInt();
					peer.reclaim(user, spaceToReclaim);
					break;
				case 4:
					System.out.print("Enter File Name: ");
					fileName = inFromUser.readLine();
					peer.delete(user, fileName);
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
				level = inFromUser.readLine();
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
			if (!CsvHandler.checkUser(name)){
				System.out.println("User doesn't exist - Press enter");
				System.in.read();
				System.out.println("\n\n");
				return;
			}
			
			System.out.print("Please enter a password for user > ");
			password = inFromUser.readLine();
			if (!password.equals(CsvHandler.getUserPassword(name))){
				System.out.println("Wrong password - Press enter");
				System.in.read();
				System.out.println("\n\n");
				return;
			}
			
			
			User user = new User(name, password, CsvHandler.getUserLevel(name));
			
			System.out.println("Login successful - Press enter");
			System.in.read();
			System.out.println("\n\n");
			
			userMenu(user);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void showMenuUser() {
		System.out.println("============= MENU =============");
		System.out.println(" 1 - Backup");
		System.out.println(" 2 - Restore");
		System.out.println(" 3 - Reclaim");
		System.out.println(" 4 - Delete");
		System.out.println(" 5 - Exit");
		System.out.print("Please choose an option (1 to 5) >");
	}
	
	private static void showMenu() {
		System.out.println("============= MENU =============");
		System.out.println(" 1 - Register a new User");
		System.out.println(" 2 - Login a User");
		System.out.println(" 3 - Exit");
		System.out.print("Please choose an option (1 to 3) >");
	}
	
	private static boolean validateArgs(String[] args) throws MalformedURLException, RemoteException, NotBoundException{

		
		if(args.length>4||args.length<2){
			System.out.println("Usage: TestApp <peer_ap> <operation> <opnd_1> <opnd_2>");
			return false;
		}
		
		Registry registry = LocateRegistry.getRegistry("localhost");
		peer= (IPeer)registry.lookup(args[0]); 
		
		switch(args[1].toLowerCase()){
		
			case "backup":{
				File f = new File(args[2]);
				if(args.length!=4 || !f.exists() || f.isDirectory()||!args[3].matches("^-?\\d+$"))
					return false;
				break;
			}
			case "backupenh":{
				File f = new File(args[2]);
				if(args.length!=4 || !f.exists() || f.isDirectory()||!args[3].matches("^-?\\d+$"))
					return false;
				break;
			}
			case "restore":{
				if(args.length!=3){
					return false;
				}
				break;
			}
			case "restoreenh":{
				if(args.length!=3){
					return false;
				}
				break;
			}
			case "delete":{
				if(args.length!=3){
					return false;
				}
				break;
			}
			case "deleteenh":{
				if(args.length!=3){
					return false;
				}
				break;
			}
			case "reclaim":{
				if(args.length!=3 || !args[2].matches("^-?\\d+$")){
					return false;
				}
				break;
			}
			case "state":{
				if(args.length!=2){
					return false;
				}
				break;
			}
			default: return false;
		
		}		
		return true;
	}
	
	private static void callServer(String[] args) throws NumberFormatException, RemoteException{		
		
		/*
		switch(args[1].toLowerCase()){
		
			case "backup":{
				peer.backup("1.0",new File(args[2]).getAbsoluteFile(), Integer.parseInt(args[3]));
				break;
			}
			case "backupenh":{
				peer.backup("2.0",new File(args[2]).getAbsoluteFile(), Integer.parseInt(args[3]));
				break;
			}
			case "restore":{
				peer.restore("1.0",new File(args[2]).getAbsoluteFile().getName());
				break;
			}
			case "restoreenh":{
				peer.restore("2.0",new File(args[2]).getAbsoluteFile().getName());
				break;
			}
			case "delete":{
				peer.delete("1.0",new File(args[2]).getAbsoluteFile().getName());
				break;
			}
			case "deleteenh":{
				peer.delete("2.0",new File(args[2]).getAbsoluteFile().getName());
				break;
			}
			case "reclaim":{
				peer.reclaim(Integer.parseInt(args[2]));
				break;
			}
			case "state":{
				String temp = peer.state();
				String[] split = temp.split("/");
				for(String s : split) {
					System.out.println(s);
				}
				break;
			}
		
		}
		*/
	}
}
