package lava.sdk.api

import kotlinx.serialization.Serializable

@Serializable
enum class Protocol { HTTP, HTTPS, HTTP3 }
