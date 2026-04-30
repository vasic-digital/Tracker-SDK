package lava.sdk.mirror

import lava.sdk.api.MirrorUrl

/** Configuration for a single mirror group registered with a [MirrorManager]. */
data class MirrorGroup(
    val groupId: String,
    val mirrors: List<MirrorUrl>,
    val expectedMarker: String,
)
