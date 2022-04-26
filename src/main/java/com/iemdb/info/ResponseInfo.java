package com.iemdb.info;

import java.util.ArrayList;

public class ResponseInfo {
    private final Object value;
    private final ArrayList<String> errors;
    private final String message;
    private final boolean success;

    public ResponseInfo(Object _value, boolean _success) {
        value = _value;
        message = "No Message";
        errors = new ArrayList<>();
        success = _success;
    }

    public ResponseInfo(Object _value, boolean _success, String _message) {
        value = _value;
        message = _message.isBlank() || _message.isBlank() ? "No Message" : _message;
        errors = new ArrayList<>();
        success = _success;
    }

    public ResponseInfo(Object _value, boolean _success, String _message, ArrayList<String> _errors) {
        value = _value;
        message = _message.isBlank() || _message.isBlank() ? "No Message" : _message;
        errors = _errors;
        success = _success;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void addErrors(ArrayList<String> _errors) {
        errors.addAll(_errors);
    }

    public String getMessage() { return message; }
    public ArrayList<String> getErrors() { return errors; }
    public Object getValue() { return value; }
}
