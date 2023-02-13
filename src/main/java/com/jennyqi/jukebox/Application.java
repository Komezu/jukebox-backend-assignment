package com.jennyqi.jukebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

  // Create RestTemplate bean for injection as a dependency in service class
  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
