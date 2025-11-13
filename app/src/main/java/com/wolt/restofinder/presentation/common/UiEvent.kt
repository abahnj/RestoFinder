package com.wolt.restofinder.presentation.common

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}
