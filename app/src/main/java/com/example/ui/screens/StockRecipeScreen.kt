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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.IngredientEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductionSector
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.SupplierEntity
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.BentoMetricCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
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

@Composable
fun StockRecipeScreen(
    ingredients: List<IngredientEntity>,
    products: List<ProductEntity>,
    suppliers: List<SupplierEntity>,
    purchaseOrders: List<PurchaseOrderEntity>,
    onAddIngredient: ((name: String, unit: String, currentStock: Double, minStock: Double, costPerUnit: Double) -> Unit)? = null,
    onUpdateIngredient: ((IngredientEntity) -> Unit)? = null,
    onAdjustStock: ((ingredientId: Long, newStock: Double, reason: String) -> Unit)? = null,
    onUpdateIngredientStock: ((ingredientId: Long, newStock: Double, reason: String) -> Unit)? = onAdjustStock,
    onDeleteIngredient: ((IngredientEntity) -> Unit)? = null,
    onAddSupplier: ((name: String, contactPerson: String, phone: String, productsSupplied: String, deliveryDays: Int) -> Unit)? = null,
    onUpdateSupplier: ((SupplierEntity) -> Unit)? = null,
    onDeleteSupplier: ((SupplierEntity) -> Unit)? = null,
    onAddPurchaseOrder: ((supplierName: String, totalAmount: Double, notes: String) -> Unit)? = null,
    onUpdateProduct: ((ProductEntity) -> Unit)? = null,
    onSaveProduct: ((ProductEntity) -> Unit)? = onUpdateProduct,
    onAddProduct: ((name: String, description: String, price: Double, costPrice: Double, category: String, sector: ProductionSector) -> Unit)? = null,
    onDeleteProduct: ((ProductEntity) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Estoque, 1: Previsão de Compra, 2: Fichas Técnicas & Cardápio, 3: Fornecedores

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
                text = { Text("Estoque", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Previsão", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Cardápio & Fichas", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Fornecedores", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedTab) {
            0 -> StockInventoryView(
                ingredients = ingredients,
                onAddIngredient = onAddIngredient,
                onUpdateIngredient = onUpdateIngredient,
                onUpdateIngredientStock = onUpdateIngredientStock ?: onAdjustStock,
                onDeleteIngredient = onDeleteIngredient
            )
            1 -> PurchaseForecastView(
                ingredients = ingredients,
                suppliers = suppliers,
                onAddPurchaseOrder = onAddPurchaseOrder
            )
            2 -> RecipeCostView(
                products = products,
                onSaveProduct = onSaveProduct ?: onUpdateProduct,
                onAddProduct = onAddProduct,
                onDeleteProduct = onDeleteProduct
            )
            3 -> SuppliersPurchasesView(
                suppliers = suppliers,
                purchaseOrders = purchaseOrders,
                onAddSupplier = onAddSupplier,
                onUpdateSupplier = onUpdateSupplier,
                onDeleteSupplier = onDeleteSupplier,
                onAddPurchaseOrder = onAddPurchaseOrder
            )
        }
    }
}

/**
 * Tab 0: Estoque Físico & Insumos (Preenchimento e Edição pelo Gerente)
 */
@Composable
private fun StockInventoryView(
    ingredients: List<IngredientEntity>,
    onAddIngredient: ((name: String, unit: String, currentStock: Double, minStock: Double, costPerUnit: Double) -> Unit)?,
    onUpdateIngredient: ((IngredientEntity) -> Unit)?,
    onUpdateIngredientStock: ((ingredientId: Long, newStock: Double, reason: String) -> Unit)?,
    onDeleteIngredient: ((IngredientEntity) -> Unit)?
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedIngredientToAdjust by remember { mutableStateOf<IngredientEntity?>(null) }
    var selectedIngredientToEdit by remember { mutableStateOf<IngredientEntity?>(null) }
    var ingredientToDelete by remember { mutableStateOf<IngredientEntity?>(null) }

    val lowStockCount = ingredients.count { it.currentStock <= it.minStock }
    val totalStockValue = ingredients.sumOf { it.currentStock * it.costPerUnit }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoMetricCard(
                    title = "ITENS CADASTRADOS",
                    value = "${ingredients.size}",
                    icon = Icons.Default.Inventory,
                    badge = "INSUMOS",
                    modifier = Modifier.weight(1f)
                )

                BentoMetricCard(
                    title = "ALERTA ESTOQUE",
                    value = "$lowStockCount",
                    icon = Icons.Default.Warning,
                    badge = if (lowStockCount > 0) "REPOR" else "OK",
                    badgeColor = if (lowStockCount > 0) StatusRed else StatusGreen,
                    isHighlighted = lowStockCount > 0,
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
                    Text("Gestão de Insumos & Contagem Física", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
                    Text("Patrimônio estimado: R$ ${String.format("%.2f", totalStockValue)}", fontSize = 11.sp, color = TextSecondary)
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo Insumo", fontSize = 12.sp)
                }
            }
        }

        items(ingredients) { ing ->
            val isLow = ing.currentStock <= ing.minStock

            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = if (isLow) BentoCardVariant.WineTinted else BentoCardVariant.Default,
                cornerRadius = 16.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(ing.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                if (isLow) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(StatusRedContainer)
                                            .border(1.dp, StatusRed.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Estoque Baixo", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Unidade: ${ing.unit} • Custo: R$ ${String.format("%.2f", ing.costPerUnit)}/${ing.unit}", fontSize = 11.sp, color = TextSecondary)
                            Text("Estoque Mínimo Definido: ${ing.minStock} ${ing.unit}", fontSize = 11.sp, color = TextTertiary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${String.format("%.1f", ing.currentStock)} ${ing.unit}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isLow) StatusRed else StatusGreen
                            )
                            val totalValue = ing.currentStock * ing.costPerUnit
                            Text("Total: R$ ${String.format("%.2f", totalValue)}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ações do Gerente:",
                            fontSize = 11.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { selectedIngredientToAdjust = ing },
                                colors = ButtonDefaults.buttonColors(containerColor = WineContainer),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Atualizar Estoque", fontSize = 11.sp, color = WinePrimary, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { selectedIngredientToEdit = ing },
                                colors = ButtonDefaults.buttonColors(containerColor = CardWhite),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }

                            IconButton(
                                onClick = { ingredientToDelete = ing },
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

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("kg") }
        var currentStockStr by remember { mutableStateOf("") }
        var minStockStr by remember { mutableStateOf("5.0") }
        var costPerUnitStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text("Cadastrar Novo Insumo", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Insumo") },
                        placeholder = { Text("Ex: Picanha Angus, Queijo Coalho") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidade (kg, g, un, ml, L)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currentStockStr,
                            onValueChange = { currentStockStr = it },
                            label = { Text("Estoque Atual") },
                            placeholder = { Text("Ex: 15.0") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = minStockStr,
                            onValueChange = { minStockStr = it },
                            label = { Text("Estoque Mín.") },
                            placeholder = { Text("Ex: 5.0") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = costPerUnitStr,
                        onValueChange = { costPerUnitStr = it },
                        label = { Text("Custo por Unidade (R$)") },
                        placeholder = { Text("Ex: 48.50") },
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
                        val curr = currentStockStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val min = minStockStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val cost = costPerUnitStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank()) {
                            onAddIngredient?.invoke(name, unit, curr, min, cost)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Salvar Insumo", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val adjustingIngredient = selectedIngredientToAdjust
    if (adjustingIngredient != null) {
        var newStockStr by remember(adjustingIngredient) { mutableStateOf(adjustingIngredient.currentStock.toString()) }
        var reason by remember(adjustingIngredient) { mutableStateOf("Balanço e Contagem Física") }

        AlertDialog(
            onDismissRequest = { selectedIngredientToAdjust = null },
            title = {
                Text("Atualizar Estoque: ${adjustingIngredient.name}", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Estoque Atual no Sistema: ${String.format("%.1f", adjustingIngredient.currentStock)} ${adjustingIngredient.unit}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Quick adjustment chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(1.0, 5.0, 10.0, -1.0).forEach { delta ->
                            OutlinedButton(
                                onClick = {
                                    val cur = newStockStr.replace(",", ".").toDoubleOrNull() ?: adjustingIngredient.currentStock
                                    val next = (cur + delta).coerceAtLeast(0.0)
                                    newStockStr = String.format(java.util.Locale.US, "%.1f", next)
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(if (delta > 0) "+$delta" else "$delta", fontSize = 10.sp)
                            }
                        }
                        OutlinedButton(
                            onClick = { newStockStr = "0.0" },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Zerar", fontSize = 10.sp, color = StatusRed)
                        }
                    }

                    OutlinedTextField(
                        value = newStockStr,
                        onValueChange = { newStockStr = it },
                        label = { Text("Nova Quantidade Contada (${adjustingIngredient.unit}) *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Motivo do Ajuste *") },
                        placeholder = { Text("Ex: Entrada de NF, Balanço, Perda") },
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
                        val newStock = newStockStr.replace(",", ".").toDoubleOrNull()
                        if (newStock != null) {
                            onUpdateIngredientStock?.invoke(adjustingIngredient.id, newStock, reason.ifBlank { "Ajuste de Estoque" })
                            selectedIngredientToAdjust = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Atualizar Estoque", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedIngredientToAdjust = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val editingIngredient = selectedIngredientToEdit
    if (editingIngredient != null) {
        var name by remember(editingIngredient) { mutableStateOf(editingIngredient.name) }
        var unit by remember(editingIngredient) { mutableStateOf(editingIngredient.unit) }
        var minStockStr by remember(editingIngredient) { mutableStateOf(editingIngredient.minStock.toString()) }
        var costPerUnitStr by remember(editingIngredient) { mutableStateOf(editingIngredient.costPerUnit.toString()) }

        AlertDialog(
            onDismissRequest = { selectedIngredientToEdit = null },
            title = {
                Text("Editar Insumo", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
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
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidade") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = minStockStr,
                            onValueChange = { minStockStr = it },
                            label = { Text("Estoque Mín.") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = costPerUnitStr,
                            onValueChange = { costPerUnitStr = it },
                            label = { Text("Custo Unit. (R$)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val min = minStockStr.replace(",", ".").toDoubleOrNull() ?: editingIngredient.minStock
                        val cost = costPerUnitStr.replace(",", ".").toDoubleOrNull() ?: editingIngredient.costPerUnit
                        if (name.isNotBlank()) {
                            onUpdateIngredient?.invoke(
                                editingIngredient.copy(
                                    name = name,
                                    unit = unit,
                                    minStock = min,
                                    costPerUnit = cost
                                )
                            )
                            selectedIngredientToEdit = null
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
                    onClick = { selectedIngredientToEdit = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val deletingIngredient = ingredientToDelete
    if (deletingIngredient != null) {
        AlertDialog(
            onDismissRequest = { ingredientToDelete = null },
            title = { Text("Excluir Insumo", fontWeight = FontWeight.Bold, color = StatusRed) },
            text = { Text("Tem certeza que deseja remover '${deletingIngredient.name}' do controle de estoque?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteIngredient?.invoke(deletingIngredient)
                        ingredientToDelete = null
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
                    onClick = { ingredientToDelete = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Tab 1: Previsão de Compra
 */
private data class ForecastItem(
    val ingredient: IngredientEntity,
    val estimatedWeeklyConsumption: Double,
    val suggestedBuyQty: Double,
    val estimatedCost: Double,
    val criticalityLevel: Int
)

@Composable
private fun PurchaseForecastView(
    ingredients: List<IngredientEntity>,
    suppliers: List<SupplierEntity>,
    onAddPurchaseOrder: ((supplierName: String, totalAmount: Double, notes: String) -> Unit)? = null
) {
    var showExportDialog by remember { mutableStateOf(false) }
    var showRegisterOrderDialog by remember { mutableStateOf(false) }

    val forecastList = ingredients.map { ing ->
        val weeklyUse = (ing.minStock * 1.35).coerceAtLeast(1.0)
        val neededToRestore = ((ing.minStock * 2.0) - ing.currentStock).coerceAtLeast(0.0)
        val suggestedBuy = if (neededToRestore > 0.0) neededToRestore else 0.0
        val cost = suggestedBuy * ing.costPerUnit

        val level = when {
            ing.currentStock <= ing.minStock * 0.4 -> 0
            ing.currentStock <= ing.minStock -> 1
            else -> 2
        }

        ForecastItem(
            ingredient = ing,
            estimatedWeeklyConsumption = weeklyUse,
            suggestedBuyQty = suggestedBuy,
            estimatedCost = cost,
            criticalityLevel = level
        )
    }.sortedBy { it.criticalityLevel }

    val criticalCount = forecastList.count { it.criticalityLevel == 0 }
    val totalSuggestedCost = forecastList.sumOf { it.estimatedCost }

    fun buildExportText(): String {
        val sb = StringBuilder()
        sb.appendLine("*PEDIDO DE COMPRA — SANTO BARRIL*")
        sb.appendLine("Previsão e Sugestão de Reposição de Estoque")
        sb.appendLine("----------------------------------")
        val needsBuy = forecastList.filter { it.suggestedBuyQty > 0 }
        if (needsBuy.isEmpty()) {
            sb.appendLine("Nenhum item precisa de reposição no momento.")
        } else {
            needsBuy.forEach { item ->
                val urgency = if (item.criticalityLevel == 0) "[URGENTE]" else "[REPOSIÇÃO]"
                sb.appendLine("$urgency ${item.ingredient.name}: ${String.format("%.1f", item.suggestedBuyQty)} ${item.ingredient.unit} (Est. R$ ${String.format("%.2f", item.estimatedCost)})")
            }
        }
        sb.appendLine("----------------------------------")
        sb.appendLine("Total Estimado: R$ ${String.format("%.2f", totalSuggestedCost)}")
        return sb.toString()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoMetricCard(
                    title = "ITENS CRÍTICOS",
                    value = "$criticalCount",
                    icon = Icons.Default.Warning,
                    badge = "COMPRAR HOJE",
                    badgeColor = if (criticalCount > 0) StatusRed else StatusGreen,
                    isHighlighted = criticalCount > 0,
                    modifier = Modifier.weight(1f)
                )

                BentoMetricCard(
                    title = "INVESTIMENTO SUGERIDO",
                    value = "R$ ${String.format("%.2f", totalSuggestedCost)}",
                    icon = Icons.Default.LocalShipping,
                    badge = "CICLO SEMANAL",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exportar Lista", fontSize = 12.sp, color = WinePrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showRegisterOrderDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lançar Compra", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(forecastList) { item ->
            val statusColor = when (item.criticalityLevel) {
                0 -> StatusRed
                1 -> Color(0xFFF59E0B)
                else -> StatusGreen
            }

            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = 14.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    .background(statusColor)
                                    .border(1.dp, BorderSubtle, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(item.ingredient.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("Giro Semanal Médio: ~${String.format("%.1f", item.estimatedWeeklyConsumption)} ${item.ingredient.unit}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Atual: ${String.format("%.1f", item.ingredient.currentStock)} ${item.ingredient.unit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                            Text("Mínimo: ${item.ingredient.minStock} ${item.ingredient.unit}", fontSize = 10.sp, color = TextTertiary)
                        }
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.suggestedBuyQty > 0) {
                            Text(
                                text = "Comprar: +${String.format("%.1f", item.suggestedBuyQty)} ${item.ingredient.unit}",
                                fontSize = 12.sp,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Est. R$ ${String.format("%.2f", item.estimatedCost)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        } else {
                            Text(
                                text = "Estoque suficiente para o ciclo atual",
                                fontSize = 11.sp,
                                color = StatusGreen,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "R$ 0,00",
                                fontSize = 12.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        val exportText = buildExportText()
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text("Lista de Pedido de Compra", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 15.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(OffWhiteBackground)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(text = exportText, fontSize = 11.sp, color = TextPrimary, lineHeight = 16.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Concluir", fontSize = 12.sp)
                }
            }
        )
    }

    if (showRegisterOrderDialog) {
        var supplierName by remember { mutableStateOf(suppliers.firstOrNull()?.name ?: "Distribuidora de Carnes") }
        var amountStr by remember { mutableStateOf(String.format("%.2f", totalSuggestedCost).replace(",", ".")) }
        var notes by remember { mutableStateOf("Reposição semanal de insumos críticos") }

        AlertDialog(
            onDismissRequest = { showRegisterOrderDialog = false },
            title = {
                Text("Lançar Pedido de Compra no Sistema", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = supplierName,
                        onValueChange = { supplierName = it },
                        label = { Text("Fornecedor") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Valor Total do Pedido (R$)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações / Itens") },
                        maxLines = 3,
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
                        val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0 && supplierName.isNotBlank()) {
                            onAddPurchaseOrder?.invoke(supplierName, amount, notes)
                            showRegisterOrderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Registrar Pedido", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRegisterOrderDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Tab 2: Fichas Técnicas & Gestão Completa de Produtos (CRUD)
 */
@Composable
private fun RecipeCostView(
    products: List<ProductEntity>,
    onSaveProduct: ((ProductEntity) -> Unit)? = null,
    onAddProduct: ((name: String, description: String, price: Double, costPrice: Double, category: String, sector: ProductionSector) -> Unit)? = null,
    onDeleteProduct: ((ProductEntity) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Todas") }
    var selectedSectorFilter by remember { mutableStateOf<ProductionSector?>(null) }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var selectedProductToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val categories = remember(products) {
        listOf("Todas") + products.map { it.category }.distinct().filter { it.isNotBlank() }
    }

    val filteredProducts = products.filter { prod ->
        val matchesSearch = searchQuery.isBlank() ||
                prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.code.contains(searchQuery, ignoreCase = true) ||
                prod.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "Todas" || prod.category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesSector = selectedSectorFilter == null || prod.productionSector == selectedSectorFilter
        matchesSearch && matchesCategory && matchesSector
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Gestão de Produtos & Fichas",
                        fontWeight = FontWeight.Bold,
                        color = WinePrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        "${filteredProducts.size} de ${products.size} itens listados",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showAddProductDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo Produto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por nome, código ou categoria...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WinePrimary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WinePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Sector Filter Chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Setor:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                FilterChip(
                    selected = selectedSectorFilter == null,
                    onClick = { selectedSectorFilter = null },
                    label = { Text("Todos", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WineContainer,
                        selectedLabelColor = WineDark
                    )
                )
                ProductionSector.entries.forEach { sector ->
                    val isSelected = selectedSectorFilter == sector
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSectorFilter = if (isSelected) null else sector },
                        label = { Text(sector.name, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WineContainer,
                            selectedLabelColor = WineDark
                        )
                    )
                }
            }
        }

        // Product Cards
        items(filteredProducts) { prod ->
            val cmvPercent = if (prod.price > 0) (prod.costPrice / prod.price) * 100 else 0.0
            val profit = prod.price - prod.costPrice

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
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (prod.code.isNotBlank()) {
                                    Text(
                                        "[${prod.code}] ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                }
                                Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Cat: ${prod.category}", fontSize = 11.sp, color = TextSecondary)
                                Text("•", fontSize = 11.sp, color = TextTertiary)
                                Text("Setor: ${prod.productionSector.name}", fontSize = 11.sp, color = WinePrimary, fontWeight = FontWeight.Medium)
                            }
                            if (prod.description.isNotBlank()) {
                                Text(
                                    prod.description,
                                    fontSize = 10.sp,
                                    color = TextTertiary,
                                    maxLines = 1
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "R$ ${String.format("%.2f", prod.price)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = WinePrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (cmvPercent > 40) StatusRedContainer else StatusGreenContainer)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "CMV ${String.format("%.0f", cmvPercent)}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (cmvPercent > 40) StatusRed else StatusGreen
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Lucro: R$ ${String.format("%.2f", profit)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = StatusGreen)
                        }
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Availability Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (prod.isAvailable) StatusGreenContainer else StatusRedContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (prod.isAvailable) "Disponível" else "Pausado",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (prod.isAvailable) StatusGreen else StatusRed
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Custo: R$ ${String.format("%.2f", prod.costPrice)}",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }

                        // Edit / Delete Actions
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { selectedProductToEdit = prod },
                                colors = ButtonDefaults.buttonColors(containerColor = WineContainer),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar Produto", fontSize = 11.sp, color = WinePrimary, fontWeight = FontWeight.SemiBold)
                            }

                            if (onDeleteProduct != null) {
                                IconButton(
                                    onClick = { productToDelete = prod },
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

    // DIALOG: Cadastrar Novo Produto
    if (showAddProductDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Espetinhos") }
        var sector by remember { mutableStateOf(ProductionSector.CHURRASQUEIRA) }
        var priceStr by remember { mutableStateOf("") }
        var costPriceStr by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        val commonCategories = listOf("Espetinhos", "Porções", "Comidas", "Hambúrgueres", "Chopp", "Cervejas", "Drinks", "Bebidas", "Sobremesas")

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = {
                Text("Cadastrar Novo Produto", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Produto *") },
                        placeholder = { Text("Ex: Picanha na Brasa com Farofa") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Código/SKU") },
                            placeholder = { Text("Ex: ESP-01") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Categoria") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Quick category chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(commonCategories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 9.5.sp) }
                            )
                        }
                    }

                    // Setor de Produção
                    Text("Setor de Preparo:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProductionSector.entries.forEach { sec ->
                            FilterChip(
                                selected = sector == sec,
                                onClick = { sector = sec },
                                label = { Text(sec.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("Preço Venda (R$) *") },
                            placeholder = { Text("32.90") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = costPriceStr,
                            onValueChange = { costPriceStr = it },
                            label = { Text("Custo Insumo (R$)") },
                            placeholder = { Text("11.50") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição / Ficha") },
                        placeholder = { Text("Acompanha vinagrete e farofa...") },
                        maxLines = 2,
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
                        val price = priceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val cost = costPriceStr.replace(",", ".").toDoubleOrNull() ?: (price * 0.35)
                        if (name.isNotBlank() && price > 0) {
                            onAddProduct?.invoke(
                                name,
                                description,
                                price,
                                cost,
                                category.ifBlank { "Geral" },
                                sector
                            )
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cadastrar Produto", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddProductDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Editar Produto Completo (Wired to saveProduct)
    val editingProduct = selectedProductToEdit
    if (editingProduct != null) {
        var name by remember(editingProduct) { mutableStateOf(editingProduct.name) }
        var code by remember(editingProduct) { mutableStateOf(editingProduct.code) }
        var category by remember(editingProduct) { mutableStateOf(editingProduct.category) }
        var sector by remember(editingProduct) { mutableStateOf(editingProduct.productionSector) }
        var salePriceStr by remember(editingProduct) { mutableStateOf(editingProduct.price.toString()) }
        var costPriceStr by remember(editingProduct) { mutableStateOf(editingProduct.costPrice.toString()) }
        var description by remember(editingProduct) { mutableStateOf(editingProduct.description) }
        var isAvailable by remember(editingProduct) { mutableStateOf(editingProduct.isAvailable) }

        AlertDialog(
            onDismissRequest = { selectedProductToEdit = null },
            title = {
                Text("Editar Produto: ${editingProduct.name}", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Produto") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Código") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Categoria") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Setor de Produção
                    Text("Setor de Preparo:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProductionSector.entries.forEach { sec ->
                            FilterChip(
                                selected = sector == sec,
                                onClick = { sector = sec },
                                label = { Text(sec.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = salePriceStr,
                            onValueChange = { salePriceStr = it },
                            label = { Text("Preço Venda (R$)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = costPriceStr,
                            onValueChange = { costPriceStr = it },
                            label = { Text("Custo Insumo (R$)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição / Composição") },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Toggle Disponível no Cardápio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Disponível no Cardápio:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { isAvailable = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WinePrimary,
                                checkedTrackColor = WineContainer
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val salePrice = salePriceStr.replace(",", ".").toDoubleOrNull() ?: editingProduct.price
                        val costPrice = costPriceStr.replace(",", ".").toDoubleOrNull() ?: editingProduct.costPrice
                        val updated = editingProduct.copy(
                            name = name.ifBlank { editingProduct.name },
                            code = code,
                            category = category.ifBlank { editingProduct.category },
                            productionSector = sector,
                            price = salePrice,
                            costPrice = costPrice,
                            description = description,
                            isAvailable = isAvailable
                        )
                        onSaveProduct?.invoke(updated)
                        selectedProductToEdit = null
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
                    onClick = { selectedProductToEdit = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Confirmar Exclusão de Produto
    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Excluir Produto", fontWeight = FontWeight.Bold, color = StatusRed) },
            text = {
                Text("Tem certeza de que deseja remover o produto '${prod.name}' do cardápio e do banco de dados?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct?.invoke(prod)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sim, Excluir", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { productToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Tab 3: Fornecedores & Pedidos de Compra
 */
@Composable
private fun SuppliersPurchasesView(
    suppliers: List<SupplierEntity>,
    purchaseOrders: List<PurchaseOrderEntity>,
    onAddSupplier: ((name: String, contactPerson: String, phone: String, productsSupplied: String, deliveryDays: Int) -> Unit)? = null,
    onUpdateSupplier: ((SupplierEntity) -> Unit)? = null,
    onDeleteSupplier: ((SupplierEntity) -> Unit)? = null,
    onAddPurchaseOrder: ((supplierName: String, totalAmount: Double, notes: String) -> Unit)? = null
) {
    var showAddSupplierDialog by remember { mutableStateOf(false) }
    var selectedSupplierToEdit by remember { mutableStateOf<SupplierEntity?>(null) }
    var supplierToDelete by remember { mutableStateOf<SupplierEntity?>(null) }
    var showAddOrderDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Fornecedores Homologados", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
                    Text("${suppliers.size} empresas cadastradas", fontSize = 11.sp, color = TextSecondary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showAddOrderDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Novo Pedido", fontSize = 12.sp, color = WinePrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showAddSupplierDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fornecedor", fontSize = 12.sp)
                    }
                }
            }
        }

        items(suppliers) { sup ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sup.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text("Fornece: ${sup.productsSupplied}", fontSize = 11.sp, color = TextSecondary)
                            Text("Contato: ${sup.contactPerson} • ${sup.phone}", fontSize = 11.sp, color = TextSecondary)
                            Text("Prazo médio de entrega: ${sup.deliveryDays} dias", fontSize = 10.sp, color = TextTertiary)
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WineContainer)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(20.dp))
                        }
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { selectedSupplierToEdit = sup },
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
                            onClick = { supplierToDelete = sup },
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

        if (purchaseOrders.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Histórico Recente de Compras", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 14.sp)
            }

            items(purchaseOrders) { order ->
                BentoCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = BentoCardVariant.Default,
                    cornerRadius = 14.dp,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(order.supplierName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Total: R$ ${String.format("%.2f", order.totalAmount)}", fontSize = 12.sp, color = WinePrimary, fontWeight = FontWeight.SemiBold)
                            if (order.description.isNotBlank()) {
                                Text(order.description, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (order.status == "Recebido" || order.status == "ENTREGUE") StatusGreenContainer else WineContainer)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                order.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (order.status == "Recebido" || order.status == "ENTREGUE") StatusGreen else WinePrimary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddSupplierDialog) {
        var name by remember { mutableStateOf("") }
        var contactPerson by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var leadTimeStr by remember { mutableStateOf("2") }

        AlertDialog(
            onDismissRequest = { showAddSupplierDialog = false },
            title = {
                Text("Cadastrar Novo Fornecedor", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Empresa") },
                        placeholder = { Text("Ex: Frigorífico Central") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Produtos Fornecidos") },
                        placeholder = { Text("Ex: Carnes Angus, Carvão vegetal") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Contato") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefone / WhatsApp") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = leadTimeStr,
                        onValueChange = { leadTimeStr = it },
                        label = { Text("Prazo Médio de Entrega (dias)") },
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
                        val leadTime = leadTimeStr.toIntOrNull() ?: 2
                        if (name.isNotBlank()) {
                            onAddSupplier?.invoke(name, contactPerson, phone, category, leadTime)
                            showAddSupplierDialog = false
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
                    onClick = { showAddSupplierDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val editingSupplier = selectedSupplierToEdit
    if (editingSupplier != null) {
        var name by remember(editingSupplier) { mutableStateOf(editingSupplier.name) }
        var contactPerson by remember(editingSupplier) { mutableStateOf(editingSupplier.contactPerson) }
        var phone by remember(editingSupplier) { mutableStateOf(editingSupplier.phone) }
        var category by remember(editingSupplier) { mutableStateOf(editingSupplier.productsSupplied) }
        var leadTimeStr by remember(editingSupplier) { mutableStateOf(editingSupplier.deliveryDays.toString()) }

        AlertDialog(
            onDismissRequest = { selectedSupplierToEdit = null },
            title = {
                Text("Editar Fornecedor", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Empresa") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Produtos Fornecidos") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Contato") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefone") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = leadTimeStr,
                        onValueChange = { leadTimeStr = it },
                        label = { Text("Prazo de Entrega (dias)") },
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
                        val leadTime = leadTimeStr.toIntOrNull() ?: editingSupplier.deliveryDays
                        if (name.isNotBlank()) {
                            onUpdateSupplier?.invoke(
                                editingSupplier.copy(
                                    name = name,
                                    contactPerson = contactPerson,
                                    phone = phone,
                                    productsSupplied = category,
                                    deliveryDays = leadTime
                                )
                            )
                            selectedSupplierToEdit = null
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
                    onClick = { selectedSupplierToEdit = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val deletingSupplier = supplierToDelete
    if (deletingSupplier != null) {
        AlertDialog(
            onDismissRequest = { supplierToDelete = null },
            title = { Text("Excluir Fornecedor", fontWeight = FontWeight.Bold, color = StatusRed) },
            text = { Text("Deseja remover '${deletingSupplier.name}' da lista de fornecedores homologados?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSupplier?.invoke(deletingSupplier)
                        supplierToDelete = null
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
                    onClick = { supplierToDelete = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showAddOrderDialog) {
        var supplierName by remember { mutableStateOf(suppliers.firstOrNull()?.name ?: "") }
        var amountStr by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddOrderDialog = false },
            title = {
                Text("Lançar Pedido de Compra", fontWeight = FontWeight.Bold, color = WinePrimary, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = supplierName,
                        onValueChange = { supplierName = it },
                        label = { Text("Nome do Fornecedor") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Valor Total (R$)") },
                        placeholder = { Text("Ex: 1250.00") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações / Insumos") },
                        placeholder = { Text("Ex: 30kg de Picanha, 10 fardos de carvão") },
                        maxLines = 3,
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
                        val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (supplierName.isNotBlank() && amount > 0) {
                            onAddPurchaseOrder?.invoke(supplierName, amount, notes)
                            showAddOrderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Lançar Compra", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddOrderDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
