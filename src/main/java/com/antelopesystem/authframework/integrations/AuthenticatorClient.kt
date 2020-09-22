package com.antelopesystem.authframework.integrations

import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator

class AuthenticatorClient(private val issuer: String) {

    fun setup(accountName: String): AuthenticatorSetupResponsePayload {
        val authenticator = GoogleAuthenticator()
        val key = authenticator.createCredentials()
        val payload = AuthenticatorSetupResponsePayload(key.key, GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuer, accountName, key))
        return payload
    }

    fun validate(key: String, verificationCode: Int): Boolean {
        val authenticator = GoogleAuthenticator()
        return authenticator.authorize(key, verificationCode)
    }

    data class AuthenticatorSetupResponsePayload(var key: String, var keyUrl: String)
}
