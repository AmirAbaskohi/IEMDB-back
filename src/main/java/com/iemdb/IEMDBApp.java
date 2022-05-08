package com.iemdb;

import com.iemdb.data.IemdbRepository;
import com.iemdb.data.MovieRepository;
import com.iemdb.model.Movie;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.swagger.models.auth.In;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.rowset.*;
import java.sql.*;
import java.util.ArrayList;

import java.util.Map;
import java.util.regex.*;

import static java.util.Map.entry;

//@SpringBootApplication
//@ServletComponentScan
//@EnableSwagger2
public class IEMDBApp {
	public static void main(String[] args) {
//		SpringApplication.run(IEMDBApp.class, args);

		IemdbRepository iemdbRepository = new IemdbRepository();
		MovieRepository movieRepository = new MovieRepository();
		ArrayList<Movie> movies = movieRepository.getMovies(2, "god", "date");
	}
}
