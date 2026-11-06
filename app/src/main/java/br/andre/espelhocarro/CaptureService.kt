package br.andre.espelhocarro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class CaptureService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val canal = "espelho"
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(canal, "Espelhamento", NotificationManager.IMPORTANCE_LOW)
        )
        val notif = Notification.Builder(this, canal)
            .setContentTitle("Espelhando a tela no carro")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()

        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(1, notif)
        }

        val codigo = intent?.getIntExtra("code", 0) ?: 0
        val dados = intent?.getParcelableExtra<Intent>("data")
        if (dados == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val mpm = getSystemService(MediaProjectionManager::class.java)
        val projecao = mpm.getMediaProjection(codigo, dados)
        projecao.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Mirror.released()
                stopSelf()
            }
        }, Handler(Looper.getMainLooper()))

        Mirror.start(projecao)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Mirror.stop()
        super.onDestroy()
    }
}
