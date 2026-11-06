package br.andre.espelhocarro

import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.Template
import androidx.car.app.navigation.NavigationManager
import androidx.car.app.navigation.NavigationManagerCallback
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Mostra a tela do celular na tela do carro (somente imagem, sem toque). */
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
        val sair = Action.Builder()
            .setTitle("Sair")
            .setOnClickListener { carContext.finishCarApp() }
            .build()

        return NavigationTemplate.Builder()
            .setActionStrip(ActionStrip.Builder().addAction(sair).build())
            .setBackgroundColor(CarColor.SECONDARY)
            .build()
    }
}
