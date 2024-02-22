package com.jdacodes.turbinemockkunittest.login

import com.jdacodes.turbinemockkunittest.base.ButtonState

data class LoginState(
    val username: String = "",
    val password: String = "",
    val continueButtonState: ButtonState = ButtonState.Active
)
