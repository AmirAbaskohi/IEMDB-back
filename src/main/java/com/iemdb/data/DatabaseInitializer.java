package com.iemdb.data;

import com.iemdb.sqls.SqlQueries;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseInitializer {
    ComboPooledDataSource dataSource;

    String databaseName = "iemdb";

    List<String> tableNames = List.of("actor", "movie", "user", "rate", "comment", "genre", "vote", "actor_movie",
            "writer", "genre_movie", "watchlist", "writer_movie");

    public DatabaseInitializer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("Root_1234");

        dataSource.setInitialPoolSize(5);
        dataSource.setMinPoolSize(5);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(20);
        dataSource.setMaxStatements(100);
    }

    public void updateQuery(String query){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println("BadQuery");
        }
        if (statement != null){
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize(){
        if(!checkDatabaseExistence()){
            updateQuery("create schema " + databaseName + ";");
        }
        changeConnection();
        createTables();
    }

    public void createTables(){
        for (String tableName : tableNames){
            String content = SqlQueries.getQueries().get(tableName);
            updateQuery(content);
        }
    }

    public boolean checkDatabaseExistence(){
        try{
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                if (name.equals(databaseName)){
                    connection.close();
                    resultSet.close();
                    return true;
                }
            }
            connection.close();
            resultSet.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void changeConnection(){
        dataSource.close();
        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + databaseName + "?autoReconnect=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("Root_1234");

        dataSource.setInitialPoolSize(5);
        dataSource.setMinPoolSize(5);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(20);
        dataSource.setMaxStatements(100);
    }

    }
