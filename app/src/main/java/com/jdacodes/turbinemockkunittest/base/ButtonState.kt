package com.jdacodes.turbinemockkunittest.base

sealed class ButtonState {
    object Active: ButtonState()

    object Disabled: ButtonState()

    object Loading: ButtonState()
}