package com.iemdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.*;


@SpringBootApplication
@ServletComponentScan
@EnableSwagger2
public class IEMDBApp {
	public static void main(String[] args) {
//		SpringApplication.run(IEMDBApp.class, args);

		System.out.println("Hello World!!!!!!!!!");

		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://iemdb-mysql:3306","root","root");
			ResultSet resultSet = con.getMetaData().getCatalogs();
			while (resultSet.next()) {
				String name = resultSet.getString(1);
				System.out.println(name);
			}

		}catch(Exception e){ System.out.println(e);}
	}
}
