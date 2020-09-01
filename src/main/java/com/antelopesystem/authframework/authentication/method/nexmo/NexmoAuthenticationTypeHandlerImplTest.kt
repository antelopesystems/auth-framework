package com.antelopesystem.authframework.authentication.method.nexmo

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.crudframework.crud.handler.CrudHandlerImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*

internal class NexmoAuthenticationTypeHandlerImplTest {

    private val nexmoClient = mock<NexmoClient>()

    private val nexmoClientProvider = mock<NexmoClientProvider>()

    private val handler = NexmoAuthenticationMethodHandlerImpl(CrudHandlerImpl(), nexmoClientProvider, any())

    @BeforeEach
    fun init() {
        whenever(nexmoClientProvider.getNexmoClient(anyString())).thenReturn(nexmoClient)
    }

    @Test
    fun `type should return correct type`() {
        assertEquals(AuthenticationMethod.Nexmo, handler.method)
    }

    @Test
    fun `doRegister should throw if code is invalid`() {
        whenever(nexmoClient.validateVerification(anyString(), anyString())).thenReturn(true)

//        assertThrows<LoginFailedException> {
//            handler.doRegister()
//        }
    }
}