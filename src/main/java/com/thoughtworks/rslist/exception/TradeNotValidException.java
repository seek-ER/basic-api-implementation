package com.thoughtworks.rslist.exception;

public class TradeNotValidException extends RuntimeException{
    private String errorMessage;

    public TradeNotValidException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
