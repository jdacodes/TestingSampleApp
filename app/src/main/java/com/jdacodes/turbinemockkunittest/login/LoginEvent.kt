package com.jdacodes.turbinemockkunittest.login

sealed class LoginEvent {
    data class OnNameChange(val newValue: String) : LoginEvent()
    data class OnPasswordChange(val newValue: String) : LoginEvent()

    object OnLoginClick : LoginEvent()
}