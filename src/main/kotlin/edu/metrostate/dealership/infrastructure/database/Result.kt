// Just a helper to encapsulate a result that needs a boolean + a message.
package edu.metrostate.dealership.infrastructure.database

class Result<T> private constructor(
    val isSuccess: Boolean,
    private val data: T?,
    val errorMessage: String? = null
) {
    fun getData(): T {
        check(isSuccess) { "Cannot get data from a failed result: $errorMessage" }
        return data!!
    }

    companion object {
        fun <T> success(): Result<T> = Result(true, null)

        fun <T> success(data: T): Result<T> = Result(true, data)

        fun <T> failure(errorMessage: String): Result<T> = Result(false, null, errorMessage)
    }
}
