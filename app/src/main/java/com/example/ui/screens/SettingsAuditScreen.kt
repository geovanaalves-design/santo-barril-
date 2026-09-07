package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotificationEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.PrintJobEntity
import com.example.data.model.ProductionSector
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardHeader
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.BentoMetricCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsAuditScreen(
    printJobs: List<PrintJobEntity>,
    auditLogs: List<AuditLogEntity>,
    notifications: List<AppNotificationEntity>,
    onMarkNotificationRead: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Impressoras Térmicas, 1: Logs de Auditoria, 2: Notificações

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 14.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = WinePrimary,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Impressoras Térmicas", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Auditoria & Logs", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Notificações", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedTab) {
            0 -> ThermalPrintersView(printJobs = printJobs)
            1 -> AuditLogsView(auditLogs = auditLogs)
            2 -> NotificationsView(notifications = notifications, onMarkRead = onMarkNotificationRead)
        }
    }
}

@Composable
fun ThermalPrintersView(printJobs: List<PrintJobEntity>) {
    val printerStations = listOf(
        Triple("Impressora Churrasqueira (80mm)", "Rede TCP/IP: 192.168.1.201", "Online"),
        Triple("Impressora Cozinha (80mm)", "Rede TCP/IP: 192.168.1.202", "Online"),
        Triple("Impressora Bar (80mm)", "Rede TCP/IP: 192.168.1.203", "Online"),
        Triple("Impressora Caixa PDV (80mm)", "USB Esc/POS", "Online")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Bento Hero Metrics for Printers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoMetricCard(
                    title = "ESTAÇÕES ATIVAS",
                    value = "${printerStations.size} impressoras",
                    icon = Icons.Default.Print,
                    badge = "TODAS ONLINE",
                    badgeColor = StatusGreen,
                    modifier = Modifier.weight(1f)
                )

                BentoMetricCard(
                    title = "FILA DE IMPRESSÃO",
                    value = "${printJobs.size} tickets",
                    icon = Icons.Default.Refresh,
                    badge = "SINCRONIZADO",
                    isHighlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text("Estações de Impressão Térmica (ESC/POS)", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
        }

        items(printerStations) { (name, ip, status) ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = 20.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(WineContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text(ip, fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(status, color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Fila de Impressão e Reimpressão de Tickets", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
        }

        items(printJobs.take(8)) { job ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = 16.dp,
                contentPadding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pedido #${job.orderNumber} • Mesa ${job.tableNumber} (${job.sector.name})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(job.itemsSummary, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
                    }

                    OutlinedButton(
                        onClick = { /* Reimprimir simulado */ },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reimprimir", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogsView(auditLogs: List<AuditLogEntity>) {
    val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.OffWhite,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(WineContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Segurança, Compliance & Auditoria Fiscal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WinePrimary)
                        Text(
                            "Todas as operações de cancelamento, transferência e sangrias são criptografadas e registradas.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        items(auditLogs) { log ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = 16.dp,
                contentPadding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WinePrimary)
                    Text(dateFormat.format(Date(log.timestampMillis)), fontSize = 11.sp, color = TextTertiary)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(log.details, fontSize = 12.sp, color = TextPrimary)
                Text("Operador: ${log.userName}", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun NotificationsView(
    notifications: List<AppNotificationEntity>,
    onMarkRead: (Long) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notifications) { notif ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = if (!notif.isRead) BentoCardVariant.WineTinted else BentoCardVariant.Default,
                cornerRadius = 18.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (!notif.isRead) WinePrimary else TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(dateFormat.format(Date(notif.timestampMillis)), fontSize = 10.sp, color = TextTertiary)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(notif.message, fontSize = 12.sp, color = TextPrimary)
                    }

                    if (!notif.isRead) {
                        OutlinedButton(
                            onClick = { onMarkRead(notif.id) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text("OK", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
