package lava.sdk.api

class MirrorUnavailableException(
    val tried: List<MirrorUrl>,
    cause: Throwable? = null,
) : Exception("MirrorUnavailable: ${tried.size} mirror(s) attempted", cause)
