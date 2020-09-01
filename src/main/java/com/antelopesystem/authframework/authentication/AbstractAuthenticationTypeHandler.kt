package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.controller.AuthenticationPayload

abstract class GenericPayloadWrapper(val payload: AuthenticationPayload) {
}

abstract class AbstractAuthenticationTypeHandler: AuthenticationTypeHandler {
}