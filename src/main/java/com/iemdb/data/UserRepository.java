package com.iemdb.data;

import com.iemdb.model.*;
import java.util.*;

public class UserRepository {
    IemdbRepository iemdbRepository;

    public UserRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        String query = "select * from user";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(query);
        for(Map<String, Object> result : queryResult){
            User newUser = new User(result);
            users.add(newUser);
        }
        return users;
    }

    public User getUserByEmail(String userEmail){
        String query = String.format("select * from user u where u.email='%s'", userEmail);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(query);
        if(queryResult.size() > 0){
            User newUser = new User(queryResult.get(0));
            return newUser;
        }else{
            System.out.println("User does not exist");
        }
        return null;
    }
}
