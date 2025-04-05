package edu.metrostate.dealership.infrastructure.utils

import java.io.File

interface IFileHandler {
    fun importFile(file: File): Boolean
    fun exportFile(file: File): Boolean
}