package app.vitune.providers.innertube.requests

import app.vitune.providers.innertube.Innertube
import app.vitune.providers.innertube.models.BrowseResponse
import app.vitune.providers.innertube.models.Context
import app.vitune.providers.innertube.models.MusicCarouselShelfRenderer
import app.vitune.providers.innertube.models.MusicShelfRenderer
import app.vitune.providers.innertube.models.bodies.BrowseBody
import app.vitune.providers.innertube.utils.findSectionByTitle
import app.vitune.providers.innertube.utils.from
import app.vitune.providers.utils.runCatchingCancellable
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext

suspend fun Innertube.artistPage(body: BrowseBody) = runCatchingCancellable {
    val ctx = currentCoroutineContext()
    val response = client.post(BROWSE) {
        setBody(body)
        mask("contents,header")
    }.body<BrowseResponse>()

    val responseNoLang by lazy {
        CoroutineScope(ctx).async(start = CoroutineStart.LAZY) {
            client.post(BROWSE) {
                setBody(body.copy(context = Context.DefaultWebNoLang))
                mask("contents,header")
            }.body<BrowseResponse>()
        }
    }

    suspend fun findSectionByTitle(text: String) = response
        .contents
        ?.singleColumnBrowseResultsRenderer
        ?.tabs
        ?.get(0)
        ?.tabRenderer
        ?.content
        ?.sectionListRenderer
        ?.findSectionByTitle(text) ?: responseNoLang.await()
        .contents
        ?.singleColumnBrowseResultsRenderer
        ?.tabs
        ?.get(0)
        ?.tabRenderer
        ?.content
        ?.sectionListRenderer
        ?.findSectionByTitle(text)

    val songsSection = findSectionByTitle("Songs")?.musicShelfRenderer
    val albumsSection = findSectionByTitle("Albums")?.musicCarouselShelfRenderer
    val singlesSection = (findSectionByTitle("Singles & EPs") ?: findSectionByTitle("Singles"))
        ?.musicCarouselShelfRenderer

    Innertube.ArtistPage(
        name = response
            .header
            ?.musicImmersiveHeaderRenderer
            ?.title
            ?.text,
        description = response
            .header
            ?.musicImmersiveHeaderRenderer
            ?.description
            ?.text,
        thumbnail = (
            response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.foregroundThumbnail
                ?: response
                    .header
                    ?.musicImmersiveHeaderRenderer
                    ?.thumbnail
            )
            ?.musicThumbnailRenderer
            ?.thumbnail
            ?.thumbnails
            ?.getOrNull(0),
        shuffleEndpoint = response
            .header
            ?.musicImmersiveHeaderRenderer
            ?.playButton
            ?.buttonRenderer
            ?.navigationEndpoint
            ?.watchEndpoint,
        radioEndpoint = response
            .header
            ?.musicImmersiveHeaderRenderer
            ?.startRadioButton
            ?.buttonRenderer
            ?.navigationEndpoint
            ?.watchEndpoint,
        songs = songsSection
            ?.contents
            ?.mapNotNull(MusicShelfRenderer.Content::musicResponsiveListItemRenderer)
            ?.mapNotNull(Innertube.SongItem::from),
        songsEndpoint = songsSection
            ?.bottomEndpoint
            ?.browseEndpoint,
        albums = albumsSection
            ?.contents
            ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
            ?.mapNotNull(Innertube.AlbumItem::from),
        albumsEndpoint = albumsSection
            ?.header
            ?.musicCarouselShelfBasicHeaderRenderer
            ?.moreContentButton
            ?.buttonRenderer
            ?.navigationEndpoint
            ?.browseEndpoint,
        singles = singlesSection
            ?.contents
            ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
            ?.mapNotNull(Innertube.AlbumItem::from),
        singlesEndpoint = singlesSection
            ?.header
            ?.musicCarouselShelfBasicHeaderRenderer
            ?.moreContentButton
            ?.buttonRenderer
            ?.navigationEndpoint
            ?.browseEndpoint,
        subscribersCountText = response
            .header
            ?.musicImmersiveHeaderRenderer
            ?.subscriptionButton
            ?.subscribeButtonRenderer
            ?.subscriberCountText
            ?.text
    )
}
