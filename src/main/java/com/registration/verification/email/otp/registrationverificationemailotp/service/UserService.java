package com.registration.verification.email.otp.registrationverificationemailotp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.registration.verification.email.otp.registrationverificationemailotp.repository.UserRepository;
import com.registration.verification.email.otp.registrationverificationemailotp.token.ConfirmationToken;
import com.registration.verification.email.otp.registrationverificationemailotp.token.ConfirmationTokenService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.registration.verification.email.otp.registrationverificationemailotp.dto.UserRegistrationDto;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.Role;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.StoreOTP;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.TempOTP;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.User;

@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailValidator emailValidator;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MobileNumberValidation mobileNumberValidation;
	
	@Autowired
	private ConfirmationTokenService confirmationTokenService;
	
    @Autowired
    private SimpMessagingTemplate webSocket;
	
	private final String ACCOUNT_SID ="AC40889c9df3a6154ac097839cc122174b";

    private final String AUTH_TOKEN = "9cb244eb7b53f934b487471d642a8024";

    private final String FROM_NUMBER = "+19102124185";

    private final String  TOPIC_DESTINATION = "/lesson/sms";
    
	public String save(UserRegistrationDto userRegistrationDto) {
		User user=new User(userRegistrationDto.getName(),userRegistrationDto.getAddress(),userRegistrationDto.getEmailOrMobileno(),passwordEncoder.encode(userRegistrationDto.getPassword()),Arrays.asList(new Role("ROLE_USER")));
		boolean isValidEmail = emailValidator.
                test(user.getEmailOrMobileno());

        if (!isValidEmail) {
        	
        	/*boolean isValidMobileNumber=mobileNumberValidation.test(user.getEmailOrMobileno());
        	
        	if (!isValidMobileNumber) {
        		throw new IllegalStateException("email or mobile number not valid");
        	}
        	else
        	{*/
        		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        	      
            	
                int min = 100000;  
                 int max = 999999; 
                int number=(int)(Math.random()*(max-min+1)+min);
              
                
                String msg ="Your OTP - "+number+ " please verify this OTP in your Application by Shivani Chauhan";
               
                
                Message message = Message.creator(new PhoneNumber(user.getEmailOrMobileno()), new PhoneNumber(FROM_NUMBER), msg)
                        .create();
               
            StoreOTP.setOtp(number);
            StoreOTP.setMobileNumber(user.getEmailOrMobileno());
            webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": SMS has been sent!: "+user.getEmailOrMobileno());
            userRepository.save(user);
            return "Otp sent successfully";
        	
           // throw new IllegalStateException("email not valid");
        }
		
		User userExists = null;
		userExists=userRepository
                .findByEmailOrMobileno(user.getEmailOrMobileno());

        if (userExists!=null) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.

            throw new IllegalStateException("email already taken");
        }
        
        userRepository.save(user);
        
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setTo(user.getEmailOrMobileno());
        mailMessage.setSubject("Complete Registration!!");
        mailMessage.setFrom("shivanishivu33@gmail.com");
        mailMessage.setText("To complete your registration please click on link provided below "+"http://localhost:9090/confirm?token="+token);
        emailService.sendEmail(mailMessage);
		return "token successfully sent";
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrMobileno(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmailOrMobileno(), user.getPassword(), mapRolesToAuthorities(Arrays.asList(new Role("ROLE_USER"))));
	}
	  private Collection<? extends GrantedAuthority > mapRolesToAuthorities(Collection < Role > roles) {
	        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	    }
	  
	  
	  @Transactional
	    public String confirmToken(String token) {
	        ConfirmationToken confirmationToken = confirmationTokenService
	                .getToken(token)
	                .orElseThrow(() ->
	                        new IllegalStateException("token not found"));

	        if (confirmationToken.getConfirmedAt() != null) {
	            throw new IllegalStateException("email already confirmed");
	        }

	        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

	        if (expiredAt.isBefore(LocalDateTime.now())) {
	            throw new IllegalStateException("token expired");
	        }

	        confirmationTokenService.setConfirmedAt(token);
	        
	        userRepository.enableAppUser(confirmationToken.getUser().getEmailOrMobileno());
	        return "confirmed";
	    }
	  
	  private String getTimeStamp() {
	       return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
	    }

	public String confirmOtp(TempOTP sms) {
		// TODO Auto-generated method stub

		if(sms.getOtp()==StoreOTP.getOtp())
		{
			 userRepository.enableAppUserMobileNo(StoreOTP.getMobileNumber());
			return "correct otp";
		}
		else
		{
			return "not correct otp";
		}
	}
	
	public void receive(MultiValueMap<String, String> smscallback) {
		   
    }

}
