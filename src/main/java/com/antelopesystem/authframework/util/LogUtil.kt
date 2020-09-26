package com.antelopesystem.authframework.util

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import org.slf4j.Logger


fun Logger.trace(supplier: () -> String) = this.trace(null, supplier)

fun Logger.trace(ex: Throwable? = null, supplier: () -> String) {
    if(this.isTraceEnabled) {
        if(ex != null) {
            this.trace(supplier(), ex)
        } else {
            this.trace(supplier())
        }
    }
}

fun Logger.debug(supplier: () -> String) = this.debug(null, supplier)

fun Logger.debug(ex: Throwable? = null, supplier: () -> String) {
    if(this.isDebugEnabled) {
        if(ex != null) {
            this.debug(supplier(), ex)
        } else {
            this.debug(supplier())
        }
    }
}

fun Logger.info(supplier: () -> String) = this.info(null, supplier)

fun Logger.info(ex: Throwable? = null, supplier: () -> String) {
    if(this.isInfoEnabled) {
        if(ex != null) {
            this.info(supplier(), ex)
        } else {
            this.info(supplier())
        }
    }
}

fun Logger.warn(supplier: () -> String) = this.warn(null, supplier)

fun Logger.warn(ex: Throwable? = null, supplier: () -> String) {
    if(this.isWarnEnabled) {
        if(ex != null) {
            this.warn(supplier(), ex)
        } else {
            this.warn(supplier())
        }
    }
}

fun Logger.error(supplier: () -> String) = this.error(null, supplier)

fun Logger.error(ex: Throwable? = null, supplier: () -> String) {
    if(this.isErrorEnabled) {
        if(ex != null) {
            this.error(supplier(), ex)
        } else {
            this.error(supplier())
        }
    }
}


fun AuthenticatedEntity.forLog() = "[ Entity ID: ${this.id}, Entity Type: ${this.type} ]"

fun EntityAuthenticationMethod.forLog() = "[ Method ID: ${this.id}, Method Type: ${this.method}, Entity: ${this.entity.forLog()} ]"