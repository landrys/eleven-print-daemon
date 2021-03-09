package com.landry.eleven.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(exclude=WebMvcAutoConfiguration.class)
public class Main implements CommandLineRunner {
	
	Logger logger = LoggerFactory.getLogger(Main.class); 

	@Autowired
	private PrintDaemon printDaemon;

	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(Main.class).web(WebApplicationType.NONE).run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Starting the print daemon...");
		this.printDaemon.start();
		printDaemon.clean();
	}
}
