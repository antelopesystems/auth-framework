package com.antelopesystem.authframework.auth

import com.antelopesystem.authframework.auth.exception.ExtendedTotpRequiredException
import com.antelopesystem.authframework.auth.exception.InvalidTokenException
import com.antelopesystem.authframework.auth.model.ObjectToken
import com.antelopesystem.authframework.auth.model.TokenRequest
import com.antelopesystem.authframework.auth.model.TokenResponse
import com.antelopesystem.authframework.auth.type.base.TokenTypeHandler
import com.antelopesystem.authframework.auth.type.enums.TokenType
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import net.sf.ehcache.Cache
import net.sf.ehcache.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

@Component("authenticationHandler")
class OperatorAuthenticationHandlerImpl : OperatorAuthenticationHandler {

    @ComponentMap(key = TokenType::class, value = TokenTypeHandler::class)
    private lateinit var authenticationTypeHandlers: Map<TokenType, TokenTypeHandler>

    @Autowired
    private lateinit var authenticationDao: AuthenticationDao

    @Autowired
    private lateinit var crudHandler: CrudHandler

    @Autowired
    private lateinit var request: HttpServletRequest

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private val extendedValidationEvictionTimer: Timer = Timer()

    private val extendedValidationTokenList: MutableList<Long> = ArrayList()

    @Resource(name = "extendedTotpValidationCache")
    private lateinit var extendedTotpValidationCache: Cache


    override fun getToken(token: String, objectType: String): ObjectToken {
        return crudHandler.showBy(where {
            "token" Equal token
            "objectType" Equal objectType

        }, ObjectToken::class.java)
                .fromCache()
                .execute() ?: throw InvalidTokenException()
    }

    override fun getTokenFromRequest(request: HttpServletRequest): ObjectToken {
        val typeHandler = getAuthenticationTypeHandler(request)
        val token = typeHandler.getTokenFromRequest(request) ?: throw InvalidTokenException()
        return token
    }

    override fun isTokenExtendedValidationActive(token: ObjectToken): Boolean {
        return extendedValidationTokenList.contains(token.id)
    }

    override fun addTokenForExtendedValidation(token: ObjectToken) {
        val extendedValidatinDuration: Long = (120).toLong() * 1000
        extendedValidationTokenList += token.id
        extendedValidationEvictionTimer.schedule(extendedValidatinDuration) {
            extendedValidationTokenList.remove(token.id)
        }

        val totpRequestId = request.getHeader("x-totp-request-id")
        if (totpRequestId != null) {
            val element = extendedTotpValidationCache.get(totpRequestId)
            if (element != null && element.objectValue == false) {
                extendedTotpValidationCache.put(Element(totpRequestId, true))
            }
        }
    }

    override fun getCurrentToken(): ObjectToken = getTokenFromRequest(request!!)

    override fun deleteCurrentToken() {
        try {
            val token = getCurrentToken()
            if(token.immutable) return
            crudHandler.delete(token.id, ObjectToken::class.java).execute()

        } catch(e: Exception) {
            when(e) {
                is InvalidTokenException -> {}
                else -> throw e
            }
        }
    }

    @Transactional(readOnly = true)
    override fun deleteOldTokens() {
        val authTokenExpiry = 12L
        val toCreationTime = System.currentTimeMillis() - 1000L * 60L * 60L * authTokenExpiry
        authenticationDao.deleteOldTokens(toCreationTime)
    }

    override fun <T : TokenRequest> generateToken(payload: T): TokenResponse {
        val typeHandler = authenticationTypeHandlers[payload.type]

        val objectToken = ObjectToken(
                tokenType = payload.type,
                objectId = payload.objectId,
                objectType = payload.objectType,
                originalObjectId = payload.originalObjectId,
                ip = payload.ip,
                passwordChangeRequired = payload.passwordChangeRequired,
                totpApproved = payload.totpApproved
        )

        val processedToken = typeHandler!!.generateToken(objectToken, payload)

        crudHandler.create(objectToken)
                .execute()

        return TokenResponse(processedToken, objectToken.sessionId, payload.type)
    }

    override fun deleteAllTokensById(id: Long, objectType: String) {
        val tokens = crudHandler.index(where {
            "objectId" Equal id
            "objectType" Equal objectType
            "immutable" Equal false
        }, ObjectToken::class.java).execute()

        for (token in tokens.data) {
            crudHandler.delete(token.id, ObjectToken::class.java).execute()
        }
    }

    override fun isCurrentTokenPresent(): Boolean {
        if (RequestContextHolder.getRequestAttributes() != null) {
            return isTokenPresent(request)
        }

        return false
    }

    override fun getOriginalOperatorFromCurrentToken(): Long {
        return if (isCurrentTokenPresent()) getCurrentToken().originalObjectId else 0
    }

    override fun isTokenPresent(request: HttpServletRequest) : Boolean {
        val typeHandler = getAuthenticationTypeHandler(request)

        return typeHandler.isTokenPresent(request)
    }

    override fun requireTotp(): Boolean {
//        if (!parameterHandler.getBooleanParameter("totpActive", mainProperties.isTotpActiveDefault)) {
//            return true
//        }

        if (!isTokenPresent(request!!)) {
            return false
        }

        val token = getCurrentToken()
        if (!isTokenExtendedValidationActive(token)) {
            throw ExtendedTotpRequiredException()
        }

        return true
    }

    override fun requireTotpAlways(): Boolean {
//        if (!parameterHandler.getBooleanParameter("totpActive", mainProperties.isTotpActiveDefault)) {
//            return true
//        }

        if (!isTokenPresent(request!!)) {
            return false
        }

        val s = "x-totp-request-id"
        val totpRequestId = request!!.getHeader(s)

        val element = extendedTotpValidationCache.get(totpRequestId)
        if (element != null) {
            extendedTotpValidationCache.remove(element.objectKey)
            if (element.objectValue == true) {
                return true
            }
        }

        val uuid = UUID.randomUUID().toString()
        extendedTotpValidationCache.put(Element(uuid, false))
        throw ExtendedTotpRequiredException(uuid)

    }

    override fun updatePassword(operatorId: Long, newPassword: String) {
//        val operator = operatorHandler.getOperator(operatorId)
//        operator.password = encodePassword(newPassword)
//        operator.passwordSalt = null
//        operatorHandler.editOperator(operator)
    }

    override fun encodePassword(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }

    override fun isPasswordInOldEncoding(encodedPassword: String): Boolean {
        return false
//        return !compositeSha1BcryptPasswordEncoder.isInNewEncoding(encodedPassword)
    }

    override fun isPasswordValidForOperator(rawPassword: String, encodedPassword: String, passwordSalt: String?): Boolean {
        return true
//        return compositeSha1BcryptPasswordEncoder.isPasswordValidForUser(rawPassword, encodedPassword, passwordSalt)
    }

    private fun getAuthenticationTypeHandler(request: HttpServletRequest): TokenTypeHandler {
        var authType: TokenType = TokenType.Legacy

        val authTypeString = request.getHeader("x-auth-type")
        if (!authTypeString.isNullOrBlank()) {
            try {
                authType = TokenType.valueOf(authTypeString)
            } catch (e: Exception){}
        }

        return authenticationTypeHandlers.getValue(authType)
    }
}