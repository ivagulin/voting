package org.ivagulin.rest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class User {
	
	@Pattern(regexp="[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}")
	private String email;
	
	@NotNull
	@Pattern(regexp="[A-Za-z]+ [A-Za-z]+")
	private String name;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
