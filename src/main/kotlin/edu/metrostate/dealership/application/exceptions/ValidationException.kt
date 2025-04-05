package edu.metrostate.dealership.application.exceptions

class ValidationException(val validationErrors: List<String>) : Exception("Validation failed.") 