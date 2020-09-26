package com.antelopesystem.authframework.integrations.nexmo.exception

import com.antelopesystem.authframework.integrations.nexmo.exception.NexmoException

data class NexmoGeneralException(override val message: String) : NexmoException(message)