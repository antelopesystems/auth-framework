package com.antelopesystem.authframework.authentication.loginrules

import com.antelopesystem.authframework.authentication.loginrules.action.base.UserLoginRuleActionHandler
import com.antelopesystem.authframework.authentication.loginrules.anomaly.base.UserLoginAnomalyHandler
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.filter
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserLoginValidatorImpl : UserLoginValidator {

    @Autowired
    private lateinit var crudHandler: CrudHandler

    @ComponentMap
    private lateinit var actionHandlers: Map<UserLoginRuleActionType, UserLoginRuleActionHandler>

    @ComponentMap
    private lateinit var anomalyHandlers: Map<UserLoginAnomalyType, UserLoginAnomalyHandler>

    override fun validateLogin(loginDTO: UserLoginDTO): Int {
        val score = computeLoginScore(loginDTO)
        val ruleRanges = getLoginRuleRanges()

        for ((rule, range) in ruleRanges) {
            if (range.contains(score)) {
                actionHandlers[rule.action]?.handle(loginDTO)
            }
        }

        return score
    }

    private fun computeLoginScore(loginDTO: UserLoginDTO) : Int {
        var points = 0
        for ((type, handler) in anomalyHandlers) {
            val result = handler.handle(loginDTO)
            if (result) {
                points += type.points
            }
        }

        return points
    }

    private fun getLoginRuleRanges() : Map<UserLoginRule, IntRange> {
        val loginRules = crudHandler.index(filter {
            order {
                by = "minScore"
                ascending
            }
        }, UserLoginRule::class.java)
                .fromCache()
                .execute()
                .data
        if (loginRules.isEmpty()) {
            return mutableMapOf()
        }

        val ruleRanges = mutableMapOf<UserLoginRule, IntRange>()

        for ((i, loginRule) in loginRules.withIndex()) {
            val nextThreshold = loginRules.getOrNull(i + 1)?.minScore?.minus(1) ?: Int.MAX_VALUE
            ruleRanges[loginRule] = loginRule.minScore..nextThreshold
        }

        return ruleRanges
    }
}

