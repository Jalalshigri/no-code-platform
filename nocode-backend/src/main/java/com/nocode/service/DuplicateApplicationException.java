package com.nocode.service;

public class DuplicateApplicationException extends RuntimeException {

    public DuplicateApplicationException(String message) {
        super(message);
    }
}