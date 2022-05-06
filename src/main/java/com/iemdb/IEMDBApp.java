package com.iemdb;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.sql.*;

//@SpringBootApplication
//@ServletComponentScan
//@EnableSwagger2
public class IEMDBApp {
	public static void main(String[] args) {
//		SpringApplication.run(IEMDBApp.class, args);
		ComboPooledDataSource dataSource;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/iemdb?autoReconnect=true&useSSL=false");
		dataSource.setUser("root");
		dataSource.setPassword("Root_1234");

		dataSource.setInitialPoolSize(5);
		dataSource.setMinPoolSize(5);
		dataSource.setAcquireIncrement(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxStatements(100);

		Statement statement;
		try {
			Connection connection = dataSource.getConnection();
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(
					"select * from genre");
			result.next();
			System.out.println(result.getString(2));

			result.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


}
