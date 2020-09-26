package com.antelopesystem.authframework.integrations.nexmo

import com.antelopesystem.authframework.integrations.nexmo.exception.NexmoCooldownException
import com.antelopesystem.authframework.integrations.nexmo.exception.NexmoGeneralException
import com.antelopesystem.authframework.integrations.nexmo.exception.NexmoRequestNotFoundException
import com.nexmo.client.NexmoClient
import com.nexmo.client.verify.VerifyRequest
import com.nexmo.client.verify.VerifyStatus
import java.util.*

class NexmoClient(apiKey: String, apiSecret: String, private val brand: String) {
    private val client = NexmoClient.Builder()
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .build()

    private val numberRequestIds: MutableMap<String, NumberRequestDTO> = WeakHashMap()

    fun requestVerification(number: String): Long {
        throwIfExistingRequest(number)
        val request = VerifyRequest(number, brand)
        request.pinExpiry = PIN_EXPIRY
        request.nextEventWait = NEXT_EVENT_WAIT
        request.workflow = VerifyRequest.Workflow.SMS_TTS
        val response = client.verifyClient.verify(request)
        if(response.status == VerifyStatus.ALREADY_REQUESTED) {
            // In case we have a pending request that we don't know about - add it as a new request
            numberRequestIds[number] = NumberRequestDTO(response.requestId)
            throwIfExistingRequest(number)
        }

        if(response.status != VerifyStatus.OK) {
            throw NexmoGeneralException(response.errorText)
        }

        numberRequestIds[number] = NumberRequestDTO(response.requestId)
        return EFFECTIVE_COOLDOWN.toLong()
    }

    fun cancelVerification(number: String) {
        val request = getOrThrowIfNoRequest(number)
        client.verifyClient.cancelVerification(request.requestId)
        numberRequestIds.remove(number)
    }

    fun validateVerification(number: String, code: String): Boolean {
        val request = getOrThrowIfNoRequest(number)
        val response = client.verifyClient.check(request.requestId, code)

        if(response.status == VerifyStatus.OK) {
            numberRequestIds.remove(number)
            return true
        }
        return false
    }

    private fun getOrThrowIfNoRequest(number: String): NumberRequestDTO {
        val request = numberRequestIds[number]
        val now = System.currentTimeMillis()
        if(request == null) {
            throw NexmoRequestNotFoundException()
        } else if(now - request.timeOfRequest > EFFECTIVE_COOLDOWN * 1000) {
            numberRequestIds.remove(number)
            throw NexmoRequestNotFoundException()
        }
        return request
    }

    private fun throwIfExistingRequest(number: String) {
        val request = numberRequestIds[number]
        if(request != null) {
            val now = System.currentTimeMillis()
            if(now - request.timeOfRequest < EFFECTIVE_COOLDOWN * 1000) {
                throw NexmoCooldownException(EFFECTIVE_COOLDOWN - ((now - request.timeOfRequest) / 1000))
            } else {
                numberRequestIds.remove(number)
            }
        }
    }

    companion object {
        private const val PIN_EXPIRY = 60
        private const val NEXT_EVENT_WAIT = PIN_EXPIRY
        private const val EFFECTIVE_COOLDOWN = PIN_EXPIRY + NEXT_EVENT_WAIT
    }
}

