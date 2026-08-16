package com.nisha.bookmyshow.exception;

import org.springframework.http.HttpStatus;

public class SeatNotAvailableException extends ApiException {
    public SeatNotAvailableException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
