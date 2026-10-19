package com.nocode.service;

public class DuplicatePageException extends RuntimeException {

    public DuplicatePageException(String message) {
        super(message);
    }
}