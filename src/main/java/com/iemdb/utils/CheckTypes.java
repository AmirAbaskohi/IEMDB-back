package com.iemdb.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.time.LocalDate;

public class CheckTypes {

    public JSONArray objectToJSONArray(Object object){
        Object json = null;
        JSONArray jsonArray = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            jsonArray = (JSONArray) json;
        }
        return jsonArray;
    }

    public boolean isInt(Object value) {
        return value instanceof Integer;
    }

    public boolean isDouble(Object value) {
        return value instanceof Double;
    }

    public boolean isString(Object value) {
        return value instanceof String;
    }

    public boolean isDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean isJsonArray(Object value) {
        return value instanceof JSONArray;
    }

    public boolean isListInteger(JSONArray array) {
        for (int i = 0 ; i < array.length() ; i++) {
            try {
                if (!(array.get(i) instanceof Integer))
                    return false;
            }catch (Exception e){
                return false;
            }

        }
        return true;
    }

    public boolean isListString(JSONArray array) {
        for (int i = 0 ; i < array.length() ; i++) {
            try {
                if (!(array.get(i) instanceof String))
                    return false;
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }

    public boolean isValidType(String type, Object value) {
        switch (type) {
            case "int":
                return isInt(value);
            case "double":
                return isDouble(value);
            case "string":
                return isString(value);
            case "date":
                if (!isString(value))
                    return false;
                return isDate(value.toString());
            case "string_list":
                if (!isJsonArray(value))
                    return false;
                return isListString(objectToJSONArray(value));
            case "int_list":
                if (!isJsonArray(value))
                    return false;
                return isListInteger(objectToJSONArray(value));
            default:
                return false;
        }
    }
}
