package com.antelopesystem.authframework.authentication.log

import com.antelopesystem.crudframework.jpa.model.BaseJpaEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table

enum class AuthenticationLogAction {
    Registration, Login, InitializeForgotPassword, CompleteForgotPassword
}

@Entity
@Table
data class ActionLog(
        var entityId: Long = 0L,
        var methodId: Long = 0L,
        var deviceHash: String = "",
        @get:Enumerated(EnumType.STRING)
        var action: AuthenticationLogAction? = null,
        var success: Boolean = false,
        var error: String = ""
): BaseJpaEntity() {
}

