package com.fetchrewards.app.credit.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private HttpStatus status;
    private String message;

    ErrorResponse(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    ErrorResponse(HttpStatus status, Throwable ex){
        this();
        this.status = status;
        this.message = ex.getLocalizedMessage();
    }

}
