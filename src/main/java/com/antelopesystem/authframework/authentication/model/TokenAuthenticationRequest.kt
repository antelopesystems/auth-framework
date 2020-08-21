package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.token.model.ObjectToken

data class TokenAuthenticationRequest(val token: ObjectToken)