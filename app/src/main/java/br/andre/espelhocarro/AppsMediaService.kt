package br.andre.espelhocarro

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

/**
 * App de mídia "de mentira": a lista de apps aparece como itens de música no Android Auto.
 * Tocar em um item abre o app correspondente no celular.
 */
class AppsMediaService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        session = MediaSessionCompat(this, "EspelhoCarro").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                    if (mediaId != null) abrir(mediaId)
                }
            })
            setPlaybackState(estado(PlaybackStateCompat.STATE_PAUSED))
            isActive = true
        }
        sessionToken = session.sessionToken
    }

    override fun onDestroy() {
        session.release()
        super.onDestroy()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = BrowserRoot(RAIZ, null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val pm = packageManager
        val itens = mutableListOf<MediaBrowserCompat.MediaItem>()
        if (parentId == RAIZ) {
            for (pacote in AppPrefs.selected(this)) {
                try {
                    val info = pm.getApplicationInfo(pacote, 0)
                    val desc = MediaDescriptionCompat.Builder()
                        .setMediaId(pacote)
                        .setTitle(pm.getApplicationLabel(info).toString())
                        .setSubtitle("Toque para abrir no celular")
                        .setIconBitmap(icone(pm.getApplicationIcon(info)))
                        .build()
                    itens.add(
                        MediaBrowserCompat.MediaItem(
                            desc,
                            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                        )
                    )
                } catch (e: Exception) {
                    // app desinstalado: ignora
                }
            }
        }
        result.sendResult(itens)
    }

    private fun abrir(pacote: String) {
        val pm = packageManager
        val nome = try {
            pm.getApplicationLabel(pm.getApplicationInfo(pacote, 0)).toString()
        } catch (e: Exception) {
            pacote
        }

        val intent = pm.getLaunchIntentForPackage(pacote)
        if (intent == null) {
            erro("$nome não encontrado no celular")
            return
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
            session.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Abrindo $nome no celular")
                    .build()
            )
            session.setPlaybackState(estado(PlaybackStateCompat.STATE_PAUSED))
        } catch (e: Exception) {
            erro("Não consegui abrir $nome: ${e.message}")
        }
    }

    private fun erro(msg: String) {
        session.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(ACOES)
                .setState(PlaybackStateCompat.STATE_ERROR, 0, 0f)
                .setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR, msg)
                .build()
        )
    }

    private fun estado(state: Int): PlaybackStateCompat =
        PlaybackStateCompat.Builder()
            .setActions(ACOES)
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
            .build()

    private fun icone(d: Drawable): Bitmap {
        val b = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        d.setBounds(0, 0, 128, 128)
        d.draw(c)
        return b
    }

    companion object {
        private const val RAIZ = "raiz"
        private const val ACOES = PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }
}
