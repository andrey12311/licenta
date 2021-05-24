package com.andrei.licenta.exceptions;

public class EmailExistsException extends Exception{
    public EmailExistsException(String message) {
        super(message);
    }
}
