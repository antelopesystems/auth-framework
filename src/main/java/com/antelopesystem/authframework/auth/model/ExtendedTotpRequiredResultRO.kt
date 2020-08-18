package com.antelopesystem.authframework.auth.model

import com.antelopesystem.crudframework.web.ro.ResultRO
import com.fasterxml.jackson.annotation.JsonInclude

class ExtendedTotpRequiredResultRO : ResultRO<Nothing>() {
    val extendedTotpRequired = true

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var requestId: String? = null
}