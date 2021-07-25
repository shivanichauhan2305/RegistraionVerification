package com.registration.verification.email.otp.registrationverificationemailotp.entities;

public class StoreOTP {

	private static int otp;
	private static String mobileNumber;

	public static int getOtp() {
		return otp;
	}

	public static void setOtp(int otp) {
		StoreOTP.otp = otp;
	}

	public static String getMobileNumber() {
		return mobileNumber;
	}

	public static void setMobileNumber(String mobileNumber) {
		StoreOTP.mobileNumber = mobileNumber;
	}

	
	
}
