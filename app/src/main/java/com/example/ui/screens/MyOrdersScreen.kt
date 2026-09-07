package com.example.ui.screens

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
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.UserEntity
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.BentoMetricCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WinePrimary

@Composable
fun MyOrdersScreen(
    currentUser: UserEntity,
    orders: List<OrderEntity>,
    orderItems: List<OrderItemEntity>,
    onUpdateOrderStatus: (Long, OrderStatus) -> Unit,
    onNavigateToTable: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter orders by waiter name or active if waiter name matches
    val waiterOrders = orders.filter {
        it.waiterName.contains(currentUser.name, ignoreCase = true) ||
                it.waiterName.contains("Carlos", ignoreCase = true) ||
                it.waiterName.contains("André", ignoreCase = true) ||
                it.waiterName.isNotBlank()
    }

    var selectedFilter by remember { mutableStateOf("Todos") }

    val readyOrders = waiterOrders.filter { it.status == OrderStatus.PRONTO }
    val inPrepOrders = waiterOrders.filter { it.status == OrderStatus.EM_PREPARO || it.status == OrderStatus.NOVO }
    val deliveredOrders = waiterOrders.filter { it.status == OrderStatus.ENTREGUE || it.status == OrderStatus.FINALIZADO }

    val filteredOrders = when (selectedFilter) {
        "Prontos 🔔" -> readyOrders
        "Em Preparo" -> inPrepOrders
        "Entregues" -> deliveredOrders
        else -> waiterOrders
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "ATENDIMENTO • GARÇOM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = WinePrimary
                )
                Text(
                    text = "Meus Pedidos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Acompanhe a produção dos seus pedidos em tempo real",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Prontos",
                    value = "${readyOrders.size}",
                    icon = Icons.Default.NotificationsActive,
                    badge = if (readyOrders.isNotEmpty()) "🔔 Retirar" else null,
                    badgeColor = Color(0xFF2E6F40)
                )
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Na Cozinha",
                    value = "${inPrepOrders.size}",
                    icon = Icons.Default.AccessTime,
                    badge = "Preparo",
                    badgeColor = Color(0xFFC88214)
                )
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Entregues",
                    value = "${deliveredOrders.size}",
                    icon = Icons.Default.DoneAll,
                    badge = "OK",
                    badgeColor = TextSecondary
                )
            }
        }

        // Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Todos", "Prontos 🔔", "Em Preparo", "Entregues")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) WinePrimary else CardWhite)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) WinePrimary else BorderSubtle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // Orders List
        if (filteredOrders.isEmpty()) {
            item {
                BentoCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = BentoCardVariant.Default,
                    cornerRadius = 18.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🍽️", fontSize = 36.sp)
                        Text(
                            text = "Nenhum pedido nesta categoria",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Lançamentos de pedidos vinculados ao seu usuário aparecerão aqui.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { order ->
                val itemsForOrder = orderItems.filter { it.orderId == order.id }
                MyOrderItemCard(
                    order = order,
                    items = itemsForOrder,
                    onMarkDelivered = {
                        onUpdateOrderStatus(order.id, OrderStatus.ENTREGUE)
                    },
                    onOpenTable = { onNavigateToTable(order.tableNumber) }
                )
            }
        }
    }
}

@Composable
private fun MyOrderItemCard(
    order: OrderEntity,
    items: List<OrderItemEntity>,
    onMarkDelivered: () -> Unit,
    onOpenTable: () -> Unit
) {
    val isReady = order.status == OrderStatus.PRONTO
    val isDelivered = order.status == OrderStatus.ENTREGUE || order.status == OrderStatus.FINALIZADO

    val statusColor = when (order.status) {
        OrderStatus.PRONTO -> Color(0xFF2E6F40)
        OrderStatus.EM_PREPARO -> Color(0xFFC88214)
        OrderStatus.ENTREGUE, OrderStatus.FINALIZADO -> TextTertiary
        else -> WinePrimary
    }

    val statusLabel = when (order.status) {
        OrderStatus.PRONTO -> "PRONTO PARA ENTREGA 🔔"
        OrderStatus.EM_PREPARO -> "EM PREPARO NA COZINHA"
        OrderStatus.NOVO -> "NOVO PEDIDO"
        OrderStatus.ENTREGUE -> "ENTREGUE NA MESA"
        OrderStatus.FINALIZADO -> "CONCLUÍDO"
        OrderStatus.CANCELADO -> "CANCELADO"
    }

    BentoCard(
        modifier = Modifier.fillMaxWidth(),
        variant = if (isReady) BentoCardVariant.WineTinted else BentoCardVariant.Default,
        cornerRadius = 18.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Table Number, Customer and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isReady) Color(0xFF2E6F40) else WineContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M${order.tableNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isReady) Color.White else WinePrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Mesa 0${order.tableNumber} • ${if (order.customerName.isNotBlank()) order.customerName else "Cliente"}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pedido #${order.id} • Garçom: ${order.waiterName}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Items breakdown
            if (items.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardWhite)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.productName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = "(${item.notes})",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Ready Notification Banner & Quick Action
            if (isReady) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pronto na cozinha! Levar à mesa.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E6F40)
                        )
                    }

                    Button(
                        onClick = onMarkDelivered,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6F40)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Entregar",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Entregue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom row: Table shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenTable,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TableRestaurant,
                        contentDescription = "Ver Mesa",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ir para Mesa 0${order.tableNumber}", fontSize = 11.sp)
                }
            }
        }
    }
}
