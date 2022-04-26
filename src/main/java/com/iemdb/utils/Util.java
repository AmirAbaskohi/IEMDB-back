package com.iemdb.utils;

import org.json.JSONArray;

import java.util.ArrayList;

public class Util {
    public static String JSONArrayToString(JSONArray jsonArray){
        ArrayList<String> listData = new ArrayList<>();
        if (jsonArray != null) {
            for (int i=0;i<jsonArray.length();i++){
                listData.add(jsonArray.getString(i));
            }
        }

        StringBuilder sbString = new StringBuilder("");
        for(String data : listData){
            sbString.append(data).append(", ");
        }
        String strList = sbString.toString();
        if( strList.length() > 0 )
            strList = strList.substring(0, strList.length() - 2);
        return strList;
    }

    public static String ArrayStringToString(ArrayList<String> array){
        StringBuilder sbString = new StringBuilder("");
        for(String data : array){
            sbString.append(data).append(", ");
        }
        String strList = sbString.toString();
        if( strList.length() > 0 )
            strList = strList.substring(0, strList.length() - 2);
        return strList;
    }

    public static String ArrayIntToString(ArrayList<Integer> array){
        StringBuilder sbString = new StringBuilder("");
        for(Integer data : array){
            sbString.append(data.toString()).append(", ");
        }
        String strList = sbString.toString();
        if( strList.length() > 0 )
            strList = strList.substring(0, strList.length() - 2);
        return strList;
    }
}
