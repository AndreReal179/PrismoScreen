package br.andre.espelhocarro

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class EspelhoCarService : CarAppService() {

    // Instalação por fora da loja: aceita qualquer host do Android Auto.
    override fun createHostValidator(): HostValidator =
        HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession(): Session = EspelhoSession()
}

class EspelhoSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen = MenuScreen(carContext)
}
