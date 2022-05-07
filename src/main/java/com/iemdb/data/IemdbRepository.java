package com.iemdb.data;

import com.iemdb.utils.Util;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        initializeTables();
    }

    public void initializeTables(){
        if(sendQuery("select * from user").size() == 0){
            addUsers();
        }
        if(sendQuery("select * from actor").size() == 0){
            addActors();
        }
//        System.out.println(sendQuery("select * from user"));
    }

    public ArrayList<Map<String, Object>> sendQuery(String query){
        ArrayList<Map<String, Object>> response = new ArrayList<>();
            Connection connection = null;
            Statement statement = null;
            ResultSet result = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            ResultSetMetaData metaData= result.getMetaData();

            while (result.next()){
                Map<String, Object> newEntry = new HashMap<>();
                for(int i=1; i <= metaData.getColumnCount(); i++){
                    newEntry.put(metaData.getColumnName(i), result.getObject(i));
                }
                response.add(newEntry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(statement != null && result!=null){
                result.close();
                statement.close();
                connection.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
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

    public ArrayList<String> getResponseFromUrl(String _url){
        ArrayList<String> response = new ArrayList<>();
        try{
            URL url = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.add(inputLine);
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public void addUsers(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/users");
        JSONArray jsonArray = new JSONArray(response.get(0));
        String query = "INSERT INTO user VALUES ";
        boolean commaNeeded = false;
        for (Object data : jsonArray){
            JSONObject userData = (JSONObject) data;
            query += commaNeeded ? ",(" : "(";
            query += "'" + userData.getString("email") + "',";
            query += "'" + userData.getString("password") + "',";
            query += "'" + userData.getString("name") + "',";
            query += "'" + userData.getString("nickname") + "',";
            query += "'" + userData.getString("birthDate") + "'";
            query += ")";
            commaNeeded = true;
        }
        updateQuery(query);
    }

    public void addActors(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/v2/actors");
        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object data : jsonArray){
            String query = "INSERT INTO actor VALUES ";
            JSONObject userData = (JSONObject) data;
            query += "(";
            query += "" + userData.getInt("id") + ",";
            query += "'" + userData.getString("name") + "',";
            String birthDate = Util.standardizeDateType(userData.getString("birthDate"));
            if (birthDate == null){
                query += "null,";
            }
            else{
                query += "'" + Util.standardizeDateType(userData.getString("birthDate")) + "',";
            }
            query += "'" + userData.getString("nationality") + "',";
            query += "'" + userData.getString("image") + "'";
            query += ")";
            updateQuery(query);
        }
    }
}
