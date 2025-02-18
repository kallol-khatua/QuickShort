package com.quickshort.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class WorkspaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkspaceServiceApplication.class, args);
	}

}
