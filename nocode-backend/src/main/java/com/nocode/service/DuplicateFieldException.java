package com.nocode.service;


public class DuplicateFieldException extends RuntimeException {

    public DuplicateFieldException(String message) {
        super(message);
    }
}
