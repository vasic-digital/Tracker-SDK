package lava.sdk.api

import kotlinx.serialization.Serializable

/**
 * A single mirror endpoint URL with metadata. Lower [priority] = higher precedence.
 */
@Serializable
data class MirrorUrl(
    val url: String,
    val isPrimary: Boolean = false,
    val priority: Int = 0,
    val protocol: Protocol = Protocol.HTTPS,
    val region: String? = null,
)
