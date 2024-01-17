package jku.win.se.assignmentManager.backend.dto;

import dev.morphia.annotations.Entity;

@Entity
public class User extends AbstractEntity {
	
	private String username;
	private String password;
	
	public User() {}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
