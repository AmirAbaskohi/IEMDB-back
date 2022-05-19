package com.iemdb.data;

import com.iemdb.utils.Util;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.json.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class IemdbRepository {

    ComboPooledDataSource dataSource;

    public IemdbRepository(){
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.initialize();
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
        if (sendQuery("select * from movie").size() == 0){
            addMovies();
        }
        if (sendQuery("select * from comment").size() == 0){
            addComments();
        }
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
            String passHash = Util.toHexString(Util.getSHA(userData.getString("password")));
            query += commaNeeded ? ",(" : "(";
            query += "'" + userData.getString("email") + "',";
            query += "'" + passHash + "',";
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

    public void addMovies(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/v2/movies");

        ArrayList<String> writers = new ArrayList<>();
        ArrayList<String> genres = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object data : jsonArray){
            String query = "INSERT INTO movie VALUES ";
            JSONObject movieData = (JSONObject) data;
            query += "(";
            query += "" + movieData.getInt("id") + ",";
            query += "\"" + movieData.getString("name") + "\",";
            query += "\"" + movieData.getString("summary").replace("\"", "\\\"") + "\",";
            query += "\"" + movieData.getString("releaseDate") + "\",";
            query += "\"" + movieData.getString("director") + "\",";
            query += "" + movieData.getDouble("imdbRate") + ",";
            query += "" + movieData.getInt("duration") + ",";
            query += "" + movieData.getInt("ageLimit") + ",";
            query += "NULL,";
            query += "'" + movieData.getString("image") + "',";
            query += "'" + movieData.getString("coverImage") + "'";
            query += ");";

            updateQuery(query);

            JSONArray movieWriters = movieData.getJSONArray("writers");
            JSONArray movieGenres = movieData.getJSONArray("genres");
            JSONArray movieCast = movieData.getJSONArray("cast");

            for (Object movieWriter : movieWriters) {
                int writerIndex = writers.indexOf((String) movieWriter);
                if (writerIndex == -1) {
                    writerIndex = writers.size();
                    writers.add((String) movieWriter);
                }

                String writerQuery = "INSERT INTO writer VALUES ";
                writerQuery += "(";
                writerQuery += "" + (writerIndex+1) + ",";
                writerQuery += "'" + (String)movieWriter + "'";
                writerQuery += ");";

                updateQuery(writerQuery);

                writerQuery = "INSERT INTO writer_movie VALUES ";
                writerQuery += "(";
                writerQuery += "" + (writerIndex+1) + ",";
                writerQuery += "" + movieData.getInt("id") + "";
                writerQuery += ");";

                updateQuery(writerQuery);
            }

            for (Object movieGenre : movieGenres) {
                int genreIndex = genres.indexOf((String) movieGenre);
                if (genreIndex == -1) {
                    genreIndex = genres.size();
                    genres.add((String) movieGenre);
                }

                String genreQuery = "INSERT INTO genre VALUES ";
                genreQuery += "(";
                genreQuery += "" + (genreIndex+1) + ",";
                genreQuery += "'" + movieGenre + "'";
                genreQuery += ");";

                updateQuery(genreQuery);

                genreQuery = "INSERT INTO genre_movie VALUES ";
                genreQuery += "(";
                genreQuery += "" + movieData.getInt("id") + ",";
                genreQuery += "" + (genreIndex+1) + "";
                genreQuery += ");";

                updateQuery(genreQuery);
            }

            for (Object movieActor : movieCast) {
                Integer actorId = (Integer)movieActor;

                String actorQuery = "INSERT INTO actor_movie VALUES ";
                actorQuery += "(";
                actorQuery += "" + actorId + ",";
                actorQuery += "" + movieData.getInt("id") + "";
                actorQuery += ");";

                updateQuery(actorQuery);
            }
        }
    }

    public void addComments(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/comments");
        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object data : jsonArray){
            JSONObject commentData = (JSONObject) data;
            String query = "INSERT INTO comment VALUES ";
            query += "(";
            query += "NULL,";
            query += "'" + commentData.getString("userEmail") + "',";
            query += "'" + commentData.getString("text") + "',";
            query += "" + commentData.getInt("movieId") + "";
            query += ")";
            updateQuery(query);
        }
    }

}
