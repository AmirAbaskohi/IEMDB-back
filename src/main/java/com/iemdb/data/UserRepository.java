package com.iemdb.data;

import com.iemdb.model.*;
import com.iemdb.utils.Util;

import java.util.*;

public class UserRepository {
    IemdbRepository iemdbRepository;

    public UserRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        String query = "select * from user";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(query, null);
        for(Map<String, Object> result : queryResult){
            User newUser = new User(result);
            users.add(newUser);
        }
        return users;
    }

    public User getUserByEmail(String userEmail){
        ArrayList<Object> params = new ArrayList<>();
        String query = "select * from user u where u.email=?";
        params.add(userEmail);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(query, params);
        if(queryResult.size() > 0){
            return new User(queryResult.get(0));
        }
        return null;
    }

    public void addUser(String name, String nickName, String userEmail, String password, String birthDate){
        String query;
        if (password == null) {
            query = "INSERT INTO user VALUES (?, ?, ?, ?, ?)";
            iemdbRepository.updateQuery(query, new ArrayList<>(List.of(userEmail, "NULL", name, nickName, birthDate)));
        }
        else {
            query = "INSERT INTO user VALUES (?, ?, ?, ?, ?)";
            iemdbRepository.updateQuery(query, new ArrayList<>(List.of(userEmail, password, name, nickName, birthDate)));
        }

    }
}
