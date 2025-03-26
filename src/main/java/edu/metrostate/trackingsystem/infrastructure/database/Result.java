// Just a helper to encapsulate a result that needs a boolean + a message.
package edu.metrostate.trackingsystem.infrastructure.database;


public class Result<T> {

    private final boolean success;
    private final T data;
    private final String errorMessage;

    private Result(boolean success, T data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success() {
        return new Result<>(true, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> failure(String errorMessage) {
        return new Result<>(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        if (!success) {
            throw new IllegalStateException("Cannot get data from a failed result: " + errorMessage);
        }
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
