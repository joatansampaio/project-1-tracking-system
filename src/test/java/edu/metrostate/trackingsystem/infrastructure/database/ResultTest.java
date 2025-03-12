package edu.metrostate.trackingsystem.infrastructure.database;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void success() {
        var result = Result.success();
        assertTrue(result.isSuccess());
    }

    @Test
    void testSuccess() {
        assertTrue(Result.success().isSuccess());
    }

    @Test
    void failure() {
        assertFalse(Result.failure("").isSuccess());
    }

    @Test
    void isSuccess() {
        var result = Result.failure("");
        assertFalse(result.isSuccess());
    }

    @Test
    void getData() {
        var result = Result.success(new JsonObject());
        assertNotNull(result.getData());
    }

    @Test
    void getErrorMessage() {
        var result = Result.failure("error");
        assertEquals("error", result.getErrorMessage());
    }

    @Test
    void cantGetDataWhenFailure() {
        var result = Result.failure("error");
        assertThrows(IllegalStateException.class, result::getData);
    }
}