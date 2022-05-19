package com.iemdb;

import com.iemdb.utils.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.*;
import java.nio.charset.*;
import java.security.*;

@SpringBootApplication
@ServletComponentScan
@EnableSwagger2
public class IEMDBApp {
	public static void main(String[] args) {
		SpringApplication.run(IEMDBApp.class, args);
	}
}
