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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.CustomerEntity
import com.example.data.model.EmployeeEntity
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
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary

@Composable
fun TeamCrmScreen(
    employees: List<EmployeeEntity>,
    customers: List<CustomerEntity>,
    onAddEmployee: ((name: String, role: String, phone: String, commissionRate: Double) -> Unit)? = null,
    onUpdateEmployee: ((EmployeeEntity) -> Unit)? = null,
    onDeleteEmployee: ((EmployeeEntity) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Garçons & Produtividade, 1: CRM & Canais de Origem
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var selectedEmployeeToEdit by remember { mutableStateOf<EmployeeEntity?>(null) }
    var employeeToDelete by remember { mutableStateOf<EmployeeEntity?>(null) }

    val channelStats = customers.groupBy { it.originChannel }
        .map { (channel, list) ->
            val totalSpent = list.sumOf { it.totalSpent }
            val count = list.size
            val avgTicket = if (count > 0) totalSpent / count else 0.0
            ChannelData(channel, count, totalSpent, avgTicket)
        }
        .sortedByDescending { it.totalRevenue }

    val totalTeamSales = employees.sumOf { it.totalSales }
    val totalCommissions = employees.sumOf { it.totalSales * (it.commissionRate / 100) }

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
                text = { Text("Equipe & Produtividade", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("CRM & Canais", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (selectedTab == 0) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BentoMetricCard(
                            title = "VENDAS DA EQUIPE",
                            value = "R$ ${String.format("%.2f", totalTeamSales)}",
                            icon = Icons.Default.Leaderboard,
                            badge = "${employees.size} MEMBROS",
                            modifier = Modifier.weight(1f)
                        )

                        BentoMetricCard(
                            title = "COMISSÕES TOTAIS",
                            value = "R$ ${String.format("%.2f", totalCommissions)}",
                            icon = Icons.Default.MonetizationOn,
                            badge = "A PAGAR",
                            isHighlighted = true,
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
                            Text("Ranking e Produtividade da Equipe", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
                            Text("Gestão de colaboradores e comissionamentos", fontSize = 11.sp, color = TextSecondary)
                        }

                        Button(
                            onClick = { showAddEmployeeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Novo Membro", fontSize = 12.sp)
                        }
                    }
                }

                itemsIndexed(employees.sortedByDescending { it.totalSales }) { index, emp ->
                    val commission = emp.totalSales * (emp.commissionRate / 100)
                    val isTop1 = index == 0

                    BentoCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = if (isTop1) BentoCardVariant.WineTinted else BentoCardVariant.Default,
                        cornerRadius = 20.dp,
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
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isTop1) WinePrimary else WineContainer)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isTop1) {
                                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                        } else {
                                            Text("${index + 1}º", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WinePrimary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                        Text("Cargo: ${emp.role} • Taxa: ${String.format("%.1f", emp.commissionRate)}%", fontSize = 11.sp, color = TextSecondary)
                                        Text("${emp.ordersCount} pedidos • ${emp.tablesServed} mesas atendidas", fontSize = 11.sp, color = TextTertiary)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("R$ ${String.format("%.2f", emp.totalSales)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WinePrimary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(GoldContainer)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "Comissão: R$ ${String.format("%.2f", commission)}",
                                            fontSize = 10.sp,
                                            color = GoldDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { selectedEmployeeToEdit = emp },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardWhite),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Editar", fontSize = 11.sp, color = WinePrimary, fontWeight = FontWeight.SemiBold)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { employeeToDelete = emp },
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
        } else {
            val totalCustomerSpend = customers.sumOf { it.totalSpent }
            val avgCustomerSpend = if (customers.isNotEmpty()) totalCustomerSpend / customers.size else 0.0

            LazyColumn(
                contentPadding = PaddingValues(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BentoMetricCard(
                            title = "CLIENTES NA BASE",
                            value = "${customers.size}",
                            icon = Icons.Default.Group,
                            badge = "+28% ESTE MÊS",
                            badgeColor = StatusGreen,
                            modifier = Modifier.weight(1f)
                        )

                        BentoMetricCard(
                            title = "TICKET MÉDIO CRM",
                            value = "R$ ${String.format("%.2f", avgCustomerSpend)}",
                            icon = Icons.Default.MonetizationOn,
                            badge = "POR CLIENTE",
                            isHighlighted = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    BentoCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = BentoCardVariant.Default,
                        cornerRadius = 20.dp,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            BentoCardHeader(
                                title = "Desempenho por Canal de Aquisição",
                                subtitle = "Onde os clientes mais frequentes conheceram o Santo Barril",
                                icon = Icons.Default.Campaign
                            )

                            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                            channelStats.forEach { ch ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(WinePrimary)
                                                .border(1.dp, BorderSubtle, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(ch.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("R$ ${String.format("%.2f", ch.totalRevenue)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WineDark)
                                        Text("${ch.customerCount} clientes • Méd: R$ ${String.format("%.2f", ch.averageTicket)}", fontSize = 10.sp, color = TextTertiary)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Clientes Mais Frequentes & VIPs", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
                }

                items(customers.sortedByDescending { it.visitCount }.take(10)) { customer ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(WineContainer)
                                        .border(1.dp, BorderSubtle, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("Canal: ${customer.originChannel} • ${customer.visitCount} visitas", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("R$ ${String.format("%.2f", customer.totalSpent)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StatusGreen)
                                Text("Gasto Acumulado", fontSize = 10.sp, color = TextTertiary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddEmployeeDialog) {
        var name by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("Garçom") }
        var phone by remember { mutableStateOf("") }
        var commissionStr by remember { mutableStateOf("10.0") }

        AlertDialog(
            onDismissRequest = { showAddEmployeeDialog = false },
            title = {
                Text("Cadastrar Novo Membro da Equipe", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Cargo") },
                        placeholder = { Text("Garçom, Chefe de Bar, Cozinheiro") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone / WhatsApp") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = commissionStr,
                        onValueChange = { commissionStr = it },
                        label = { Text("Taxa de Comissão (%)") },
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
                        val comm = commissionStr.replace(",", ".").toDoubleOrNull() ?: 10.0
                        if (name.isNotBlank()) {
                            onAddEmployee?.invoke(name, role, phone, comm)
                            showAddEmployeeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cadastrar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddEmployeeDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val editingEmployee = selectedEmployeeToEdit
    if (editingEmployee != null) {
        var name by remember(editingEmployee) { mutableStateOf(editingEmployee.name) }
        var role by remember(editingEmployee) { mutableStateOf(editingEmployee.role) }
        var phone by remember(editingEmployee) { mutableStateOf(editingEmployee.phone) }
        var commissionStr by remember(editingEmployee) { mutableStateOf(editingEmployee.commissionRate.toString()) }

        AlertDialog(
            onDismissRequest = { selectedEmployeeToEdit = null },
            title = {
                Text("Editar Colaborador", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Cargo") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = commissionStr,
                        onValueChange = { commissionStr = it },
                        label = { Text("Comissão (%)") },
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
                        val comm = commissionStr.replace(",", ".").toDoubleOrNull() ?: editingEmployee.commissionRate
                        if (name.isNotBlank()) {
                            onUpdateEmployee?.invoke(
                                editingEmployee.copy(name = name, role = role, phone = phone, commissionRate = comm)
                            )
                            selectedEmployeeToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Salvar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedEmployeeToEdit = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val deletingEmployee = employeeToDelete
    if (deletingEmployee != null) {
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            title = { Text("Excluir Colaborador", fontWeight = FontWeight.Bold, color = StatusRed) },
            text = { Text("Deseja remover '${deletingEmployee.name}' da equipe?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteEmployee?.invoke(deletingEmployee)
                        employeeToDelete = null
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
                    onClick = { employeeToDelete = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private data class ChannelData(
    val name: String,
    val customerCount: Int,
    val totalRevenue: Double,
    val averageTicket: Double
)
