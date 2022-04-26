package com.iemdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class IEMDBApp {
	public static void main(String[] args) {
		SpringApplication.run(IEMDBApp.class, args);
	}
}
