package com.antelopesystem.authframework.util

import java.util.*

fun UUID.cleanUuid() = this.toString().replace("-", "").toUpperCase()