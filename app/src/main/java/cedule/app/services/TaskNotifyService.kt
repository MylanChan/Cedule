package cedule.app.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import cedule.app.R

class TaskNotifyService : Service() {
    private var player: AlarmPlayer? = null

    private val isGrantedPermission: Boolean
        get() {
            val p = ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
            return p == PackageManager.PERMISSION_GRANTED
        }

    private val stopAlarmPI: PendingIntent
        get() {
            val stopAlarmIntent = Intent(this, TaskNotifyService::class.java)
            stopAlarmIntent.setAction("STOP_ALARM")

            return PendingIntent.getService(
                this, 0, stopAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    private fun createChannel(name: String) {
        val manager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val c = NotificationChannel(name, name, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(c)
        }
    }

    private fun stopAlarm() {
        player?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)


        if (!isGrantedPermission) {
            stopSelf()
            return START_STICKY
        }

        if (intent?.action.equals("STOP_ALARM", true)) {
            stopAlarm()
            stopSelf()
            return START_NOT_STICKY
        }

        createChannel("Cedule")

        val builder =
            NotificationCompat.Builder(applicationContext, "Cedule")
                .setSmallIcon(R.drawable.ic_task)
                .setColor(Color.GRAY)
                .setContentText("Time to work")
                .setDeleteIntent(stopAlarmPI)
                .addAction(R.drawable.ic_close, "Stop", stopAlarmPI)

        startForeground(1, builder.build())

        if (player != null) player!!.release()

        player = AlarmPlayer(applicationContext)
        player!!.run()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}