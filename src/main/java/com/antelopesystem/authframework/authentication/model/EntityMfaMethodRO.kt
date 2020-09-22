package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import javax.persistence.*

class EntityMfaMethodRO(
        var entityId: Long? = null,
        var entityType: String? = null,
        var type: MfaType? = null,
        var param1: String? = "",
        var param2: String? = "",
        var param3: String? = "",
        var param4: String? = "",
        var param5: String? = ""
) : BaseJpaUpdatebleEntity()