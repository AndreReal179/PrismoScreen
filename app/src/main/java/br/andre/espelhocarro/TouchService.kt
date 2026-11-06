package br.andre.espelhocarro

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

/** Recebe toques vindos da tela do carro e executa no celular. */
class TouchService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    @Suppress("DEPRECATION")
    fun tamanhoTela(): Pair<Int, Int> {
        val dm = DisplayMetrics()
        (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(dm)
        return dm.widthPixels to dm.heightPixels
    }

    fun tocar(x: Float, y: Float) {
        val caminho = Path().apply { moveTo(x, y) }
        gesto(caminho, 50)
    }

    fun deslizar(x1: Float, y1: Float, x2: Float, y2: Float, duracaoMs: Long) {
        val caminho = Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
        }
        gesto(caminho, duracaoMs)
    }

    private fun gesto(caminho: Path, duracaoMs: Long) {
        val g = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(caminho, 0, duracaoMs))
            .build()
        dispatchGesture(g, null, null)
    }

    companion object {
        @Volatile
        var instance: TouchService? = null
    }
}
