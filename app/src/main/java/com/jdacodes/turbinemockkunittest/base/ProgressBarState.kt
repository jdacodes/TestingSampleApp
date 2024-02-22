package com.jdacodes.turbinemockkunittest.base

sealed class ProgressBarState {
    object Loading: ProgressBarState()

    object Gone: ProgressBarState()
}