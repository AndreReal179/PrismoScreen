package br.andre.espelhocarro

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

/** Entrada "Espelho Tela" no Android Auto: mostra a tela do celular e aceita toque. */
class EspelhoNavService : CarAppService() {

    // Instalação por fora da loja: aceita qualquer host do Android Auto.
    override fun createHostValidator(): HostValidator =
        HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession(): Session = EspelhoNavSession()
}

class EspelhoNavSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen = MirrorScreen(carContext)
}
