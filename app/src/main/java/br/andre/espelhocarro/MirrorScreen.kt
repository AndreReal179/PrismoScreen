package br.andre.espelhocarro

import android.accessibilityservice.AccessibilityService
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.NavigationManager
import androidx.car.app.navigation.NavigationManagerCallback
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.PanModeListener
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.hypot

class MirrorScreen(carContext: CarContext) : Screen(carContext) {

    private val surfaceCallback = object : SurfaceCallback {
        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            val s = surfaceContainer.surface ?: return
            Mirror.surfaceAvailable(
                s,
                surfaceContainer.width,
                surfaceContainer.height,
                surfaceContainer.dpi
            )
        }

        override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
            Mirror.surfaceDestroyed()
        }

        override fun onClick(x: Float, y: Float) {
            val s = servicoToque() ?: return
            val (pw, ph) = s.tamanhoTela()
            val ponto = Mirror.paraCelular(x, y, pw, ph) ?: return
            s.tocar(ponto.first, ponto.second)
        }

        // Só chega aqui no "modo mover" (botão de mão no mapa): vira um deslize no celular.
        override fun onFling(velocityX: Float, velocityY: Float) {
            val s = servicoToque() ?: return
            val norma = hypot(velocityX, velocityY)
            if (norma < 1f) return
            val (pw, ph) = s.tamanhoTela()
            val comprimento = ph * 0.35f
            val cx = pw / 2f
            val cy = ph / 2f
            val x2 = (cx + velocityX / norma * comprimento).coerceIn(0f, pw.toFloat())
            val y2 = (cy + velocityY / norma * comprimento).coerceIn(0f, ph.toFloat())
            s.deslizar(cx, cy, x2, y2, 250)
        }
    }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                carContext.getCarService(AppManager::class.java)
                    .setSurfaceCallback(surfaceCallback)

                val nav = carContext.getCarService(NavigationManager::class.java)
                nav.setNavigationManagerCallback(object : NavigationManagerCallback {
                    override fun onStopNavigation() {
                        carContext.finishCarApp()
                    }

                    override fun onAutoDriveEnabled() {}
                })
                nav.navigationStarted()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                try {
                    carContext.getCarService(NavigationManager::class.java).navigationEnded()
                } catch (e: Exception) {
                    // ignora
                }
                carContext.getCarService(AppManager::class.java).setSurfaceCallback(null)
                Mirror.surfaceDestroyed()
            }
        })
    }

    override fun onGetTemplate(): Template {
        val voltar = Action.Builder()
            .setIcon(icone(R.drawable.ic_back))
            .setOnClickListener {
                servicoToque()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            }
            .build()

        val inicio = Action.Builder()
            .setIcon(icone(R.drawable.ic_home))
            .setOnClickListener {
                servicoToque()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            }
            .build()

        val sair = Action.Builder()
            .setTitle("Sair")
            .setOnClickListener { carContext.finishCarApp() }
            .build()

        return NavigationTemplate.Builder()
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(voltar)
                    .addAction(inicio)
                    .addAction(sair)
                    .build()
            )
            .setMapActionStrip(ActionStrip.Builder().addAction(Action.PAN).build())
            .setPanModeListener(PanModeListener { })
            .setBackgroundColor(CarColor.SECONDARY)
            .build()
    }

    private fun icone(res: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(carContext, res)).build()

    private fun servicoToque(): TouchService? {
        val s = TouchService.instance
        if (s == null) {
            CarToast.makeText(
                carContext,
                "Ative o serviço de toque nas configurações de Acessibilidade do celular",
                CarToast.LENGTH_LONG
            ).show()
        }
        return s
    }
}
