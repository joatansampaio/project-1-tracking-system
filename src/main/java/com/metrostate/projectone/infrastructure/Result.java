package com.metrostate.projectone.infrastructure;

// Just a helper to encapsulate a result that needs a boolean + a message.
public class Result<T> {
    private T data;
    private boolean isSuccess;
    private String errorMessage;

    public Result<T> Success() {
        this.isSuccess = true;
        return this;
    }

    public Result<T> Success(T data) {
        this.isSuccess = true;
        this.data = data;
        return this;
    }

    public Result<T> Fail(String errorMessage) {
        this.isSuccess = false;
        this.errorMessage = errorMessage;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean IsSuccess() {
        return this.isSuccess;
    }
}
