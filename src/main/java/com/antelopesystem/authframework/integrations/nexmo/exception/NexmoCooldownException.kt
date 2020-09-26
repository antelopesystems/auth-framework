package com.antelopesystem.authframework.integrations.nexmo.exception

data class NexmoCooldownException(val secondsRemaining: Long) : NexmoException("Please try again in $secondsRemaining seconds")