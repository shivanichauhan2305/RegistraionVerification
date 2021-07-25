package com.registration.verification.email.otp.registrationverificationemailotp.dto;

public class UserRegistrationDto {
	
	private String name;
    private String address;
    private String emailOrMobileno;
    private String password;
    
    public UserRegistrationDto() {

    }
    
	public UserRegistrationDto(String name, String address, String emailOrMobileno,  String password) {
		super();
		this.name = name;
		this.address = address;
		this.emailOrMobileno = emailOrMobileno;
		this.password = password;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getEmailOrMobileno() {
		return emailOrMobileno;
	}

	public void setEmailOrMobileno(String emailOrMobileno) {
		this.emailOrMobileno = emailOrMobileno;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
   
}
