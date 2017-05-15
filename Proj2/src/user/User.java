package user;

public class User {
	private String username;
	private String password;
	private PriorityLevel priorityLevel;
	
	public User(String username, String password, String priorityLevel){
		this.username=username;
		this.password=password;
		if(priorityLevel.equals("low")){
			this.priorityLevel=PriorityLevel.LOW;
		}
		else if(priorityLevel.equals("high")){
			this.priorityLevel=PriorityLevel.HIGH;
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PriorityLevel getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(PriorityLevel priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	
}
