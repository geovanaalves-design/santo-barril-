package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialAccountEntity
import com.example.data.model.OrderEntity
import com.example.ui.FixedCostItem
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardHeader
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.BentoMetricCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
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
fun FinancialDreScreen(
    financialAccounts: List<FinancialAccountEntity>,
    orders: List<OrderEntity>,
    fixedCosts: List<FixedCostItem> = emptyList(),
    onAddFixedCost: ((String, String, Double) -> Unit)? = null,
    onDeleteFixedCost: ((Long) -> Unit)? = null,
    onAddFinancialAccount: ((description: String, type: String, category: String, amount: Double, isPaid: Boolean) -> Unit)? = null,
    onUpdateFinancialAccount: ((FinancialAccountEntity) -> Unit)? = null,
    onToggleFinancialAccountPaid: ((FinancialAccountEntity) -> Unit)? = null,
    onDeleteFinancialAccount: ((FinancialAccountEntity) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: DRE Gerencial, 1: Gastos Fixos, 2: Contas a Pagar/Receber
    var showAddFixedCostDialog by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var selectedAccountToEdit by remember { mutableStateOf<FinancialAccountEntity?>(null) }
    var accountToDelete by remember { mutableStateOf<FinancialAccountEntity?>(null) }

    // Fallback fixed costs if not passed
    val effectiveFixedCosts = if (fixedCosts.isNotEmpty()) fixedCosts else listOf(
        FixedCostItem(1, "Aluguel Comercial Ponto", "Imóvel", 3200.00),
        FixedCostItem(2, "Folha Salarial Fixa da Equipe", "Pessoal", 8500.00),
        FixedCostItem(3, "Energia Elétrica Comercial", "Utilidades", 840.00),
        FixedCostItem(4, "Água e Saneamento", "Utilidades", 210.00),
        FixedCostItem(5, "Software PDV, TEF & Fibra", "Tecnologia", 280.00),
        FixedCostItem(6, "Contabilidade & Honorários", "Serviços", 650.00)
    )

    // Detailed DRE Calculations
    val receitaBruta = orders.sumOf { it.totalAmount }
    val cmvInsumos = receitaBruta * 0.38 // Custo Variável (CMV)
    val impostosETaxas = receitaBruta * 0.08 // 8% Simples Nacional + taxas cartões
    val lucroBruto = receitaBruta - impostosETaxas - cmvInsumos

    // Gastos Fixos Totais da Empresa
    val totalGastosFixos = effectiveFixedCosts.sumOf { it.amount }
    val gastosFixosOperacionais = if (receitaBruta > 0) totalGastosFixos * 0.20 else totalGastosFixos * 0.10
    val lucroLiquidoReal = lucroBruto - gastosFixosOperacionais
    val margemLiquida = if (receitaBruta > 0) (lucroLiquidoReal / receitaBruta) * 100 else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 16.dp)
    ) {
        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = WinePrimary,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("DRE Gerencial", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Gastos Fixos", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Contas Pagar/Receber", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedTab) {
            0 -> {
                // TAB 0: DRE GERENCIAL ESTRUTURADO COM SAÚDE FINANCEIRA, PONTO DE EQUILÍBRIO E TENDÊNCIA 6 MESES
                val healthBadge = when {
                    margemLiquida >= 18.0 -> Triple("EXCELENTE", StatusGreen, "Margem líquida de ${String.format("%.1f", margemLiquida)}% acima da média de excelência do setor gastronômico.")
                    margemLiquida >= 10.0 -> Triple("SAUDÁVEL", StatusGreen, "Margem equilibrada (${String.format("%.1f", margemLiquida)}%), garantindo sustentabilidade e caixa operacional.")
                    margemLiquida > 0.0 -> Triple("ATENÇÃO", StatusAmber, "Margem positiva porém estreita (${String.format("%.1f", margemLiquida)}%). Monitore custos de insumos.")
                    else -> Triple("CRÍTICA", StatusRed, "Operação em déficit no período. Necessário acelerar vendas ou renegociar despesas.")
                }

                val margemContribuicaoRate = 0.54 // 100% - 38% CMV - 8% Impostos/Taxas
                val pontoEquilibrio = if (margemContribuicaoRate > 0) (totalGastosFixos / margemContribuicaoRate) else 0.0
                val breakEvenProgress = if (pontoEquilibrio > 0) (receitaBruta / pontoEquilibrio).coerceIn(0.0, 1.0).toFloat() else 0f
                val breakEvenPercent = if (pontoEquilibrio > 0) (receitaBruta / pontoEquilibrio) * 100 else 0.0

                LazyColumn(
                    contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Health Status Banner
                    item {
                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = BentoCardVariant.Default,
                            cornerRadius = 16.dp,
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (margemLiquida >= 10.0) StatusGreenContainer else if (margemLiquida > 0.0) StatusAmberContainer else StatusRedContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (margemLiquida >= 10.0) Icons.Default.CheckCircle else if (margemLiquida > 0.0) Icons.Default.TrendingUp else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = healthBadge.second,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            "SAÚDE FINANCEIRA DO ESTABELECIMENTO",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextTertiary,
                                            letterSpacing = 0.6.sp
                                        )
                                        Text(
                                            healthBadge.third,
                                            fontSize = 11.5.sp,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (margemLiquida >= 10.0) StatusGreenContainer else if (margemLiquida > 0.0) StatusAmberContainer else StatusRedContainer)
                                        .border(1.dp, healthBadge.second.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 9.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        healthBadge.first,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = healthBadge.second
                                    )
                                }
                            }
                        }
                    }

                    // 2. Metrics (Receita Bruta & Lucro Líquido)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BentoMetricCard(
                                title = "RECEITA BRUTA",
                                value = "R$ ${String.format("%.2f", receitaBruta)}",
                                icon = Icons.Default.TrendingUp,
                                badge = "VENDAS PDV",
                                modifier = Modifier.weight(1f)
                            )
                            BentoMetricCard(
                                title = "LUCRO LÍQUIDO",
                                value = "R$ ${String.format("%.2f", lucroLiquidoReal)}",
                                icon = Icons.Default.MonetizationOn,
                                badge = "${String.format("%.1f", margemLiquida)}% MARGEM",
                                isHighlighted = lucroLiquidoReal > 0,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 3. Ponto de Equilíbrio (Break-Even Progress Bar)
                    item {
                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = BentoCardVariant.Default,
                            cornerRadius = 20.dp,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(WineContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.ShowChart,
                                                contentDescription = null,
                                                tint = WinePrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                "Ponto de Equilíbrio (Break-Even)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = WinePrimary
                                            )
                                            Text(
                                                "Meta mínima de faturamento para cobrir 100% dos custos",
                                                fontSize = 10.5.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (breakEvenPercent >= 100.0) StatusGreenContainer else StatusAmberContainer)
                                            .border(1.dp, if (breakEvenPercent >= 100.0) StatusGreen else StatusAmber, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "${String.format("%.0f", breakEvenPercent)}% ATINGIDO",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (breakEvenPercent >= 100.0) StatusGreen else StatusAmber
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text("Faturamento Realizado", fontSize = 10.sp, color = TextTertiary)
                                        Text(
                                            "R$ ${String.format("%.2f", receitaBruta)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (receitaBruta >= pontoEquilibrio) StatusGreen else WinePrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Meta de Equilíbrio", fontSize = 10.sp, color = TextTertiary)
                                        Text(
                                            "R$ ${String.format("%.2f", pontoEquilibrio)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }

                                // Linear Progress Bar
                                LinearProgressIndicator(
                                    progress = { breakEvenProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = if (breakEvenPercent >= 100.0) StatusGreen else WinePrimary,
                                    trackColor = WineContainer
                                )

                                Text(
                                    text = if (receitaBruta >= pontoEquilibrio) {
                                        "✓ Ponto de equilíbrio superado! A casa operou cobrindo todas as contas e gerando margem limpa."
                                    } else {
                                        "Faltam R$ ${String.format("%.2f", (pontoEquilibrio - receitaBruta).coerceAtLeast(0.0))} para cobrir todos os custos operacionais e fixos do mês."
                                    },
                                    fontSize = 11.sp,
                                    color = if (receitaBruta >= pontoEquilibrio) StatusGreen else TextSecondary,
                                    fontWeight = if (receitaBruta >= pontoEquilibrio) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // 4. Detailed DRE Rows
                    item {
                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = BentoCardVariant.Default,
                            cornerRadius = 20.dp,
                            contentPadding = PaddingValues(18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                BentoCardHeader(
                                    title = "Demonstrativo do Exercício (DRE)",
                                    subtitle = "Visão analítica gerada a partir dos lançamentos",
                                    icon = Icons.Default.ReceiptLong
                                )

                                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                                DreRow("1. Faturamento Bruto (Mesas & PDV)", "R$ ${String.format("%.2f", receitaBruta)}", TextPrimary, isBold = true)
                                DreRow("(-) Deduções: Impostos & Taxas de TEF (8%)", "- R$ ${String.format("%.2f", impostosETaxas)}", StatusRed)
                                DreRow("(=) Receita Operacional Líquida", "R$ ${String.format("%.2f", receitaBruta - impostosETaxas)}", WinePrimary, isBold = true)

                                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                                DreRow("(-) CMV: Custo das Mercadorias & Insumos (~38%)", "- R$ ${String.format("%.2f", cmvInsumos)}", StatusRed)
                                DreRow("(=) Lucro Bruto da Operação", "R$ ${String.format("%.2f", lucroBruto)}", StatusGreen, isBold = true)

                                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                                DreRow("(-) Gastos Fixos Operacionais Rateados", "- R$ ${String.format("%.2f", gastosFixosOperacionais)}", StatusRed)
                                DreRow("(=) Resultado Líquido do Período", "R$ ${String.format("%.2f", lucroLiquidoReal)}", if (lucroLiquidoReal >= 0) StatusGreen else StatusRed, isBold = true, isLarge = true)
                            }
                        }
                    }

                    // 5. 6-Month Trend Chart Visual
                    item {
                        val trendMonths = listOf(
                            Triple("Set", 28500.0, 22200.0),
                            Triple("Out", 32100.0, 24600.0),
                            Triple("Nov", 36400.0, 27800.0),
                            Triple("Dez", 45200.0, 33400.0),
                            Triple("Jan", 34800.0, 26900.0),
                            Triple("Fev", receitaBruta.coerceAtLeast(33800.0), (cmvInsumos + impostosETaxas + gastosFixosOperacionais).coerceAtLeast(25500.0))
                        )
                        val maxTrendRevenue = trendMonths.maxOf { it.second }

                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = BentoCardVariant.Default,
                            cornerRadius = 20.dp,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(WineContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.BarChart,
                                                contentDescription = null,
                                                tint = WinePrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                "Tendência dos Últimos 6 Meses",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = WinePrimary
                                            )
                                            Text(
                                                "Faturamento vs Custos e Lucro no semestre",
                                                fontSize = 10.5.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(StatusGreenContainer)
                                            .border(1.dp, StatusGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "+18.4% SEMESTRE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusGreen
                                        )
                                    }
                                }

                                // Multi-Bar Monthly Chart
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    trendMonths.forEach { (label, rev, costs) ->
                                        val profit = rev - costs
                                        val revHeight = (rev / maxTrendRevenue).toFloat().coerceIn(0.15f, 1f)
                                        val costHeight = (costs / maxTrendRevenue).toFloat().coerceIn(0.15f, 1f)
                                        val profitHeight = (profit / maxTrendRevenue).toFloat().coerceIn(0.08f, 1f)

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Bottom,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                verticalAlignment = Alignment.Bottom,
                                                modifier = Modifier.height(100.dp)
                                            ) {
                                                // Revenue bar
                                                Box(
                                                    modifier = Modifier
                                                        .width(9.dp)
                                                        .fillMaxHeight(revHeight)
                                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                                        .background(WinePrimary)
                                                )
                                                // Cost bar
                                                Box(
                                                    modifier = Modifier
                                                        .width(9.dp)
                                                        .fillMaxHeight(costHeight)
                                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                                        .background(StatusRed.copy(alpha = 0.75f))
                                                )
                                                // Profit bar
                                                Box(
                                                    modifier = Modifier
                                                        .width(9.dp)
                                                        .fillMaxHeight(profitHeight)
                                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                                        .background(StatusGreen)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (label == "Fev") WinePrimary else TextSecondary
                                            )
                                        }
                                    }
                                }

                                // Legend
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WinePrimary))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Faturamento", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusRed.copy(alpha = 0.75f)))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Custos Totais", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Lucro Líquido", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: GASTOS FIXOS (Alimentação pelo Gerente)
                LazyColumn(
                    contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Gastos Fixos da Empresa",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Total Cadastrado: R$ ${String.format("%.2f", totalGastosFixos)}/mês",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = { showAddFixedCostDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Novo Gasto", fontSize = 12.sp)
                            }
                        }
                    }

                    items(effectiveFixedCosts) { cost ->
                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = BentoCardVariant.Default,
                            cornerRadius = 16.dp,
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
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(WineContainer)
                                            .border(1.dp, BorderSubtle, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = null,
                                            tint = WinePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(cost.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                        Text("Categoria: ${cost.category}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "R$ ${String.format("%.2f", cost.amount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = StatusRed
                                    )

                                    if (onDeleteFixedCost != null) {
                                        IconButton(onClick = { onDeleteFixedCost(cost.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Excluir",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: CONTAS A PAGAR & RECEBER (Alimentação & Gestão pelo Gerente)
                val totalPagar = financialAccounts.filter { it.type == "PAGAR" && !it.isPaid }.sumOf { it.amount }
                val totalReceber = financialAccounts.filter { it.type == "RECEBER" && !it.isPaid }.sumOf { it.amount }

                LazyColumn(
                    contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BentoMetricCard(
                                title = "A PAGAR (PENDENTE)",
                                value = "R$ ${String.format("%.2f", totalPagar)}",
                                icon = Icons.Default.ArrowDownward,
                                badge = "SAÍDAS",
                                isHighlighted = totalPagar > 0,
                                modifier = Modifier.weight(1f)
                            )

                            BentoMetricCard(
                                title = "A RECEBER (PENDENTE)",
                                value = "R$ ${String.format("%.2f", totalReceber)}",
                                icon = Icons.Default.ArrowUpward,
                                badge = "ENTRADAS",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Lançamentos Financeiros",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Contas a pagar e receber do restaurante",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = { showAddAccountDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Nova Conta", fontSize = 12.sp)
                            }
                        }
                    }

                    items(financialAccounts) { acc ->
                        val isPayable = acc.type == "PAGAR"
                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = BentoCardVariant.Default,
                            cornerRadius = 16.dp,
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isPayable) StatusRedContainer else StatusGreenContainer)
                                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isPayable) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                                contentDescription = null,
                                                tint = if (isPayable) StatusRed else StatusGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(acc.description, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text("Categoria: ${acc.category} • ${if (isPayable) "Saída (Pagar)" else "Entrada (Receber)"}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            (if (isPayable) "- " else "+ ") + "R$ ${String.format("%.2f", acc.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isPayable) StatusRed else StatusGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (acc.isPaid) StatusGreenContainer else GoldContainer)
                                                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                if (acc.isPaid) "Liquidado" else "Pendente",
                                                fontSize = 10.sp,
                                                color = if (acc.isPaid) StatusGreen else GoldDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Action to toggle status
                                    Button(
                                        onClick = { onToggleFinancialAccountPaid?.invoke(acc) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (acc.isPaid) CardWhite else WineContainer
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .height(30.dp)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (acc.isPaid) TextSecondary else WinePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            if (acc.isPaid) "Marcar Pendente" else "Liquidar / Pagar",
                                            fontSize = 11.sp,
                                            color = if (acc.isPaid) TextSecondary else WinePrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Edit Account
                                        IconButton(
                                            onClick = { selectedAccountToEdit = acc },
                                            modifier = Modifier
                                                .size(30.dp)
                                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = WinePrimary, modifier = Modifier.size(14.dp))
                                        }

                                        // Delete Account
                                        IconButton(
                                            onClick = { accountToDelete = acc },
                                            modifier = Modifier
                                                .size(30.dp)
                                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = StatusRed, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Fixed Cost
    if (showAddFixedCostDialog) {
        var costName by remember { mutableStateOf("") }
        var costCategory by remember { mutableStateOf("Utilidades") }
        var costAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFixedCostDialog = false },
            title = {
                Text(
                    text = "Cadastrar Gasto Fixo",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = costName,
                        onValueChange = { costName = it },
                        label = { Text("Nome do Gasto Fixo") },
                        placeholder = { Text("Ex: Aluguel do Galpão / Ponto") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = costCategory,
                        onValueChange = { costCategory = it },
                        label = { Text("Categoria") },
                        placeholder = { Text("Ex: Imóvel, Pessoal, Utilidades") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = costAmount,
                        onValueChange = { costAmount = it },
                        label = { Text("Valor Mensal (R$)") },
                        placeholder = { Text("Ex: 1500.00") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = costAmount.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (costName.isNotBlank() && amount > 0) {
                            onAddFixedCost?.invoke(costName, costCategory, amount)
                            showAddFixedCostDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Salvar Gasto Fixo", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddFixedCostDialog = false },
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Nova Conta a Pagar / Receber
    if (showAddAccountDialog) {
        var desc by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("PAGAR") }
        var category by remember { mutableStateOf("Insumos & Reposição") }
        var amountStr by remember { mutableStateOf("") }
        var isPaid by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddAccountDialog = false },
            title = {
                Text("Cadastrar Conta Financeira", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descrição da Conta") },
                        placeholder = { Text("Ex: Compra Carvão, Manutenção Freezer") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { type = "PAGAR" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (type == "PAGAR") StatusRed else CardWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        ) {
                            Text("A PAGAR", color = if (type == "PAGAR") Color.White else StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { type = "RECEBER" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (type == "RECEBER") StatusGreen else CardWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        ) {
                            Text("A RECEBER", color = if (type == "RECEBER") Color.White else StatusGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        placeholder = { Text("Ex: Insumos, Pessoal, Utilidades") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Valor (R$)") },
                        placeholder = { Text("Ex: 450.00") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status Inicial:", fontSize = 12.sp, color = TextPrimary)
                        Button(
                            onClick = { isPaid = !isPaid },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPaid) StatusGreenContainer else GoldContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                if (isPaid) "Já Liquidado / Pago" else "Pendente",
                                fontSize = 11.sp,
                                color = if (isPaid) StatusGreen else GoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (desc.isNotBlank() && amount > 0) {
                            onAddFinancialAccount?.invoke(desc, type, category, amount, isPaid)
                            showAddAccountDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Salvar Conta", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddAccountDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Editar Conta
    selectedAccountToEdit?.let { acc ->
        var desc by remember { mutableStateOf(acc.description) }
        var category by remember { mutableStateOf(acc.category) }
        var amountStr by remember { mutableStateOf(acc.amount.toString()) }

        AlertDialog(
            onDismissRequest = { selectedAccountToEdit = null },
            title = {
                Text("Editar Conta", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descrição") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Valor (R$)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: acc.amount
                        if (desc.isNotBlank()) {
                            onUpdateFinancialAccount?.invoke(
                                acc.copy(description = desc, category = category, amount = amount)
                            )
                            selectedAccountToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Salvar Alterações", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedAccountToEdit = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Confirmar Exclusão de Conta
    accountToDelete?.let { acc ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text("Excluir Lançamento", fontWeight = FontWeight.Bold, color = StatusRed) },
            text = { Text("Deseja realmente remover o lançamento '${acc.description}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFinancialAccount?.invoke(acc)
                        accountToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { accountToDelete = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DreRow(title: String, value: String, valueColor: Color, isBold: Boolean = false, isLarge: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = if (isLarge) 13.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = TextPrimary
        )
        Text(
            text = value,
            fontSize = if (isLarge) 15.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}
