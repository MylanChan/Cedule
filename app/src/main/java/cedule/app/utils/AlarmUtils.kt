package cedule.app.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cedule.app.data.entities.Task
import cedule.app.services.TaskNotifyReceiver

object AlarmUtils {
    fun setAlarm(context: Context, task: Task) {
        cancelAlarm(context, task.id!!)

        val intent = Intent(context, TaskNotifyReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, task.id, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerTime = (task.startDate ?: 0) + (task.startTime?.let { it * 60 * 1000 } ?: 0)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi)
    }


    fun cancelAlarm(context: Context, taskId: Int) {
        val intent = Intent(context, TaskNotifyReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pi)
    }
}