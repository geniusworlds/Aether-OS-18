package com.example.system

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Legitimate Android NotificationListenerService.
 * Only receives notifications when the user explicitly grants Notification Access
 * in Android Settings -> Special App Access -> Notification Access.
 */
class AetherNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        _isConnected.value = true
        refreshActiveNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        _isConnected.value = false
        _liveNotifications.value = emptyList()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        refreshActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        refreshActiveNotifications()
    }

    private fun refreshActiveNotifications() {
        runCatching {
            val active = activeNotifications ?: emptyArray()
            val mapped = active.take(15).mapNotNull { sbn ->
                val extras = sbn.notification.extras
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
                    ?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
                    ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
                    ?: "Active Android system notification"
                val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: text
                val pkg = sbn.packageName ?: "android"
                val appLabel = runCatching {
                    val appInfo = packageManager.getApplicationInfo(pkg, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                }.getOrDefault(pkg.substringAfterLast('.').replaceFirstChar { it.uppercase() })

                NotificationItem(
                    id = "sys_${sbn.key}",
                    appId = pkg,
                    appName = appLabel,
                    title = title,
                    message = text,
                    detailedBody = bigText,
                    timestampMillis = sbn.postTime,
                    isRealSystemNotification = true,
                    categoryTag = "Android Live",
                    accentHex = 0xFF30D158
                )
            }
            _liveNotifications.value = mapped
        }
    }

    companion object {
        private val _isConnected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

        private val _liveNotifications = MutableStateFlow<List<NotificationItem>>(emptyList())
        val liveNotifications: StateFlow<List<NotificationItem>> = _liveNotifications.asStateFlow()
    }
}
