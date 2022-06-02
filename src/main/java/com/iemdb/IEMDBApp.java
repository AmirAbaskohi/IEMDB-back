package com.iemdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@ServletComponentScan
@EnableSwagger2
public class IEMDBApp {
	public static void main(String[] args) {
		SpringApplication.run(IEMDBApp.class, args);
	}
}
