package com.iemdb.utils;

import org.json.*;
import java.util.*;
import java.util.regex.*;

import java.math.*;
import java.nio.charset.*;
import java.security.*;

import static java.util.Map.entry;

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

    public static String standardizeDateType(String date){
        Map<String, String> monthToNum = Map.ofEntries(
                entry("January", "01"), entry("February", "02"), entry("March", "03"),
                entry("April", "04"), entry("May", "05"), entry("June", "06"),
                entry("July", "07"), entry("August", "08"), entry("September", "09"),
                entry("October", "10"), entry("November", "11"), entry("December", "12")
        );
        String regex = "(\\w*)\\s(\\d*),\\s(\\d*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);

        StringBuilder result = new StringBuilder();
        if(matcher.find()) {
            result.append(matcher.group(3));
            result.append("-");
            result.append(monthToNum.get(matcher.group(1)));
            result.append("-");
            result.append(matcher.group(2));
            return result.toString();
        }
        else {
            return null;
        }
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64){
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
