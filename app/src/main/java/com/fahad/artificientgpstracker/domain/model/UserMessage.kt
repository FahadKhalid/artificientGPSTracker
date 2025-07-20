package com.fahad.artificientgpstracker.domain.model

sealed class UserMessage {
    data class Success(val message: String) : UserMessage() {
        override fun toString(): String = message
    }
    data class Error(val message: String) : UserMessage() {
        override fun toString(): String = message
    }
    data class Info(val message: String) : UserMessage() {
        override fun toString(): String = message
    }
    data class Warning(val message: String) : UserMessage() {
        override fun toString(): String = message
    }
    
    companion object {
        fun success(message: String) = Success(message)
        fun error(message: String) = Error(message)
        fun info(message: String) = Info(message)
        fun warning(message: String) = Warning(message)
    }
} 