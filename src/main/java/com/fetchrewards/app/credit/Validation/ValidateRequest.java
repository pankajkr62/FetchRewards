package com.fetchrewards.app.credit.Validation;

import com.fetchrewards.app.credit.Exceptions.ValidationFailedException;
import com.fetchrewards.app.credit.Models.RequestOfSpend;
import com.fetchrewards.app.credit.Models.Transaction;
import org.springframework.stereotype.Component;


@Component
public class ValidateRequest {

    public static void validateTransaction(Transaction txn) throws ValidationFailedException {
        if(txn.getPayer() == null && txn.getPoints() == 0 && txn.getTimestamp() == null){
            throw new ValidationFailedException("Transaction Request should not be null");
        }
        if( !(txn.getPayer() != null && !txn.getPayer().isEmpty())){
            throw new ValidationFailedException("Payer should not be null or blank");
        }
        if(txn.getPoints() == 0){
            throw new ValidationFailedException("Points should not be null or zero");
        }
        if(txn.getTimestamp() == null) {
            throw new ValidationFailedException("TimeStamp should not be null or blank");
        }
    }

    public static void validateRequestOfSpend(RequestOfSpend spend) throws ValidationFailedException{
        if(spend.getPoints() <= 0){
            throw new ValidationFailedException("Spend Points should be greater than zero");
        }
    }




}
