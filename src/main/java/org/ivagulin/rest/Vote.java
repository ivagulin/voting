package org.ivagulin.rest;

import javax.validation.constraints.NotNull;

public class Vote {
	@NotNull
	private String email;
	
	@NotNull
	private Integer language;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getLanguage() {
		return language;
	}
	public void setLanguage(Integer language) {
		this.language = language;
	}	
}
