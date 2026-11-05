package br.andre.espelhocarro

import android.content.Intent
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

class MenuScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val pm = carContext.packageManager
        val limite = carContext.getCarService(ConstraintManager::class.java)
            .getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_LIST)

        val itens = ItemList.Builder()
        var total = 0
        for (pacote in AppPrefs.selected(carContext)) {
            if (total >= limite) break
            val nome = try {
                pm.getApplicationLabel(pm.getApplicationInfo(pacote, 0)).toString()
            } catch (e: Exception) {
                continue
            }
            itens.addItem(
                Row.Builder()
                    .setTitle(nome)
                    .setOnClickListener { abrir(pacote) }
                    .build()
            )
            total++
        }
        if (total == 0) {
            itens.setNoItemsMessage("Abra o app Espelho Carro no celular e marque os apps.")
        }

        return ListTemplate.Builder()
            .setSingleList(itens.build())
            .setTitle("Meus apps")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }

    private fun abrir(pacote: String) {
        val intent = carContext.packageManager.getLaunchIntentForPackage(pacote)
        if (intent == null) {
            aviso("App não encontrado no celular")
            return
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            carContext.startActivity(intent)
            aviso("Abrindo no celular...")
        } catch (e: Exception) {
            aviso("Não consegui abrir: ${e.message}")
        }
    }

    private fun aviso(msg: String) {
        CarToast.makeText(carContext, msg, CarToast.LENGTH_SHORT).show()
    }
}
