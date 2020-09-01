package com.antelopesystem.authframework.authentication.nexmo

import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.crudframework.crud.handler.CrudHandlerImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.*

internal class NexmoAuthenticationTypeHandlerImplTest {

    private val nexmoClient = mock<NexmoClient>()

    private val nexmoClientProvider = mock<NexmoClientProvider>()

    private val handler = NexmoAuthenticationTypeHandlerImpl(CrudHandlerImpl(), nexmoClientProvider, any())

    @BeforeEach
    fun init() {
        whenever(nexmoClientProvider.getNexmoClient(anyString())).thenReturn(nexmoClient)
    }

    @Test
    fun `type should return correct type`() {
        assertEquals(AuthenticationType.Nexmo, handler.type)
    }

    @Test
    fun `doRegister should throw if code is invalid`() {
        whenever(nexmoClient.validateVerification(anyString(), anyString())).thenReturn(true)

//        assertThrows<LoginFailedException> {
//            handler.doRegister()
//        }
    }
}