package com.registration.verification.email.otp.registrationverificationemailotp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.registration.verification.email.otp.registrationverificationemailotp.dto.UserRegistrationDto;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.StoreOTP;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.TempOTP;
import com.registration.verification.email.otp.registrationverificationemailotp.entities.User;
import com.registration.verification.email.otp.registrationverificationemailotp.service.UserService;


@RestController
@RequestMapping
public class UserRegistrationController {
	
	private UserService userService;

    @Autowired
    private SimpMessagingTemplate webSocket;

   private final String  TOPIC_DESTINATION = "/lesson/sms";
	//constructor dependeny injection
	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
		
	}
	
	@PostMapping("/registration")
	public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
		String token=userService.save(userRegistrationDto);
		return new ResponseEntity<String>(token,HttpStatus.OK);
	}
	
	@GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) {
        return userService.confirmToken(token);
    }
	
	@PostMapping("/otp")
	public String verifyOTP(@RequestBody TempOTP sms) {
		
		return userService.confirmOtp(sms);
		
	}
	
	@RequestMapping(value = "/smscallback", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void smsCallback(@RequestBody MultiValueMap<String, String> map) {
		userService.receive(map);
       webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": Twilio has made a callback request! Here are the contents: "+map.toString());
    }
	
	 private String getTimeStamp() {
	       return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
	    }
}
