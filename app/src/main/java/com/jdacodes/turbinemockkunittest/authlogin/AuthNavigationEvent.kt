package com.jdacodes.turbinemockkunittest.authlogin

sealed class AuthNavigationEvent {
    object NavigateToProfile: AuthNavigationEvent()
}