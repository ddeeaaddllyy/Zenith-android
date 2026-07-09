package com.ddeeaaddllyy.zenith.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Local-only salted SHA-256 hashing. Good enough while Nedovolen has no real backend yet —
 * plaintext passwords are never persisted, only salt + hash.
 */
object PasswordHasher {

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(Base64.decode(salt, Base64.NO_WRAP))
        val hashed = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashed, Base64.NO_WRAP)
    }
}
