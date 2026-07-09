package com.ddeeaaddllyy.zenith.domain.model

sealed interface LoginResult {
    data object Success : LoginResult
    data object InvalidCredentials : LoginResult
    data object NoAccount : LoginResult
}
