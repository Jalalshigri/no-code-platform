package com.nocode.service;

public class InvalidFieldConfigurationException
        extends RuntimeException {

    public InvalidFieldConfigurationException(String message) {
        super(message);
    }
}