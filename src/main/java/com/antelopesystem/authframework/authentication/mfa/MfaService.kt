package com.antelopesystem.authframework.authentication.mfa

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.model.UserInfo

interface MfaService {
    fun setup(mfaType: MfaType, payload: MethodRequestPayload, userInfo: UserInfo): CustomParamsDTO
    fun activate(mfaType: MfaType, code: String, userInfo: UserInfo)
    fun deactivate(mfaType: MfaType, userInfo: UserInfo)
    fun issue(mfaType: MfaType, userInfo: UserInfo)
    fun validateToCurrentToken(mfaType: MfaType, code: String, userInfo: UserInfo)
    fun getAvailableProviders(userInfo: UserInfo): List<ProviderDTO>
    fun getEnabledProviders(userInfo: UserInfo): List<MfaType>
}