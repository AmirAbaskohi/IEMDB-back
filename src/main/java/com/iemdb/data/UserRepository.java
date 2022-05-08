package com.iemdb.data;

import com.iemdb.model.*;
import java.util.*;

public class UserRepository {
    IemdbRepository iemdbRepository;

    public UserRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public User getUserByEmail(String userEmail){
        String query = String.format("select * from user u where u.email=%s", userEmail);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        if(response.size() > 0){
            User newUser = new User(response.get(0));
            return newUser;
        }else{
            System.out.println("Comment does not exist");
        }
        return null;
    }
}
