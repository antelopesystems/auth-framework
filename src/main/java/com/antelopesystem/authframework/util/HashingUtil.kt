package com.antelopesystem.authframework.util

import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest

fun hashSHA256(source: String): String {
    return hash(source, "sha-256")
}

private fun hash(source: String, algorithm: String?): String {
    val digest = MessageDigest.getInstance(algorithm)
    digest.update(source.toByteArray())
    return String(Hex.encodeHex(digest.digest()))
}