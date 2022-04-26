package com.iemdb.info;

public class RatingInfo {
    private Boolean successFull;
    private String message;

    public RatingInfo(Boolean _successFull, String _message){
        successFull = _successFull;
        message = _message;
    }

    public Boolean getSuccessFull(){return successFull;}
    public String getMessage(){return message;}
}
