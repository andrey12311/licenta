package com.andrei.licenta.model;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class HttpResponse implements Serializable {
    private int httpStatusCode; //200 sucess , 400 user error , 500 server error
    private HttpStatus httpStatus; //OK,CREATED,BAD_REQUESTE, etc..
    private String reason;
    private String message; //your request was succesful

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

    public HttpResponse() {
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
