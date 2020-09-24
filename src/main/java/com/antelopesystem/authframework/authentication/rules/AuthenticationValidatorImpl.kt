package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.rules.action.base.RuleActionHandler
import com.antelopesystem.authframework.authentication.rules.anomaly.base.DeviceAnomalyHandler
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.filter
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.stereotype.Component

@Component
class AuthenticationValidatorImpl(
        private val crudHandler: CrudHandler
) : AuthenticationValidator {

    @ComponentMap
    private lateinit var actionHandlers: Map<RuleActionType, RuleActionHandler>

    @ComponentMap
    private lateinit var anomalyHandlers: Map<DeviceAnomalyType, DeviceAnomalyHandler>

    override fun validate(entity: AuthenticatedEntity, deviceInfo: DeviceInfo): Int {
        val score = computeScore(entity, deviceInfo)
        val ruleRanges = getRuleRanges()

        for ((rule, range) in ruleRanges) {
            if (range.contains(score)) {
                actionHandlers[rule.action]?.handle(entity, deviceInfo)
            }
        }

        return score
    }

    private fun computeScore(entity: AuthenticatedEntity, deviceInfo: DeviceInfo) : Int {
        var points = 0
        for ((type, handler) in anomalyHandlers) {
            val result = handler.handle(entity, deviceInfo)
            if (result) {
                points += type.points
            }
        }

        return points
    }

    private fun getRuleRanges() : Map<AuthenticationRule, IntRange> {
        val loginRules = crudHandler.index(filter {
            order {
                by = "minScore"
                ascending
            }
        }, AuthenticationRule::class.java)
                .fromCache()
                .execute()
                .data
        if (loginRules.isEmpty()) {
            return mutableMapOf()
        }

        val ruleRanges = mutableMapOf<AuthenticationRule, IntRange>()

        for ((i, loginRule) in loginRules.withIndex()) {
            val nextThreshold = loginRules.getOrNull(i + 1)?.minScore?.minus(1) ?: Int.MAX_VALUE
            ruleRanges[loginRule] = loginRule.minScore..nextThreshold
        }

        return ruleRanges
    }
}

