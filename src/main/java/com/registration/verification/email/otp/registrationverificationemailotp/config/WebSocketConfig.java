package com.registration.verification.email.otp.registrationverificationemailotp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker 
class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		System.out.println("Web Socket Configure");
		 config.enableSimpleBroker("/lesson");
	     config.setApplicationDestinationPrefixes("/app");
	
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		 System.out.println(" Stomp end opint config metjod");
		 registry.addEndpoint("/gs-guide-websocket").withSockJS();
	}
}
