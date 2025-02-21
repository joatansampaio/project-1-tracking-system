package edu.metrostate.trackingsystem.application.exceptions;

import java.util.List;

public class ValidationException extends Exception {
    private final List<String> validationErrors;

    public ValidationException(List<String> validationErrors) {
        super("Validation failed.");
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}