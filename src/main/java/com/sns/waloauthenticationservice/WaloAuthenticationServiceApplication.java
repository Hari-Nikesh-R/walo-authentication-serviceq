package com.sns.waloauthenticationservice;

import com.sns.waloauthenticationservice.services.GenerateResetPassCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@SpringBootApplication
public class WaloAuthenticationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaloAuthenticationServiceApplication.class, args);
	}
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@Bean
	public GenerateResetPassCode getGeneratePassCode(){
		return new GenerateResetPassCode() {
			@Override
			public String generateCode() {
				Random random = new Random();
				int max = 999999;
				int min = 111111;
				return String.valueOf(random.nextInt(max - min) + min);
			}
		};
	}

}
