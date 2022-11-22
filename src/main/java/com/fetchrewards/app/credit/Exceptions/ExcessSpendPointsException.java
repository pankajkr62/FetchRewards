package com.fetchrewards.app.credit.Exceptions;



public class ExcessSpendPointsException extends Throwable {

    public String message;

    public ExcessSpendPointsException(String message){
        super(message);
        this.message = message;
    }

}
