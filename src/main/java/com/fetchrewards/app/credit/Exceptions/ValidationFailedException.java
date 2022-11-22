package com.fetchrewards.app.credit.Exceptions;

public class ValidationFailedException extends Throwable{

    public String message;

    public ValidationFailedException(String message){
        super(message);
        this.message = message;
    }
}

