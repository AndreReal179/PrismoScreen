package br.andre.espelhocarro

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var apps: List<Pair<String, String>> // pacote to nome
    private lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pm = packageManager
        val main = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = pm.queryIntentActivities(main, 0)
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .filter { it.first != packageName }
            .distinctBy { it.first }
            .sortedBy { it.second.lowercase() }

        val pad = (16 * resources.displayMetrics.density).toInt()

        val topo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }

        topo.addView(TextView(this).apply {
            text = "Configuração (faça uma vez):"
            textSize = 18f
        })

        topo.addView(Button(this).apply {
            text = "1) Permitir abrir apps pelo carro"
            setOnClickListener {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        })

        topo.addView(Button(this).apply {
            text = "2) Iniciar espelhamento da tela (escolha: tela inteira)"
            setOnClickListener {
                val mpm = getSystemService(MediaProjectionManager::class.java)
                startActivityForResult(mpm.createScreenCaptureIntent(), PEDIDO_CAPTURA)
            }
        })

        topo.addView(Button(this).apply {
            text = "Parar espelhamento"
            setOnClickListener {
                stopService(Intent(this@MainActivity, CaptureService::class.java))
            }
        })

        topo.addView(TextView(this).apply {
            text = "Apps que aparecem na lista do carro:"
            textSize = 16f
            setPadding(0, pad, 0, 0)
        })

        list = ListView(this)
        list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        list.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            apps.map { it.second }
        )
        val saved = AppPrefs.selected(this)
        apps.forEachIndexed { i, app ->
            if (app.first in saved) list.setItemChecked(i, true)
        }
        list.setOnItemClickListener { _, _, _, _ -> salvar() }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            fitsSystemWindows = true
        }
        root.addView(
            ScrollView(this).apply { addView(topo) },
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.45f)
        )
        root.addView(
            list,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.55f)
        )
        setContentView(root)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PEDIDO_CAPTURA && resultCode == RESULT_OK && data != null) {
            startForegroundService(
                Intent(this, CaptureService::class.java)
                    .putExtra("code", resultCode)
                    .putExtra("data", data)
            )
        }
    }

    private fun salvar() {
        val checked = list.checkedItemPositions
        val pacotes = apps.indices.filter { checked.get(it) }.map { apps[it].first }
        AppPrefs.save(this, pacotes)
    }

    companion object {
        private const val PEDIDO_CAPTURA = 100
    }
}
