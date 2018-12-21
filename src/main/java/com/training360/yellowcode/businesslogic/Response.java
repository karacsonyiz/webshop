package com.training360.yellowcode.businesslogic;

public class Response {

    private boolean isValidRequest;
    private String message;

    public Response(boolean isValid, String message) {
        this.isValidRequest = isValid;
        this.message = message;
    }

    public boolean isValidRequest() {
        return isValidRequest;
    }

    public void setValidRequest(boolean validRequest) {
        isValidRequest = validRequest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
