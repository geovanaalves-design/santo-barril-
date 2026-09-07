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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.CashierMovementEntity
import com.example.data.model.CashierShiftEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.TableStatus
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.OffWhiteBackground
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
import kotlin.math.max

@Composable
fun CashierScreen(
    currentShift: CashierShiftEntity?,
    movements: List<CashierMovementEntity>,
    tables: List<RestaurantTableEntity>,
    orders: List<OrderEntity>,
    orderItems: List<OrderItemEntity>,
    selectedTableNumber: Int?,
    onOpenCashier: (Double) -> Unit,
    onCloseCashier: (Double) -> Unit,
    onAddMovement: (type: String, amount: Double, reason: String) -> Unit,
    onFinalizePayment: (
        tableNumber: Int,
        subtotal: Double,
        discount: Double,
        includeServiceFee: Boolean,
        serviceFee: Double,
        finalTotal: Double,
        paymentMethod: String
    ) -> Unit,
    onSelectTable: (Int?) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (selectedTableNumber != null) 0 else 1) } // 0: Fechamento Mesa, 1: Painel do Caixa
    var showMovementDialog by remember { mutableStateOf<String?>(null) } // "SANGRIA" ou "SUPRIMENTO"
    var showCloseShiftDialog by remember { mutableStateOf(false) }
    var showOpenShiftDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 12.dp)
    ) {
        // Cashier Status Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (currentShift?.isOpen == true) StatusGreenContainer else StatusRedContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentShift?.isOpen == true) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (currentShift?.isOpen == true) StatusGreen else StatusRed
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (currentShift?.isOpen == true) "Caixa ABERTO" else "Caixa FECHADO",
                            fontWeight = FontWeight.Bold,
                            color = if (currentShift?.isOpen == true) StatusGreen else StatusRed,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Operador: ${currentShift?.operatorName ?: "Administrador"}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Action Open/Close shift
                if (currentShift?.isOpen == true) {
                    OutlinedButton(
                        onClick = { showCloseShiftDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Fechar Caixa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { showOpenShiftDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Abrir Caixa", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Navigation Tabs: Fechamento de Mesa / Painel do Caixa
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = WinePrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Fechamento de Mesa PDV", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Fluxo do Caixa & Sangria", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            // TAB 0: FECHAMENTO DE MESA PDV
            TablePaymentClosingView(
                tables = tables,
                orders = orders,
                orderItems = orderItems,
                selectedTableNumber = selectedTableNumber,
                onSelectTable = onSelectTable,
                onFinalizePayment = onFinalizePayment
            )
        } else {
            // TAB 1: PAINEL DO CAIXA (SANGRIAS, SUPRIMENTOS, CONFERÊNCIA)
            CashierShiftOverviewView(
                shift = currentShift,
                movements = movements,
                onAddSangria = { showMovementDialog = "SANGRIA" },
                onAddSuprimento = { showMovementDialog = "SUPRIMENTO" }
            )
        }
    }

    // DIALOG: Sangria / Suprimento
    showMovementDialog?.let { type ->
        var amountStr by remember { mutableStateOf("") }
        var reasonStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showMovementDialog = null },
            title = {
                Text(
                    "Lançar $type",
                    fontWeight = FontWeight.Bold,
                    color = if (type == "SANGRIA") StatusRed else StatusGreen
                )
            },
            text = {
                Column {
                    Text(
                        if (type == "SANGRIA") "Retirada de dinheiro do caixa (sangria/despesas):"
                        else "Entrada de reforço de dinheiro no caixa (suprimento/troco):",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Valor (R$)") },
                        placeholder = { Text("50,00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reasonStr,
                        onValueChange = { reasonStr = it },
                        label = { Text("Motivo / Justificativa") },
                        placeholder = { Text("Ex: Pagamento gelo, Troco extra") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onAddMovement(type, amount, reasonStr.ifBlank { type })
                            showMovementDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "SANGRIA") StatusRed else StatusGreen
                    )
                ) {
                    Text("Confirmar Lançamento", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showMovementDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Fechamento de Caixa com Contagem & Diferença
    if (showCloseShiftDialog) {
        var countedCashStr by remember { mutableStateOf("") }
        val expected = currentShift?.expectedCash ?: 0.0
        val counted = countedCashStr.replace(",", ".").toDoubleOrNull() ?: 0.0
        val diff = counted - expected

        AlertDialog(
            onDismissRequest = { showCloseShiftDialog = false },
            title = {
                Text("Fechamento de Caixa", fontWeight = FontWeight.Bold, color = WinePrimary)
            },
            text = {
                Column {
                    Text(
                        "Digite o valor em dinheiro contado na gaveta para conferência:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WineContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Saldo em Dinheiro Esperado:", fontSize = 11.sp, color = TextSecondary)
                            Text("R$ ${String.format("%.2f", expected)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WineDark)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = countedCashStr,
                        onValueChange = { countedCashStr = it },
                        label = { Text("Valor Físico Contado (R$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )
                    if (countedCashStr.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Diferença Apurada:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "R$ ${String.format("%.2f", diff)}" + if (diff < 0) " (Falta)" else if (diff > 0) " (Sobra)" else " (Exato)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (diff < 0) StatusRed else if (diff > 0) StatusGreen else TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCloseCashier(counted)
                        showCloseShiftDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
                ) {
                    Text("Encerrar Caixa", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCloseShiftDialog = false }) {
                    Text("Voltar")
                }
            }
        )
    }

    // DIALOG: Abertura de Caixa
    if (showOpenShiftDialog) {
        var openingBalanceStr by remember { mutableStateOf("200,00") }

        AlertDialog(
            onDismissRequest = { showOpenShiftDialog = false },
            title = {
                Text("Abertura de Caixa", fontWeight = FontWeight.Bold, color = WinePrimary)
            },
            text = {
                Column {
                    Text(
                        "Informe o fundo de troco inicial da gaveta:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = openingBalanceStr,
                        onValueChange = { openingBalanceStr = it },
                        label = { Text("Fundo de Troco (R$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WinePrimary,
                            focusedLabelColor = WinePrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val balance = openingBalanceStr.replace(",", ".").toDoubleOrNull() ?: 200.0
                        onOpenCashier(balance)
                        showOpenShiftDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
                ) {
                    Text("Confirmar Abertura", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showOpenShiftDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TablePaymentClosingView(
    tables: List<RestaurantTableEntity>,
    orders: List<OrderEntity>,
    orderItems: List<OrderItemEntity>,
    selectedTableNumber: Int?,
    onSelectTable: (Int?) -> Unit,
    onFinalizePayment: (
        tableNumber: Int,
        subtotal: Double,
        discount: Double,
        includeServiceFee: Boolean,
        serviceFee: Double,
        finalTotal: Double,
        paymentMethod: String
    ) -> Unit
) {
    val occupiedTables = tables.filter { it.status != TableStatus.LIVRE }
    val currentTable = tables.find { it.number == selectedTableNumber } ?: occupiedTables.firstOrNull()

    var discountStr by remember { mutableStateOf("0") }
    var includeServiceFee by remember { mutableStateOf(true) }
    var splitCount by remember { mutableIntStateOf(1) }
    var selectedPaymentMethod by remember { mutableStateOf("PIX") }
    var showReceiptSuccess by remember { mutableStateOf(false) }

    val tableOrders = if (currentTable != null) {
        orders.filter { it.tableNumber == currentTable.number && !it.isPaid }
    } else emptyList()

    val subtotal = tableOrders.sumOf { it.subtotal }
    val discount = discountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
    val serviceFee = if (includeServiceFee) (subtotal - discount).coerceAtLeast(0.0) * 0.10 else 0.0
    val finalTotal = max(0.0, subtotal - discount + serviceFee)
    val splitValue = if (splitCount > 0) finalTotal / splitCount else finalTotal

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Table Selector Carousel
        item {
            Column {
                Text(
                    "Selecione a Mesa para Fechamento:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                if (occupiedTables.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardWhite)
                    ) {
                        Text(
                            "Nenhuma mesa com consumo ativo no momento.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        occupiedTables.forEach { tbl ->
                            val isSelected = tbl.number == currentTable?.number
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectTable(tbl.number) },
                                label = { Text("${tbl.name} (${tbl.customerName.ifBlank { "Cliente" }})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = WinePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        if (currentTable != null && tableOrders.isNotEmpty()) {
            // Bill Details Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "${currentTable.name} • ${currentTable.customerName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = WinePrimary
                                )
                                Text("Garçom: ${currentTable.waiterName}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Text(
                                "R$ ${String.format("%.2f", finalTotal)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = WinePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Consumed items summary
                        Text("Itens Consumidos:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        tableOrders.forEach { ord ->
                            val items = orderItems.filter { it.orderId == ord.id }
                            items.forEach { itm ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${itm.quantity}x ${itm.productName}", fontSize = 12.sp, color = TextPrimary)
                                    Text("R$ ${String.format("%.2f", itm.unitPrice * itm.quantity)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Discount & 10% Service Fee
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subtotal", fontSize = 13.sp, color = TextSecondary)
                            Text("R$ ${String.format("%.2f", subtotal)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = includeServiceFee,
                                    onCheckedChange = { includeServiceFee = it },
                                    colors = CheckboxDefaults.colors(checkedColor = WinePrimary)
                                )
                                Text("Taxa de Serviço (10%)", fontSize = 12.sp, color = TextPrimary)
                            }
                            Text("R$ ${String.format("%.2f", serviceFee)}", fontSize = 12.sp, color = TextSecondary)
                        }

                        // Split Bill (Dividir Conta)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CallSplit, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dividir Conta:", fontSize = 12.sp, color = TextPrimary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (splitCount > 1) splitCount-- },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = WinePrimary)
                                }
                                Text("$splitCount pessoas", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp))
                                IconButton(
                                    onClick = { splitCount++ },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = WinePrimary)
                                }
                            }
                        }

                        if (splitCount > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Valor por Pessoa:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                                Text("R$ ${String.format("%.2f", splitValue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WinePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Payment Methods
                        Text("Forma de Pagamento:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WinePrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("PIX", "DINHEIRO", "DÉBITO", "CRÉDITO").forEach { method ->
                                FilterChip(
                                    selected = selectedPaymentMethod == method,
                                    onClick = { selectedPaymentMethod = method },
                                    label = { Text(method, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = WinePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Finalize Payment Button
                        Button(
                            onClick = {
                                onFinalizePayment(
                                    currentTable.number,
                                    subtotal,
                                    discount,
                                    includeServiceFee,
                                    serviceFee,
                                    finalTotal,
                                    selectedPaymentMethod
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Finalizar e Emitir Comprovante (R$ ${String.format("%.2f", finalTotal)})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CashierShiftOverviewView(
    shift: CashierShiftEntity?,
    movements: List<CashierMovementEntity>,
    onAddSangria: () -> Unit,
    onAddSuprimento: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Shift metrics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Resumo das Vendas por Meio de Pagamento", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val totalVendido = (shift?.totalCash ?: 0.0) + (shift?.totalPix ?: 0.0) +
                            (shift?.totalDebit ?: 0.0) + (shift?.totalCredit ?: 0.0)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("💵 Dinheiro em Caixa", fontSize = 13.sp)
                        Text("R$ ${String.format("%.2f", shift?.totalCash ?: 0.0)}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("⚡ PIX Recebido", fontSize = 13.sp)
                        Text("R$ ${String.format("%.2f", shift?.totalPix ?: 0.0)}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("💳 Cartão de Débito", fontSize = 13.sp)
                        Text("R$ ${String.format("%.2f", shift?.totalDebit ?: 0.0)}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("💳 Cartão de Crédito", fontSize = 13.sp)
                        Text("R$ ${String.format("%.2f", shift?.totalCredit ?: 0.0)}", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Vendido no Turno:", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("R$ ${String.format("%.2f", totalVendido)}", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
                    }
                }
            }
        }

        // Quick Sangria & Suprimento Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddSangria,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRedContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = StatusRed)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sangria (Saída)", color = StatusRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onAddSuprimento,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreenContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = StatusGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Suprimento (Entrada)", color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Movements log
        item {
            Text("Histórico de Movimentações do Caixa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WinePrimary)
        }

        if (movements.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
                    Text("Nenhuma sangria ou suprimento registrado neste turno.", modifier = Modifier.padding(14.dp), fontSize = 12.sp, color = TextTertiary)
                }
            }
        } else {
            items(movements) { mov ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${mov.type}: ${mov.reason}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (mov.type == "SANGRIA") StatusRed else StatusGreen
                            )
                            Text("Operador: ${mov.operatorName}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Text(
                            text = (if (mov.type == "SANGRIA") "- " else "+ ") + "R$ ${String.format("%.2f", mov.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (mov.type == "SANGRIA") StatusRed else StatusGreen
                        )
                    }
                }
            }
        }
    }
}
