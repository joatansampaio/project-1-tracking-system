package edu.metrostate.dealership.infrastructure.database

import com.google.gson.JsonObject
import edu.metrostate.dealership.infrastructure.database.Result.Companion.failure
import edu.metrostate.dealership.infrastructure.database.Result.Companion.success
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ResultTest {

    @Test
    fun success() {
        val result = success<Any>()
        assertTrue(result.isSuccess)
    }

    @Test
    fun testSuccess() {
        assertTrue(success<Any>().isSuccess)
    }

    @Test
    fun failure() {
        assertFalse(failure<Any>("").isSuccess)
    }

    @Test
    fun isSuccessFlag() {
        val result = failure<Any>("")
        assertFalse(result.isSuccess)
    }

    @Test
    fun dataPresentOnSuccess() {
        val result = success(JsonObject())
        assertNotNull(result.getData())
    }

    @Test
    fun errorMessageOnFailure() {
        val result = failure<Any>("error")
        assertEquals("error", result.errorMessage)
    }

    @Test
    fun cantGetDataWhenFailure() {
        val result = failure<Any>("error")
        assertThrows(IllegalStateException::class.java) {
            result.getData()
        }
    }
}
