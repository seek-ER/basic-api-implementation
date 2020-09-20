package com.thoughtworks.rslist.exception;

public class VoteNotValidException extends RuntimeException{
    private String errorMessage;

    public VoteNotValidException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
