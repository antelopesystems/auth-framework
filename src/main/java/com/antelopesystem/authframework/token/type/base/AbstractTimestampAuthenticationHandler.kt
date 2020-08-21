package com.antelopesystem.authframework.token.type.base

import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.authframework.token.model.TimestampTokenRequest
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Base64Utils
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.servlet.http.HttpServletRequest


abstract class AbstractTimestampAuthenticationHandler: TokenTypeHandler {

    @Autowired
    lateinit var crudHandler: CrudHandler

    abstract fun getStringToHash(token: ObjectToken, timestamp: String): String

    final override fun getTokenFromRequest(request: HttpServletRequest): ObjectToken? {
        val hashedAuthToken = request.getHeader(AUTH_TOKEN_NAME)
        val timestamp = request.getHeader(TIMESTAMP_NAME)
        val sessionId = request.getHeader(SESSIONID_NAME)

        val token = crudHandler.showBy(where {
            "sessionId" Equal sessionId
        }, ObjectToken::class.java)
                .fromCache()
                .execute()

        if(token == null || hashedAuthToken != DigestUtils.sha256Hex(getStringToHash(token, timestamp))) {
            return null
        }

        val currentTimestamp = System.currentTimeMillis()

        val threshold = 1000L * 60L * 60L * 2L

        val timestampLong = timestamp.toLongOrNull()

        if(timestampLong == null || currentTimestamp - timestampLong > threshold) {
            return null
        }

        return token
    }

    protected fun getPublicKey(publicKey: String): PublicKey {
        val keyBytes = Base64Utils.decodeFromString(publicKey)

        val spec = X509EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePublic(spec)
    }

    protected fun encrypt(payload: TimestampTokenRequest, data: String): String {
        val publicKey: PublicKey = getPublicKey(payload.publicKey)

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        return Base64Utils.encodeToString(cipher.doFinal(data.toByteArray()))
    }

    protected fun decrypt(privateKey: PrivateKey, data: String): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        return String(cipher.doFinal(Base64Utils.decodeFromString(data)))
    }

    override fun isTokenPresent(request: HttpServletRequest): Boolean {
        return request.getHeader(TIMESTAMP_NAME) != null && request.getHeader(AUTH_TOKEN_NAME) != null && request.getHeader(SESSIONID_NAME) != null
    }

    companion object {
        const val AUTH_TOKEN_NAME = "x-auth-token"
        const val TIMESTAMP_NAME = "x-auth-timestamp"
        const val SESSIONID_NAME = "x-auth-session-id"
    }
}