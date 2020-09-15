package com.antelopesystem.authframework.authentication.method.authenticator

import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator

class AuthenticatorClient(private val issuer: String) {

    private val setups: MutableMap<String, AuthenticatorSetupResponsePayload> = mutableMapOf()

    fun setup(accountName: String): AuthenticatorSetupResponsePayload {
        val authenticator = GoogleAuthenticator()
        val key = authenticator.createCredentials()
        val payload = AuthenticatorSetupResponsePayload(key.key, GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuer, accountName, key))
        setups[accountName] = payload
        return payload
    }

    fun completeSetup(accountName: String, verificationCode: Int) : AuthenticatorSetupResponsePayload {
        val payload = setups[accountName] ?: error("Request not found")

        val valid = validate(payload.key, verificationCode)
        if(!valid) {
            error("Invalid code")
        }

        return payload
    }

    fun validate(key: String, verificationCode: Int): Boolean {
        val authenticator = GoogleAuthenticator()
        return authenticator.authorize(key, verificationCode)
    }
}
data class AuthenticatorSetupResponsePayload(var key: String, var keyUrl: String)