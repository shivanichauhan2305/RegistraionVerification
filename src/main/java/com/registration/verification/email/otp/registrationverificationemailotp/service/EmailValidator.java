package com.registration.verification.email.otp.registrationverificationemailotp.service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class EmailValidator implements Predicate<String> {

	String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	 
	Pattern pattern = Pattern.compile(regex);
	
	@Override
	public boolean test(String t) {
		// TODO Auto-generated method stub
		 Matcher matcher = pattern.matcher(t);
		return matcher.matches();
	}
	
	

}
