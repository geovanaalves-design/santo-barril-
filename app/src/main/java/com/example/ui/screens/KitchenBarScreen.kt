package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PrintJobEntity
import com.example.data.model.ProductionSector
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberContainer
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary

enum class KdsStatusFilter {
    TODOS,
    NOVO,
    EM_PREPARO,
    PRONTO
}

@Composable
fun KitchenBarScreen(
    orders: List<OrderEntity>,
    orderItems: List<OrderItemEntity>,
    printJobs: List<PrintJobEntity>,
    onUpdateItemStatus: (Long, OrderStatus) -> Unit
) {
    var selectedSector by remember { mutableStateOf<ProductionSector?>(null) } // null = Todos
    var selectedStatusFilter by remember { mutableStateOf(KdsStatusFilter.TODOS) }

    val activeItems = orderItems.filter {
        it.status != OrderStatus.FINALIZADO && it.status != OrderStatus.CANCELADO && it.status != OrderStatus.ENTREGUE
    }

    // Filter by Sector (Churrasqueira, Cozinha, Bar)
    val sectorFilteredItems = if (selectedSector == null) {
        activeItems
    } else {
        activeItems.filter { it.productionSector == selectedSector }
    }

    // Group items by orderId for KDS Ticket presentation
    val itemsByOrder = sectorFilteredItems.groupBy { it.orderId }

    // Map tickets with overall status and order entity, ordered by wait time (earliest timestamp first = waiting longest)
    val ticketList = itemsByOrder.entries.mapNotNull { (orderId, items) ->
        val order = orders.find { it.id == orderId }
        val overallStatus = when {
            items.any { it.status == OrderStatus.NOVO } -> OrderStatus.NOVO
            items.any { it.status == OrderStatus.EM_PREPARO } -> OrderStatus.EM_PREPARO
            items.all { it.status == OrderStatus.PRONTO } -> OrderStatus.PRONTO
            else -> OrderStatus.NOVO
        }
        val timestamp = order?.timestampMillis ?: 0L
        Triple(order, items, overallStatus) to timestamp
    }.sortedBy { it.second }.map { it.first }

    // Apply Status tab filter
    val filteredTickets = when (selectedStatusFilter) {
        KdsStatusFilter.TODOS -> ticketList
        KdsStatusFilter.NOVO -> ticketList.filter { it.third == OrderStatus.NOVO }
        KdsStatusFilter.EM_PREPARO -> ticketList.filter { it.third == OrderStatus.EM_PREPARO }
        KdsStatusFilter.PRONTO -> ticketList.filter { it.third == OrderStatus.PRONTO }
    }

    val countNovos = ticketList.count { it.third == OrderStatus.NOVO }
    val countEmPreparo = ticketList.count { it.third == OrderStatus.EM_PREPARO }
    val countProntos = ticketList.count { it.third == OrderStatus.PRONTO }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Sector Tabs: Todos os Setores, Cozinha, Bar, Churrasqueira (Section B.2 - No emojis)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedSector == null,
                    onClick = { selectedSector = null },
                    label = { Text("Todos os Setores", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WinePrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedSector == ProductionSector.COZINHA,
                    onClick = { selectedSector = ProductionSector.COZINHA },
                    label = { Text("Cozinha", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WinePrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedSector == ProductionSector.BAR,
                    onClick = { selectedSector = ProductionSector.BAR },
                    label = { Text("Bar", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WinePrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedSector == ProductionSector.CHURRASQUEIRA,
                    onClick = { selectedSector = ProductionSector.CHURRASQUEIRA },
                    label = { Text("Churrasqueira", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WinePrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Status filter pills: Todos | Novo | Em Preparo | Pronto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KdsStatusPill(
                label = "Todos (${ticketList.size})",
                isSelected = selectedStatusFilter == KdsStatusFilter.TODOS,
                color = WinePrimary,
                onClick = { selectedStatusFilter = KdsStatusFilter.TODOS },
                modifier = Modifier.weight(1f)
            )
            KdsStatusPill(
                label = "Novo ($countNovos)",
                isSelected = selectedStatusFilter == KdsStatusFilter.NOVO,
                color = StatusBlue,
                onClick = { selectedStatusFilter = KdsStatusFilter.NOVO },
                modifier = Modifier.weight(1f)
            )
            KdsStatusPill(
                label = "Preparo ($countEmPreparo)",
                isSelected = selectedStatusFilter == KdsStatusFilter.EM_PREPARO,
                color = StatusAmber,
                onClick = { selectedStatusFilter = KdsStatusFilter.EM_PREPARO },
                modifier = Modifier.weight(1f)
            )
            KdsStatusPill(
                label = "Pronto ($countProntos)",
                isSelected = selectedStatusFilter == KdsStatusFilter.PRONTO,
                color = StatusGreen,
                onClick = { selectedStatusFilter = KdsStatusFilter.PRONTO },
                modifier = Modifier.weight(1f)
            )
        }

        // Tickets List
        if (filteredTickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = WinePrimary.copy(alpha = 0.3f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum pedido nesta fila",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Novos pedidos enviados pela equipe aparecerão automaticamente aqui.",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTickets, key = { it.first?.id ?: it.second.first().id }) { (order, items, overallStatus) ->
                    KdsOrderTicketCard(
                        order = order,
                        items = items,
                        overallStatus = overallStatus,
                        onUpdateItemStatus = onUpdateItemStatus
                    )
                }
            }
        }
    }
}

@Composable
private fun KdsStatusPill(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color else CardWhite)
            .border(
                width = 1.dp,
                color = if (isSelected) color else BorderSubtle,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
fun KdsOrderTicketCard(
    order: OrderEntity?,
    items: List<OrderItemEntity>,
    overallStatus: OrderStatus,
    onUpdateItemStatus: (Long, OrderStatus) -> Unit
) {
    val elapsedMinutes = if (order != null && order.timestampMillis > 0) {
        ((System.currentTimeMillis() - order.timestampMillis) / (60 * 1000)).coerceAtLeast(1)
    } else 1

    val (timerBg, timerFg) = when {
        elapsedMinutes > 25 -> StatusRedContainer to StatusRed
        elapsedMinutes > 15 -> StatusAmberContainer to StatusAmber
        else -> WineContainer.copy(alpha = 0.5f) to TextSecondary
    }

    // Status-driven accent color and badges
    val (statusLabel, statusColor, statusContainer) = when (overallStatus) {
        OrderStatus.NOVO -> Triple("NOVO PEDIDO", StatusBlue, StatusBlueContainer)
        OrderStatus.EM_PREPARO -> Triple("EM PREPARO", StatusAmber, StatusAmberContainer)
        OrderStatus.PRONTO -> Triple("PRONTO P/ ENTREGA", StatusGreen, StatusGreenContainer)
        else -> Triple("ENTREGUE", WinePrimary, WineContainer)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Column {
            // Status Accent Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Clear Headings for Table, Order #, Status Stage, and Elapsed Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Order and Table Headings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Prominent Table Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(WineContainer)
                                .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "MESA %02d".format(order?.tableNumber ?: 0),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = WinePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Order Number Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OffWhiteBackground)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "#${order?.orderNumber ?: "---"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Status Stage and Elapsed Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Current Stage Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusContainer)
                                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }

                        // Elapsed Time Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(timerBg)
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = timerFg
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$elapsedMinutes min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = timerFg
                            )
                        }
                    }
                }

                // Sub-header: Attendant & Customer Information
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atendente: ${order?.waiterName?.ifBlank { "Equipe" } ?: "Equipe"}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                    Text(
                        text = "Cliente: ${order?.customerName?.ifBlank { "Mesa %02d".format(order?.tableNumber ?: 0) } ?: "Mesa"}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                // Order Items with Proper Visual Hierarchy
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items.forEach { item ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Quantity Pill
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(WineContainer)
                                            .border(1.dp, WinePrimary.copy(alpha = 0.25f), RoundedCornerShape(7.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${item.quantity}x",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = WinePrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Item Title
                                    Text(
                                        text = item.productName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Sector Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(OffWhiteBackground)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.productionSector.name,
                                            fontSize = 9.sp,
                                            color = TextTertiary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Item Individual Status Chip
                                val itemStatusColor = when (item.status) {
                                    OrderStatus.NOVO -> StatusBlue
                                    OrderStatus.EM_PREPARO -> StatusAmber
                                    OrderStatus.PRONTO -> StatusGreen
                                    else -> WinePrimary
                                }
                                val itemStatusContainer = when (item.status) {
                                    OrderStatus.NOVO -> StatusBlueContainer
                                    OrderStatus.EM_PREPARO -> StatusAmberContainer
                                    OrderStatus.PRONTO -> StatusGreenContainer
                                    else -> WineContainer
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(itemStatusContainer)
                                        .border(1.dp, itemStatusColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            val nextStatus = when (item.status) {
                                                OrderStatus.NOVO -> OrderStatus.EM_PREPARO
                                                OrderStatus.EM_PREPARO -> OrderStatus.PRONTO
                                                OrderStatus.PRONTO -> OrderStatus.ENTREGUE
                                                else -> OrderStatus.ENTREGUE
                                            }
                                            onUpdateItemStatus(item.id, nextStatus)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = when (item.status) {
                                            OrderStatus.NOVO -> "Novo"
                                            OrderStatus.EM_PREPARO -> "Em Preparo"
                                            OrderStatus.PRONTO -> "Pronto"
                                            else -> "Entregue"
                                        },
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = itemStatusColor
                                    )
                                }
                            }

                            // Dedicated High-Visibility Observation Box
                            if (item.notes.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StatusAmberContainer.copy(alpha = 0.45f))
                                        .border(1.dp, StatusAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = StatusAmber,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Obs: ${item.notes}",
                                            fontSize = 11.5.sp,
                                            fontStyle = FontStyle.Italic,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF7A4F00)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status-Driven Action Button at the Bottom of the Card
                val (buttonText, buttonColor, buttonIcon) = when (overallStatus) {
                    OrderStatus.NOVO -> Triple("Iniciar Preparo deste Pedido", StatusBlue, Icons.Default.PlayArrow)
                    OrderStatus.EM_PREPARO -> Triple("Concluir Preparo (Marcar Pronto)", Color(0xFF2E7D32), Icons.Default.Check)
                    OrderStatus.PRONTO -> Triple("Despachar Pedido (Marcar Entregue)", WinePrimary, Icons.Default.CheckCircle)
                    else -> Triple("Pedido Entregue", TextSecondary, Icons.Default.Check)
                }

                Button(
                    onClick = {
                        val nextStatus = when (overallStatus) {
                            OrderStatus.NOVO -> OrderStatus.EM_PREPARO
                            OrderStatus.EM_PREPARO -> OrderStatus.PRONTO
                            OrderStatus.PRONTO -> OrderStatus.ENTREGUE
                            else -> OrderStatus.ENTREGUE
                        }
                        items.forEach { item ->
                            onUpdateItemStatus(item.id, nextStatus)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 1.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buttonText,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}
