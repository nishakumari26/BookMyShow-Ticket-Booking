package com.nisha.bookmyshow.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends ApiException {
    public BookingException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
