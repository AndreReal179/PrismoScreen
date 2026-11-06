package br.andre.espelhocarro

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.view.Surface

/**
 * Liga a captura da tela do celular (MediaProjection) direto na superfície da tela do carro.
 * O VirtualDisplay é criado uma única vez por projeção (Android 14 não permite criar de novo);
 * depois só trocamos a superfície e o tamanho.
 */
object Mirror {

    private var projection: MediaProjection? = null
    private var display: VirtualDisplay? = null
    private var surface: Surface? = null

    var surfaceW = 0
        private set
    var surfaceH = 0
        private set
    private var surfaceDpi = 160

    fun start(p: MediaProjection) {
        stop()
        projection = p
        display = p.createVirtualDisplay(
            "espelho", 1280, 720, 160,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            null, null, null
        )
        aplicar()
    }

    /** Parada explícita (botão ou serviço sendo encerrado). */
    fun stop() {
        val p = projection
        projection = null
        display?.release()
        display = null
        try {
            p?.stop()
        } catch (e: Exception) {
            // já parada
        }
    }

    /** O sistema parou a projeção (usuário revogou, etc.). */
    fun released() {
        display?.release()
        display = null
        projection = null
    }

    fun ativo(): Boolean = display != null

    fun surfaceAvailable(s: Surface, w: Int, h: Int, dpi: Int) {
        surface = s
        surfaceW = w
        surfaceH = h
        surfaceDpi = if (dpi > 0) dpi else 160
        aplicar()
    }

    fun surfaceDestroyed() {
        surface = null
        try {
            display?.surface = null
        } catch (e: Exception) {
            // ignora
        }
    }

    private fun aplicar() {
        val d = display ?: return
        val s = surface ?: return
        if (surfaceW <= 0 || surfaceH <= 0) return
        try {
            d.resize(surfaceW, surfaceH, surfaceDpi)
            d.surface = s
        } catch (e: Exception) {
            // ignora
        }
    }

    /**
     * Converte um toque na tela do carro (pixels da superfície) em pixels da tela do celular.
     * A tela do celular aparece centralizada na superfície, com barras pretas nas sobras.
     */
    fun paraCelular(x: Float, y: Float, pw: Int, ph: Int): Pair<Float, Float>? {
        if (surfaceW <= 0 || surfaceH <= 0 || pw <= 0 || ph <= 0) return null
        val escala = minOf(surfaceW.toFloat() / pw, surfaceH.toFloat() / ph)
        val largura = pw * escala
        val altura = ph * escala
        val ox = (surfaceW - largura) / 2f
        val oy = (surfaceH - altura) / 2f
        val px = (x - ox) / escala
        val py = (y - oy) / escala
        if (px < 0f || py < 0f || px > pw || py > ph) return null
        return px to py
    }
}
