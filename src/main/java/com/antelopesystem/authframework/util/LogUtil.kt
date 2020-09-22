package com.antelopesystem.authframework.util

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import org.slf4j.Logger


fun Logger.trace(supplier: () -> String) {
    if(this.isTraceEnabled) {
        this.trace(supplier())
    }
}

fun Logger.debug(supplier: () -> String) {
    if(this.isDebugEnabled) {
        this.debug(supplier())
    }
}

fun AuthenticatedEntity.forLog() = "[ Entity ID: ${this.id}, Entity Type: ${this.type} ]"

fun EntityAuthenticationMethod.forLog() = "[ Method ID: ${this.id}, Method Type: ${this.method}, Entity: ${this.entity.forLog()} ]"