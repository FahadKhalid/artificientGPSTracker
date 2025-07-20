package com.fahad.artificientgpstracker.domain.model

sealed class AppError : Exception() {
    data class LocationError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()
    
    data class DatabaseError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()
    
    data class NetworkError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()
    
    data class PermissionError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()
    
    data class FileError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()
    
    data class ValidationError(
        override val message: String,
        val field: String? = null
    ) : AppError()
    
    data class SecurityError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()
    
    companion object {
        fun locationError(message: String, cause: Throwable? = null) = LocationError(message, cause)
        fun databaseError(message: String, cause: Throwable? = null) = DatabaseError(message, cause)
        fun networkError(message: String, cause: Throwable? = null) = NetworkError(message, cause)
        fun permissionError(message: String, cause: Throwable? = null) = PermissionError(message, cause)
        fun fileError(message: String, cause: Throwable? = null) = FileError(message, cause)
        fun validationError(message: String, field: String? = null) = ValidationError(message, field)
        fun securityError(message: String, cause: Throwable? = null) = SecurityError(message, cause)
    }
} 