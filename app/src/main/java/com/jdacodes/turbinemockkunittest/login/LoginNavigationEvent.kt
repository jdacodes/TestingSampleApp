package com.jdacodes.turbinemockkunittest.login

sealed class LoginNavigationEvent {
    object NavigateToHome: LoginNavigationEvent()
}