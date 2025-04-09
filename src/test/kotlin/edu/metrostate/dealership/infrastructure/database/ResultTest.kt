package edu.metrostate.dealership.infrastructure.database

import com.google.gson.JsonObject
import edu.metrostate.dealership.infrastructure.database.Result.Companion.failure
import edu.metrostate.dealership.infrastructure.database.Result.Companion.success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ResultTest {
    @Test
    fun success() {
        val result = success<Any>()
        Assertions.assertTrue(result.isSuccess)
    }

    @Test
    fun testSuccess() {
        Assertions.assertTrue(success<Any>().isSuccess)
    }

    @Test
    fun failure() {
        Assertions.assertFalse(failure<Any>("").isSuccess)
    }

    @get:Test
    val isSuccess: Unit
        get() {
            val result =
                failure<Any>("")
            Assertions.assertFalse(result.isSuccess)
        }

    @get:Test
    val data: Unit
        get() {
            val result =
                success(JsonObject())
            Assertions.assertNotNull(result.getData())
        }

    @get:Test
    val errorMessage: Unit
        get() {
            val result =
                failure<Any>("error")
            Assertions.assertEquals("error", result.errorMessage)
        }

    @Test
    fun cantGetDataWhenFailure() {
        val result = failure<Any>("error")
        Assertions.assertThrows(
            IllegalStateException::class.java
        ) { result.getData() }
    }
}