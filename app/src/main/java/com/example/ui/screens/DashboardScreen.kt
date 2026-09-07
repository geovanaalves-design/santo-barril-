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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmployeeEntity
import com.example.data.model.FinancialAccountEntity
import com.example.data.model.IngredientEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.TableStatus
import com.example.ui.MainDestination
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberContainer
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

@Composable
fun DashboardScreen(
    tables: List<RestaurantTableEntity>,
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    ingredients: List<IngredientEntity>,
    employees: List<EmployeeEntity>,
    financialAccounts: List<FinancialAccountEntity>,
    aiInsight: String?,
    isGeneratingAi: Boolean,
    onGenerateAi: () -> Unit,
    onNavigate: (MainDestination) -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("Hoje") }
    val periods = listOf("Hoje", "Ontem", "Últimos 7 Dias", "Mês Atual", "Ano Fiscal")

    val occupiedTablesCount = tables.count { it.status != TableStatus.LIVRE }
    val totalRevenue = orders.sumOf { it.totalAmount }
    val orderCount = orders.size
    val ticketMedio = if (orderCount > 0) totalRevenue / orderCount else 0.0
    val lowStockCount = ingredients.count { it.currentStock <= it.minStock }

    val accountsPayable = financialAccounts.filter { it.type == "PAGAR" && !it.isPaid }.sumOf { it.amount }
    val accountsReceivable = financialAccounts.filter { it.type == "RECEBER" && !it.isPaid }.sumOf { it.amount }

    // Power BI Metrics Estimations
    val salesTarget = 6500.00
    val targetProgress = if (salesTarget > 0) (totalRevenue / salesTarget).coerceIn(0.0, 1.0).toFloat() else 0f
    val grossProfitEstimated = totalRevenue * 0.642
    val occupancyRate = if (tables.isNotEmpty()) (occupiedTablesCount.toFloat() / tables.size.toFloat() * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Power BI Executive Ribbon & Live Telemetry Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WineContainer)
                                    .border(1.dp, WinePrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = WinePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "SANTO BARRIL BI • RELATÓRIO EXECUTIVO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Painel de Performance Gerencial",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinePrimary
                                )
                            }
                        }

                        // Live Sync Pill with Border
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(StatusGreenContainer.copy(alpha = 0.5f))
                                .border(1.dp, StatusGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "POWER BI LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Power BI Slicers Row (Period Segmented Control)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(periods) { p ->
                            val isSelected = selectedPeriod == p
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) WinePrimary else CardWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) WinePrimary else BorderSubtle,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedPeriod = p }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = p,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Power BI Smart Narrative / Copilot AI Insights Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent)
                                    .border(1.dp, GoldDark.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Narrativa Inteligente (Smart BI Insights)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = WineDark
                            )
                        }

                        Button(
                            onClick = onGenerateAi,
                            enabled = !isGeneratingAi,
                            colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                            shape = RoundedCornerShape(7.dp)
                        ) {
                            if (isGeneratingAi) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Atualizar Análise", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = aiInsight ?: "• Pico de vendas projetado entre 19h30 e 22h00.\n• Pratos com maior contribuição: Picanha na Chapa (72% margem) e Chopp Artesanal (78% margem).\n• Ocupação do salão está em ritmo acelerado. Mantenha garçons atentos às mesas 04, 08 e 12.",
                        fontSize = 11.5.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // 3. Power BI Executive KPI Scorecards (Grid 2x2 with Progress Gauges)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Faturamento Total & Ticket Médio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PowerBiKpiCard(
                        title = "FATURAMENTO BRUTO",
                        value = "R$ ${String.format("%.2f", totalRevenue)}",
                        subtitle = "Meta: R$ ${String.format("%.0f", salesTarget)}",
                        progress = targetProgress,
                        badge = "+14.8%",
                        badgeColor = StatusGreen,
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.weight(1f)
                    )

                    PowerBiKpiCard(
                        title = "TICKET MÉDIO",
                        value = "R$ ${String.format("%.2f", ticketMedio)}",
                        subtitle = "$orderCount comanda(s) emitida(s)",
                        progress = (ticketMedio / 200.0).coerceIn(0.0, 1.0).toFloat(),
                        badge = "+5.2%",
                        badgeColor = GoldDark,
                        icon = Icons.Default.ReceiptLong,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Taxa de Ocupação & Margem Bruta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PowerBiKpiCard(
                        title = "TAXA DE OCUPAÇÃO",
                        value = "$occupancyRate%",
                        subtitle = "$occupiedTablesCount de ${tables.size} mesas ativas",
                        progress = occupancyRate / 100f,
                        badge = if (occupancyRate > 70) "ALTA" else "NORMAL",
                        badgeColor = if (occupancyRate > 70) StatusAmber else StatusGreen,
                        icon = Icons.Default.TableRestaurant,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(MainDestination.MESAS) }
                    )

                    PowerBiKpiCard(
                        title = "LUCRO BRUTO EST.",
                        value = "R$ ${String.format("%.2f", grossProfitEstimated)}",
                        subtitle = "Margem Operacional: 64.2%",
                        progress = 0.642f,
                        badge = "SAUDÁVEL",
                        badgeColor = StatusGreen,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(MainDestination.FINANCEIRO_DRE) }
                    )
                }
            }
        }

        // 4. Power BI Visual: Curva de Vendas por Hora (Column Chart Visual)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WineContainer)
                                    .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "CURVA HORÁRIA DE VENDAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    "Faturamento por Faixa de Horário",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinePrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WineContainer)
                                .border(1.dp, WinePrimary.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "PICO: 20h - 22h",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WinePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Column Chart Simulation with 9 Hours slots
                    val hourlyData = listOf(
                        Pair("12h", 320.0),
                        Pair("13h", 580.0),
                        Pair("14h", 240.0),
                        Pair("18h", 450.0),
                        Pair("19h", 890.0),
                        Pair("20h", 1420.0),
                        Pair("21h", 1680.0),
                        Pair("22h", 1120.0),
                        Pair("23h", 640.0)
                    )
                    val maxSales = hourlyData.maxOf { it.second }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        hourlyData.forEach { (hour, amount) ->
                            val isPeak = amount == maxSales
                            val barHeightFraction = (amount / maxSales).toFloat()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isPeak) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(WinePrimary)
                                            .padding(horizontal = 3.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            "R$1.6k",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height((90 * barHeightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (isPeak) WinePrimary else WineContainer)
                                        .border(
                                            1.dp,
                                            if (isPeak) WineDark else WinePrimary.copy(alpha = 0.35f),
                                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = hour,
                                    fontSize = 10.sp,
                                    fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPeak) WinePrimary else TextTertiary
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        // 5. Power BI Visual: Mix de Pagamentos (Segmented Bar & Category Breakdown)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WineContainer)
                                    .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "CONCILIAÇÃO FINANCEIRA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    "Mix de Meios de Pagamento",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinePrimary
                                )
                            }
                        }

                        Text(
                            "DETALHES PDV",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            modifier = Modifier.clickable { onNavigate(MainDestination.CAIXA_PDV) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Segmented Proportional Bar with Borders
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.48f).fillMaxSize().background(WinePrimary))
                        Box(modifier = Modifier.weight(0.32f).fillMaxSize().background(GoldDark))
                        Box(modifier = Modifier.weight(0.14f).fillMaxSize().background(StatusGreen))
                        Box(modifier = Modifier.weight(0.06f).fillMaxSize().background(StatusAmber))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend Badges with Borders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentLegendPill(name = "PIX", percent = "48%", color = WinePrimary, modifier = Modifier.weight(1f))
                        PaymentLegendPill(name = "Crédito", percent = "32%", color = GoldDark, modifier = Modifier.weight(1f))
                        PaymentLegendPill(name = "Débito", percent = "14%", color = StatusGreen, modifier = Modifier.weight(1f))
                        PaymentLegendPill(name = "Dinheiro", percent = "6%", color = StatusAmber, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 6. Power BI Visual: Curva ABC - Top 5 Produtos (Pareto & Margem de Contribuição)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WineContainer)
                                    .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "CURVA ABC & MARGEM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    "Produtos de Maior Rentabilidade",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinePrimary
                                )
                            }
                        }

                        Text(
                            "Ver Cardápio",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            modifier = Modifier.clickable { onNavigate(MainDestination.PEDIDO_CARDAPIO) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    products.take(5).forEachIndexed { index, prod ->
                        val estimatedCost = prod.costPrice
                        val grossMargin = if (prod.price > 0) ((prod.price - estimatedCost) / prod.price) * 100 else 0.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                // Rank Badge with Border
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (index == 0) GoldContainer else OffWhiteBackground)
                                        .border(
                                            1.dp,
                                            if (index == 0) GoldAccent else BorderSubtle,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "#${index + 1}",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index == 0) GoldDark else TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        prod.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.5.sp,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "Preço: R$ ${String.format("%.2f", prod.price)} • Custo: R$ ${String.format("%.2f", estimatedCost)}",
                                        fontSize = 10.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Margin Badge with Border
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusGreenContainer)
                                    .border(1.dp, StatusGreen.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "Margem: ${grossMargin.toInt()}%",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }
                        if (index < products.take(5).size - 1) {
                            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }

        // 7. Power BI Visual: Eficiência Operacional & SLA de Cozinha (KDS Metrics)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WineContainer)
                                    .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "SLA & TELEMETRIA KDS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    "Eficiência Operacional do Salão & Cozinha",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinePrimary
                                )
                            }
                        }

                        Text(
                            "Ver KDS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            modifier = Modifier.clickable { onNavigate(MainDestination.COZINHA_BAR) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KdsMetricBox(
                            title = "TEMPO PREPARO",
                            value = "17 min",
                            status = "Dentro do SLA",
                            icon = Icons.Default.Timer,
                            isGood = true,
                            modifier = Modifier.weight(1f)
                        )

                        KdsMetricBox(
                            title = "GIRO DE MESA",
                            value = "52 min",
                            status = "Ritmo Ideal",
                            icon = Icons.Default.CheckCircle,
                            isGood = true,
                            modifier = Modifier.weight(1f)
                        )

                        KdsMetricBox(
                            title = "ESTOQUE CRÍTICO",
                            value = "$lowStockCount itens",
                            status = if (lowStockCount > 0) "Repor" else "OK",
                            icon = Icons.Default.Warning,
                            isGood = lowStockCount == 0,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 8. Power BI Visual: Desempenho da Equipe de Salão & Comissões
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WineContainer)
                                    .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "EQUIPE & PRODUTIVIDADE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    "Desempenho da Brigada de Salão",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinePrimary
                                )
                            }
                        }

                        Text(
                            "Detalhes CRM",
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigate(MainDestination.EQUIPE_CRM) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    employees.filter { it.role.contains("Garçom", ignoreCase = true) }.forEachIndexed { index, emp ->
                        val commission = emp.totalSales * (emp.commissionRate / 100)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(WineContainer)
                                        .border(1.dp, WinePrimary.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emp.name.firstOrNull()?.toString() ?: "G",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = WinePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                    Text("${emp.ordersCount} pedidos • ${emp.tablesServed} mesas atendidas", fontSize = 10.5.sp, color = TextSecondary)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("R$ ${String.format("%.2f", emp.totalSales)}", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 12.5.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldContainer)
                                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("Comissão: R$ ${String.format("%.2f", commission)}", fontSize = 9.5.sp, color = GoldDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (index < employees.filter { it.role.contains("Garçom", ignoreCase = true) }.size - 1) {
                            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }

        // 9. Power BI Visual: DRE Rápido & Fluxo de Caixa (Saúde Financeira)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "DEMONSTRAÇÃO DE RESULTADO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextTertiary,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                "Fluxo Financeiro do Dia (DRE Sintético)",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = WinePrimary
                            )
                        }

                        Text(
                            "DRE COMPLETO",
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigate(MainDestination.FINANCEIRO_DRE) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusRedContainer)
                                .border(1.dp, StatusRed.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("A Pagar (Hoje)", fontSize = 10.5.sp, color = StatusRed, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("R$ ${String.format("%.2f", accountsPayable)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusGreenContainer)
                                .border(1.dp, StatusGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("A Receber (Hoje)", fontSize = 10.5.sp, color = StatusGreen, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("R$ ${String.format("%.2f", accountsReceivable)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                            }
                        }

                        val saldoDia = accountsReceivable - accountsPayable
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (saldoDia >= 0) WineContainer else StatusRedContainer)
                                .border(
                                    1.dp,
                                    if (saldoDia >= 0) WinePrimary.copy(alpha = 0.35f) else StatusRed.copy(alpha = 0.35f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Saldo Líquido", fontSize = 10.5.sp, color = if (saldoDia >= 0) WinePrimary else StatusRed, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("R$ ${String.format("%.2f", saldoDia)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (saldoDia >= 0) WinePrimary else StatusRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente Power BI Card Visual para métricas de alto impacto com barra de progresso / meta
 */
@Composable
private fun PowerBiKpiCard(
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WineContainer)
                        .border(1.dp, WinePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(badge, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = WinePrimary,
                trackColor = BorderSubtle.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 9.5.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Badge de Legenda para conciliação de pagamentos com borda
 */
@Composable
private fun PaymentLegendPill(
    name: String,
    percent: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(percent, fontSize = 9.sp, color = TextSecondary)
        }
    }
}

/**
 * Caixa de Telemetria de SLA KDS com borda
 */
@Composable
private fun KdsMetricBox(
    title: String,
    value: String,
    status: String,
    icon: ImageVector,
    isGood: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isGood) StatusGreenContainer.copy(alpha = 0.4f) else StatusAmberContainer.copy(alpha = 0.4f))
            .border(
                1.dp,
                if (isGood) StatusGreen.copy(alpha = 0.35f) else StatusAmber.copy(alpha = 0.4f),
                RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGood) StatusGreen else StatusAmber,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = title,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGood) WinePrimary else StatusAmber
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = status,
                fontSize = 9.sp,
                color = if (isGood) StatusGreen else StatusAmber,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
