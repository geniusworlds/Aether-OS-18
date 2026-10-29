package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.NotificationItem
import com.example.ui.theme.CyanAether
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.SapphireAccentLight

@Composable
fun NotificationCenterOverlay(
    liveNotifications: List<NotificationItem>,
    demoNotifications: List<NotificationItem>,
    expandedIds: Set<String>,
    groupByApp: Boolean,
    notificationListenerAuthorized: Boolean,
    onDismissOverlay: () -> Unit,
    onToggleExpandItem: (String) -> Unit,
    onDismissDemoItem: (String) -> Unit,
    onClearAllDemo: () -> Unit,
    onRestoreDemo: () -> Unit,
    onToggleGroupByApp: (Boolean) -> Unit,
    onOpenNotificationAccessSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE0070B14))
            .clickable(onClick = onDismissOverlay)
            .testTag("notification_center_overlay"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .clickable(enabled = false, onClick = {})
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.notification_center_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Clearly separates Live Android Alerts & Local Demo Cards",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8)
                        )
                    )
                }

                IconButton(
                    onClick = onDismissOverlay,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B2436))
                        .testTag("close_notification_center_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close Notification Center",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Controls Strip: Group By App Toggle + Clear / Restore Demo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = groupByApp,
                    onClick = { onToggleGroupByApp(!groupByApp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Group by App", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SapphireAccentLight,
                        selectedLabelColor = Color.White,
                        labelColor = Color.White
                    ),
                    modifier = Modifier.testTag("notif_group_by_app_chip")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (demoNotifications.isNotEmpty()) {
                        Surface(
                            onClick = onClearAllDemo,
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.testTag("clear_all_demo_notifications_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteSweep,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = stringResource(R.string.clear_all_demo_notifications),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    } else {
                        Surface(
                            onClick = onRestoreDemo,
                            shape = RoundedCornerShape(50.dp),
                            color = SapphireAccentLight,
                            modifier = Modifier.testTag("restore_demo_notifications_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = stringResource(R.string.restore_demo_notifications),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // SECTION 1: LIVE ANDROID SYSTEM NOTIFICATIONS
                item(key = "section_live_header") {
                    Text(
                        text = "LIVE ANDROID SYSTEM NOTIFICATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldActive,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                if (!notificationListenerAuthorized) {
                    item(key = "notif_listener_permission_card") {
                        Surface(
                            onClick = onOpenNotificationAccessSettings,
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFF141E30),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    EmeraldActive.copy(alpha = 0.35f),
                                    RoundedCornerShape(22.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = null,
                                    tint = EmeraldActive,
                                    modifier = Modifier.size(26.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Optional: Connect Live Android Notifications",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Aether OS never accesses notifications without explicit authorization. Tap to open Android Notification Listener settings.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else if (liveNotifications.isEmpty()) {
                    item(key = "live_empty_state") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF121A2A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notification Listener authorized • No active Android notifications right now.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(
                        items = liveNotifications,
                        key = { it.id }
                    ) { notif ->
                        NotificationCardItem(
                            item = notif,
                            isExpanded = expandedIds.contains(notif.id),
                            onToggleExpand = { onToggleExpandItem(notif.id) },
                            onDismiss = null
                        )
                    }
                }

                // SECTION 2: LOCAL DEMONSTRATION NOTIFICATIONS
                item(key = "section_demo_header") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "LOCAL DEMONSTRATION NOTIFICATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAether,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                if (demoNotifications.isEmpty()) {
                    item(key = "demo_empty_state") {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFF121A2A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "All demonstration notifications cleared",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                                )
                                Text(
                                    text = "Tap 'Restore Demo' above to repopulate sample notification cards.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
                                )
                            }
                        }
                    }
                } else {
                    val displayList = if (groupByApp) {
                        demoNotifications.sortedBy { it.appName }
                    } else {
                        demoNotifications.sortedByDescending { it.timestampMillis }
                    }
                    items(
                        items = displayList,
                        key = { it.id }
                    ) { notif ->
                        NotificationCardItem(
                            item = notif,
                            isExpanded = expandedIds.contains(notif.id),
                            onToggleExpand = { onToggleExpandItem(notif.id) },
                            onDismiss = { onDismissDemoItem(notif.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCardItem(
    item: NotificationItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onDismiss: (() -> Unit)?
) {
    val relativeMinutes = ((System.currentTimeMillis() - item.timestampMillis) / 60_000L).coerceAtLeast(1L)
    val relativeLabel = if (relativeMinutes < 60L) "${relativeMinutes}m ago" else "${relativeMinutes / 60L}h ago"

    Surface(
        onClick = onToggleExpand,
        shape = RoundedCornerShape(22.dp),
        color = Color(0xD9151E30),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(22.dp))
            .testTag("notification_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(item.accentHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.appName.take(1),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = item.appName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isRealSystemNotification) "ANDROID LIVE" else "DEMO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = if (item.isRealSystemNotification) EmeraldActive else CyanAether
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = relativeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    if (onDismiss != null) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("dismiss_notif_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss notification",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = item.message,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFCBD5E1)),
                maxLines = if (isExpanded) 5 else 2,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0E1524))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.detailedBody,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }
    }
}
