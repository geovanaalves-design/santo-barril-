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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.TableStatus
import com.example.ui.MainViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberContainer
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WineLight
import com.example.ui.theme.WinePrimary

@Composable
fun TablesScreen(
    tables: List<RestaurantTableEntity>,
    orders: List<OrderEntity>,
    customers: List<CustomerEntity>,
    onOpenTableOrder: (Int) -> Unit,
    onOpenBillPayment: (Int) -> Unit,
    viewModel: MainViewModel
) {
    var selectedSection by remember { mutableStateOf("Todos") }
    var selectedStatusFilter by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }
    val sections = listOf("Todos", "Salão Principal", "Varanda", "Deck")

    val totalCount = tables.size
    val freeCount = tables.count { it.status == TableStatus.LIVRE }
    val occupiedCount = tables.count { it.status != TableStatus.LIVRE }
    val waitingBillCount = tables.count { it.status == TableStatus.AGUARDANDO_PAGAMENTO }

    val statusFilters = listOf(
        "Todas ($totalCount)" to "Todas",
        "Livres ($freeCount)" to "Livres",
        "Ocupadas ($occupiedCount)" to "Ocupadas",
        "Conta ($waitingBillCount)" to "Aguardando Conta"
    )

    // Filtered tables by status, section and search query
    val filteredTables = tables.filter { table ->
        val sectionMatch = selectedSection == "Todos" || table.section.equals(selectedSection, ignoreCase = true)
        val statusMatch = when (selectedStatusFilter) {
            "Livres" -> table.status == TableStatus.LIVRE
            "Ocupadas" -> table.status != TableStatus.LIVRE && table.status != TableStatus.AGUARDANDO_PAGAMENTO
            "Aguardando Conta" -> table.status == TableStatus.AGUARDANDO_PAGAMENTO
            else -> true
        }
        val searchMatch = if (searchQuery.isBlank()) true else {
            val q = searchQuery.trim().lowercase()
            table.name.lowercase().contains(q) ||
            table.number.toString() == q ||
            table.section.lowercase().contains(q) ||
            table.customerName.lowercase().contains(q) ||
            table.waiterName.lowercase().contains(q)
        }
        sectionMatch && statusMatch && searchMatch
    }

    // Dialog states
    var tableToOpen by remember { mutableStateOf<RestaurantTableEntity?>(null) }
    var tableToManage by remember { mutableStateOf<RestaurantTableEntity?>(null) }
    var tableToTransfer by remember { mutableStateOf<RestaurantTableEntity?>(null) }
    var tableToMerge by remember { mutableStateOf<RestaurantTableEntity?>(null) }

    val allOrderItems by viewModel.orderItems.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Overview Title & Live Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mapa de Mesas",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = WinePrimary,
                        fontSize = 19.sp
                    )
                )
                Text(
                    text = "Visão geral e status de atendimento do salão",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                )
            }

            // Real-time live occupancy pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(WineContainer)
                    .border(1.dp, WinePrimary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (occupiedCount > 0) StatusAmber else StatusGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$occupiedCount/$totalCount Ocupadas",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = WinePrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Summary Metrics Cards: Proportional 3-column banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: Total
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WineContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableRestaurant,
                            contentDescription = null,
                            tint = WinePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "$totalCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "TOTAL",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Card 2: Livres
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "$freeCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                        Text(
                            text = "LIVRES",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Card 3: Ocupadas
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (occupiedCount > 0) WinePrimary.copy(alpha = 0.35f) else BorderSubtle),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (occupiedCount > 0) StatusAmberContainer else OffWhiteBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (occupiedCount > 0) StatusAmber else TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "$occupiedCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (occupiedCount > 0) WinePrimary else TextSecondary
                        )
                        Text(
                            text = "OCUPADAS",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (occupiedCount > 0) StatusAmber else TextTertiary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar with Clean Pill Outline
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    "Buscar mesa por nº, setor ou cliente...",
                    fontSize = 12.sp,
                    color = TextTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpar busca",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor = WinePrimary,
                unfocusedBorderColor = BorderSubtle
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Status Filter Tabs with Counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            statusFilters.forEach { (label, filterKey) ->
                val isSelected = selectedStatusFilter == filterKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) WinePrimary else CardWhite)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) WinePrimary else BorderSubtle,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedStatusFilter = filterKey }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Section Filter Chips (Scrollable row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sections.forEach { section ->
                val isSelected = selectedSection == section
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSection = section },
                    label = {
                        Text(
                            section,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WineContainer,
                        selectedLabelColor = WinePrimary,
                        containerColor = CardWhite,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) WinePrimary else BorderSubtle
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tables Grid Map or Empty State
        if (filteredTables.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, bottom = 80.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(WineContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableRestaurant,
                                contentDescription = null,
                                tint = WinePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "Nenhuma mesa encontrada",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Não há mesas que correspondam aos filtros selecionados ou à busca atual.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        OutlinedButton(
                            onClick = {
                                selectedStatusFilter = "Todas"
                                selectedSection = "Todos"
                                searchQuery = ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, WinePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = WinePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Limpar Filtros e Busca", color = WinePrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // LazyVerticalGrid with responsive adaptive columns and proportional spacing
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 155.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 84.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTables, key = { it.number }) { table ->
                    val tableOrders = orders.filter { it.tableNumber == table.number && !it.isPaid }
                    val tableTotal = tableOrders.sumOf { it.totalAmount }

                    TableItemCard(
                        table = table,
                        totalConsumed = tableTotal,
                        onClick = {
                            if (table.status == TableStatus.LIVRE) {
                                tableToOpen = table
                            } else {
                                tableToManage = table
                            }
                        }
                    )
                }
            }
        }
    }

    // DIALOG: Open Table (Cliente Novo vs Cliente da Casa)
    tableToOpen?.let { table ->
        OpenTableDialog(
            table = table,
            customers = customers,
            onDismiss = { tableToOpen = null },
            onConfirm = { name, phone, isNew, channel ->
                viewModel.openTable(table.number, name, phone, isNew, channel)
                tableToOpen = null
                onOpenTableOrder(table.number)
            }
        )
    }

    // DIALOG: Manage Table Actions (View Consumption, Add Order, Transfer, Merge, Close, Cancel)
    tableToManage?.let { table ->
        val tableOrders = orders.filter { it.tableNumber == table.number && !it.isPaid }
        val tableTotal = tableOrders.sumOf { it.totalAmount }
        val tableOrderIds = tableOrders.map { it.id }.toSet()
        val tableOrderItems = allOrderItems.filter { it.orderId in tableOrderIds }

        ManageTableDialog(
            table = table,
            activeOrders = tableOrders,
            orderItems = tableOrderItems,
            totalConsumed = tableTotal,
            onDismiss = { tableToManage = null },
            onAddOrder = {
                tableToManage = null
                onOpenTableOrder(table.number)
            },
            onCloseBill = {
                tableToManage = null
                onOpenBillPayment(table.number)
            },
            onTransfer = {
                tableToTransfer = table
                tableToManage = null
            },
            onMerge = {
                tableToMerge = table
                tableToManage = null
            },
            onCancelService = {
                viewModel.cancelTableService(table.number, "Cancelado pelo operador")
                tableToManage = null
            }
        )
    }

    // DIALOG: Transfer Table
    tableToTransfer?.let { fromTable ->
        val availableTables = tables.filter { it.status == TableStatus.LIVRE && it.number != fromTable.number }
        var targetTableNumber by remember { mutableIntStateOf(availableTables.firstOrNull()?.number ?: 0) }

        AlertDialog(
            onDismissRequest = { tableToTransfer = null },
            title = {
                Text(
                    "Transferir Mesa ${fromTable.number}",
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary
                )
            },
            text = {
                Column {
                    Text(
                        "Selecione a nova mesa para onde os pedidos e consumo de '${fromTable.customerName}' serão transferidos:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (availableTables.isEmpty()) {
                        Text("Nenhuma mesa livre disponível no momento.", color = StatusRed, fontSize = 13.sp)
                    } else {
                        availableTables.forEach { tbl ->
                            val isSelected = targetTableNumber == tbl.number
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) WineContainer.copy(alpha = 0.5f) else OffWhiteBackground)
                                    .border(
                                        1.dp,
                                        if (isSelected) WinePrimary else BorderSubtle,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { targetTableNumber = tbl.number }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { targetTableNumber = tbl.number },
                                    colors = RadioButtonDefaults.colors(selectedColor = WinePrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "${tbl.name} (${tbl.section})",
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) WinePrimary else TextPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (targetTableNumber > 0) {
                            viewModel.transferTable(fromTable.number, targetTableNumber)
                            tableToTransfer = null
                        }
                    },
                    enabled = targetTableNumber > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
                ) {
                    Text("Confirmar Transferência", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { tableToTransfer = null }) {
                    Text("Voltar")
                }
            }
        )
    }

    // DIALOG: Merge Tables
    tableToMerge?.let { mainTable ->
        val otherTables = tables.filter { it.number != mainTable.number && it.mergedWithTableNumber == null }
        var selectedToMerge by remember { mutableIntStateOf(otherTables.firstOrNull()?.number ?: 0) }

        AlertDialog(
            onDismissRequest = { tableToMerge = null },
            title = {
                Text(
                    "Juntar com Mesa ${mainTable.number}",
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary
                )
            },
            text = {
                Column {
                    Text(
                        "Unifique mesas para atender grupos maiores sob o mesmo atendimento.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    otherTables.take(6).forEach { tbl ->
                        val isSelected = selectedToMerge == tbl.number
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) WineContainer.copy(alpha = 0.5f) else OffWhiteBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) WinePrimary else BorderSubtle,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedToMerge = tbl.number }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedToMerge = tbl.number },
                                colors = RadioButtonDefaults.colors(selectedColor = WinePrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "${tbl.name} - ${tbl.status.name} (${tbl.section})",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) WinePrimary else TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedToMerge > 0) {
                            viewModel.mergeTables(mainTable.number, selectedToMerge)
                            tableToMerge = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
                ) {
                    Text("Unificar Mesas", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { tableToMerge = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TableItemCard(
    table: RestaurantTableEntity,
    totalConsumed: Double,
    onClick: () -> Unit
) {
    val isOccupied = table.status != TableStatus.LIVRE

    val (statusBg, statusFg, statusLabel) = when (table.status) {
        TableStatus.LIVRE -> Triple(StatusGreenContainer, StatusGreen, "LIVRE")
        TableStatus.OCUPADA -> Triple(StatusAmberContainer, StatusAmber, "OCUPADA")
        TableStatus.PEDIDO_ENVIADO -> Triple(StatusBlueContainer, StatusBlue, "PEDIDO ENVIADO")
        TableStatus.EM_PREPARO -> Triple(StatusBlueContainer, StatusBlue, "EM PREPARO")
        TableStatus.AGUARDANDO_PAGAMENTO -> Triple(StatusPurpleContainer, StatusPurple, "CONTA")
        TableStatus.FINALIZADA -> Triple(StatusGreenContainer, StatusGreen, "FINALIZADA")
    }

    val elapsedMinutes = if (table.openedAtMillis > 0) {
        ((System.currentTimeMillis() - table.openedAtMillis) / (60 * 1000)).coerceAtLeast(1)
    } else 0

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOccupied) Color(0xFFFCF8F8) else CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (isOccupied) 1.5.dp else 1.dp,
            color = if (isOccupied) WinePrimary.copy(alpha = 0.55f) else BorderSubtle
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Status Badge locked in Top-Right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusBg)
                    .border(1.dp, statusFg.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(statusFg)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusLabel,
                        color = statusFg,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp,
                        maxLines = 1
                    )
                }
            }

            // Main Content: Arranged vertically with locked proportions
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Table Icon & Number + Section Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 76.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOccupied) WineContainer else OffWhiteBackground)
                            .border(
                                1.dp,
                                if (isOccupied) WinePrimary.copy(alpha = 0.35f) else BorderSubtle,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableRestaurant,
                            contentDescription = null,
                            tint = if (isOccupied) WinePrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = table.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = if (isOccupied) WinePrimary else TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = table.section.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextTertiary,
                                fontSize = 8.5.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))

                // Middle Block: Exactly 50.dp fixed height for proportional grid symmetry
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isOccupied) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = WinePrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = table.customerName.ifBlank { "Cliente da mesa" },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${elapsedMinutes}m",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextTertiary
                                    )
                                }
                            }

                            Text(
                                text = if (table.waiterName.isNotBlank()) "Atendente: ${table.waiterName}" else "Atendimento ativo",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        // Free table state: Clean, encouraging visual feedback
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Mesa disponível",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StatusGreen
                                )
                            }
                            Text(
                                text = "Pronta para receber clientes",
                                fontSize = 10.sp,
                                color = TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))

                // Footer: Consumo display & Action Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "CONSUMO",
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = TextTertiary
                        )
                        Text(
                            text = "R$ ${String.format("%.2f", if (isOccupied) totalConsumed else 0.0)}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOccupied) WinePrimary else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Action Button with minimum interactive size
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOccupied) WinePrimary else StatusGreenContainer)
                            .border(
                                width = 1.dp,
                                color = if (isOccupied) WinePrimary else StatusGreen.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = onClick)
                            .padding(horizontal = 9.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isOccupied) Icons.Default.RestaurantMenu else Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isOccupied) Color.White else StatusGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOccupied) "Comanda" else "Abrir",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOccupied) Color.White else StatusGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OpenTableDialog(
    table: RestaurantTableEntity,
    customers: List<CustomerEntity>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, isNew: Boolean, channel: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Novo, 1 = Da Casa
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("Instagram") }
    var selectedExistingCustomer by remember { mutableStateOf<CustomerEntity?>(null) }

    val originChannels = listOf(
        "Indicação de amigo", "Instagram", "Facebook", "TikTok",
        "Google", "Passou em frente", "Evento", "Publicidade", "Já conhecia", "Outro"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Abertura: ${table.name}",
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary
                )
                Text(
                    text = "Selecione o perfil de cliente para iniciar o atendimento",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = OffWhiteBackground,
                        contentColor = WinePrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Cliente Novo", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Cliente da Casa", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Cliente Novo (Somente Nome + Telefone + Como conheceu)
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nome Completo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Telefone / WhatsApp *") },
                        placeholder = { Text("(17) 99999-9999") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Como conheceu o Santo Barril?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WinePrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        originChannels.chunked(2).forEach { rowChannels ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowChannels.forEach { channel ->
                                    val isChanSelected = selectedChannel == channel
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isChanSelected) WineContainer.copy(alpha = 0.4f) else OffWhiteBackground)
                                            .border(
                                                1.dp,
                                                if (isChanSelected) WinePrimary.copy(alpha = 0.6f) else BorderSubtle,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedChannel = channel }
                                            .padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isChanSelected,
                                            onClick = { selectedChannel = channel },
                                            colors = RadioButtonDefaults.colors(selectedColor = WinePrimary)
                                        )
                                        Text(
                                            text = channel,
                                            fontSize = 11.sp,
                                            fontWeight = if (isChanSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isChanSelected) WinePrimary else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Cliente da Casa (Pesquisa por Nome ou Telefone)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            selectedExistingCustomer = null
                        },
                        label = { Text("Buscar por nome ou telefone") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WinePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val matching = customers.filter {
                        it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
                    }

                    if (selectedExistingCustomer != null) {
                        val cust = selectedExistingCustomer!!
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = GoldContainer),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.7f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cust.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("VIP", color = GoldDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Text("Telefone: ${cust.phone}", fontSize = 12.sp, color = TextSecondary)
                                Text("Total de Visitas: ${cust.visitCount} vezes", fontSize = 12.sp, color = TextSecondary)
                                Text("Total Consumido: R$ ${String.format("%.2f", cust.totalSpent)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WinePrimary)
                                Text("Canal de Origem: ${cust.originChannel}", fontSize = 11.sp, color = TextTertiary)
                            }
                        }
                    } else {
                        Text(
                            text = "Selecione na lista abaixo:",
                            fontSize = 11.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Column(modifier = Modifier.height(180.dp).verticalScroll(rememberScrollState())) {
                            matching.forEach { cust ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable {
                                            selectedExistingCustomer = cust
                                            nameInput = cust.name
                                            phoneInput = cust.phone
                                        },
                                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(cust.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(cust.phone, fontSize = 11.sp, color = TextSecondary)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("${cust.visitCount} visitas", fontSize = 11.sp, color = GoldDark, fontWeight = FontWeight.Bold)
                                            Text("R$ ${String.format("%.2f", cust.totalSpent)}", fontSize = 11.sp, color = WinePrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        val name = nameInput.ifBlank { "Cliente Mesa ${table.number}" }
                        onConfirm(name, phoneInput, true, selectedChannel)
                    } else {
                        val cust = selectedExistingCustomer
                        if (cust != null) {
                            onConfirm(cust.name, cust.phone, false, cust.originChannel)
                        } else {
                            val name = nameInput.ifBlank { "Cliente Mesa ${table.number}" }
                            onConfirm(name, phoneInput, false, "Já conhecia")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
            ) {
                Text("Abrir e Ver Cardápio", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ManageTableDialog(
    table: RestaurantTableEntity,
    activeOrders: List<OrderEntity>,
    orderItems: List<OrderItemEntity> = emptyList(),
    totalConsumed: Double,
    onDismiss: () -> Unit,
    onAddOrder: () -> Unit,
    onCloseBill: () -> Unit,
    onTransfer: () -> Unit,
    onMerge: () -> Unit,
    onCancelService: () -> Unit
) {
    // Group consumption by waiter
    val waiterMap = remember(activeOrders, orderItems, table) {
        val map = mutableMapOf<String, MutableList<OrderItemEntity>>()
        if (orderItems.isNotEmpty()) {
            orderItems.forEach { item ->
                val waiter = if (item.waiterName.isNotBlank()) item.waiterName
                else (activeOrders.find { it.id == item.orderId }?.waiterName?.ifBlank { table.waiterName } ?: table.waiterName.ifBlank { "Garçom da Mesa" })
                map.getOrPut(waiter) { mutableListOf() }.add(item)
            }
        }
        map
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(table.name, fontWeight = FontWeight.Bold, color = WinePrimary)
                    Text("Cliente: ${table.customerName}", fontSize = 13.sp, color = TextSecondary)
                }
                Text(
                    "R$ ${String.format("%.2f", totalConsumed)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = WinePrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Info Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WineContainer)
                            .border(1.dp, WinePrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Garçom Responsável", fontSize = 10.sp, color = TextSecondary)
                            Text(table.waiterName.ifBlank { "Não informado" }, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WineDark)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OffWhiteBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Pedidos Ativos", fontSize = 10.sp, color = TextSecondary)
                            Text("${activeOrders.size} lançamentos", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PER-WAITER BREAKDOWN SECTION
                Text(
                    text = "Detalhamento por Garçom",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = WinePrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (waiterMap.isEmpty() && activeOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(OffWhiteBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum item lançado nesta mesa até o momento.", fontSize = 11.5.sp, color = TextTertiary)
                    }
                } else if (waiterMap.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        waiterMap.forEach { (waiterName, items) ->
                            val waiterTotal = items.sumOf { it.unitPrice * it.quantity }
                            val percent = if (totalConsumed > 0) (waiterTotal / totalConsumed) * 100 else 0.0

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                tint = WinePrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                waiterName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = TextPrimary
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "R$ ${String.format("%.2f", waiterTotal)}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = WinePrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(WineContainer)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    "${String.format("%.0f", percent)}%",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = WineDark
                                                )
                                            }
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        color = BorderSubtle.copy(alpha = 0.5f)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        items.forEach { item ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        "${item.quantity}x ${item.productName}",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextPrimary
                                                    )
                                                    if (item.notes.isNotBlank()) {
                                                        Text(
                                                            "Obs: ${item.notes}",
                                                            fontSize = 10.sp,
                                                            color = TextTertiary
                                                        )
                                                    }
                                                }
                                                Text(
                                                    "R$ ${String.format("%.2f", item.unitPrice * item.quantity)}",
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Fallback to active orders if items are pending load
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        activeOrders.forEach { ord ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardWhite)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(ord.waiterName.ifBlank { table.waiterName }, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text("Pedido #${ord.orderNumber}: R$ ${String.format("%.2f", ord.totalAmount)}", fontSize = 11.5.sp, color = WinePrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Primary Action: Add more items from menu
                    Button(
                        onClick = onAddOrder,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lançar Novos Pedidos", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Fechar Mesa / Ir para Pagamento
                    Button(
                        onClick = onCloseBill,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fechar Conta (Caixa PDV)", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onTransfer,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Transferir", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onMerge,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Merge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Juntar", fontSize = 12.sp)
                        }
                    }

                    // Cancel
                    OutlinedButton(
                        onClick = onCancelService,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cancelar Atendimento da Mesa", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
