package com.foryou.partyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PartyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartyApiApplication.class, args);
	}

}
