//package com.antelopesystem.authframework.authentication
//
//import com.antelopesystem.authframework.authentication.enums.AuthenticationType
//import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
//import com.antelopesystem.authframework.authentication.usernamepassword.UsernamePasswordTypeHandlerImpl
//import com.antelopesystem.authframework.controller.AuthenticationPayload
//import com.antelopesystem.authframework.settings.SecuritySettingsHandlerImpl
//import com.antelopesystem.authframework.token.type.enums.TokenType
//import com.antelopesystem.crudframework.crud.handler.CrudHandlerImpl
//import com.google.gson.Gson
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertAll
//import org.junit.jupiter.api.assertThrows
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//
//internal class UsernamePasswordTypeHandlerImplTest {
//
//    @Test
//    fun `should return correct type`() {
//        val handler = UsernamePasswordTypeHandlerImpl(CrudHandlerImpl(), SecuritySettingsHandlerImpl(CrudHandlerImpl()))
//        assertEquals(AuthenticationType.UsernamePassword, handler.type)
//    }
//
//    @Test
//    fun `doLogin should throw LoginFailedException when password is not correct`() {
//        val handler = UsernamePasswordTypeHandlerImpl(CrudHandlerImpl(), SecuritySettingsHandlerImpl(CrudHandlerImpl()))
//        assertThrows<LoginFailedException> {
//            handler.doLogin(getMockAuthenticationPayload(), getMockAuthenticatedEntity(password = "Test2"))
//        }
//    }
//
//    @Test
//    fun `doLogin should pass when password is correct`() {
//        val handler = UsernamePasswordTypeHandlerImpl(CrudHandlerImpl(), SecuritySettingsHandlerImpl(CrudHandlerImpl()))
//        handler.doLogin(getMockAuthenticationPayload(), getMockAuthenticatedEntity())
//    }
//
//    @Test
//    fun `doRegister should return correct AuthenticatedEntity`() {
//        val handler = UsernamePasswordTypeHandlerImpl(CrudHandlerImpl(), SecuritySettingsHandlerImpl(CrudHandlerImpl()))
//        val payload = getMockAuthenticationPayload()
//        val entity = handler.doRegister(payload, AuthenticatedEntity())
//
//        assertAll("entity",
//                { assertEquals(payload.bodyMap["username"], entity.username) },
//                { assertTrue(passwordEncoder.matches(payload.bodyMap["password"] as CharSequence?, entity.password)) },
//                { assertEquals(payload.type, entity.type) }
//        )
//    }
//
//    private fun getMockAuthenticationPayload(username: String = "Test", password: String = "Test") : AuthenticationPayload {
//        return AuthenticationPayload("User", AuthenticationType.UsernamePassword, emptyMap(), gson.toJson(mapOf("username" to username, "password" to password)), TokenType.Legacy)
//    }
//
//    private fun getMockAuthenticatedEntity(username: String = "Test", password: String = "Test"): AuthenticatedEntity {
//        return AuthenticatedEntity(
//                username,
//                passwordEncoder.encode(password),
//                "User"
//        )
//    }
//
//    companion object {
//        val gson = Gson()
//        val passwordEncoder = BCryptPasswordEncoder()
//    }
//}