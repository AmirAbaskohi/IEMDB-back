package com.iemdb.data;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.*;
import java.util.*;

public class IemdbRepository {

    ComboPooledDataSource dataSource;

    public IemdbRepository(){
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
    }

    public ArrayList<Map<String, Object>> sendQuery(String query){
        ArrayList<Map<String, Object>> response = new ArrayList<>();

        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            ResultSetMetaData metaData= result.getMetaData();

            while (result.next()){
                Map<String, Object> newEntry = new HashMap<>();
                for(int i=1; i <= metaData.getColumnCount(); i++){
                    newEntry.put(metaData.getColumnName(i), result.getObject(i));
                }
                response.add(newEntry);
            }

            result.close();
            statement.close();
            connection.close();

            return response;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void addUsers(){

    }
}
