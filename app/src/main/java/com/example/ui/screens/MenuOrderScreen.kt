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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.data.model.ProductionSector
import com.example.data.model.RestaurantTableEntity
import com.example.data.repository.RestaurantRepository
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderScreen(
    products: List<ProductEntity>,
    tables: List<RestaurantTableEntity>,
    selectedTableNumber: Int?,
    cartItems: List<RestaurantRepository.CartItem>,
    onAddToCart: (ProductEntity, String) -> Unit,
    onUpdateQuantity: (ProductEntity, String, Int) -> Unit,
    onClearCart: () -> Unit,
    onDispatchOrder: (tableNumber: Int, customerName: String, customerPhone: String) -> Unit,
    onSelectTable: (Int) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var showCartSheet by remember { mutableStateOf(false) }

    // Notes dialog state for product before adding
    var productForNotes by remember { mutableStateOf<ProductEntity?>(null) }
    var observationText by remember { mutableStateOf("") }

    val categories = listOf(
        "Todos", "Espetinhos", "Porções", "Comidas", "Hambúrgueres",
        "Chopp", "Cervejas", "Drinks", "Bebidas", "Refrigerantes", "Sobremesas", "Combos"
    )

    val currentTable = tables.find { it.number == selectedTableNumber }
    val cartTotal = cartItems.sumOf { it.product.price * it.quantity }
    val cartCount = cartItems.sumOf { it.quantity }

    val filteredProducts = products.filter { product ->
        val matchesCategory = (selectedCategory == "Todos" || product.category == selectedCategory)
        val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true) ||
                product.code.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        // Top Header: Active Table & Waiter Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = WinePrimary)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (currentTable != null) "${currentTable.name} • ${currentTable.customerName.ifBlank { "Atendimento" }}" else "Selecione uma Mesa",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WinePrimary
                            )
                        )
                        Text(
                            text = if (currentTable != null) "Garçom: ${currentTable.waiterName.ifBlank { "Livre" }}" else "Toque para vincular mesa",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                // Table Selector Dropdown / Change
                OutlinedButton(
                    onClick = {
                        val next = tables.firstOrNull { it.number != selectedTableNumber }
                        if (next != null) onSelectTable(next.number)
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (currentTable != null) "Mesa ${currentTable.number}" else "Mesa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WinePrimary
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Pesquisar espetinhos, chopps, porções...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WinePrimary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor = WinePrimary,
                unfocusedBorderColor = BorderSubtle
            )
        )

        // Categories Horizontal Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WinePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = CardWhite,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) WinePrimary else BorderSubtle
                    )
                )
            }
        }

        // Product List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                ProductItemCard(
                    product = product,
                    onAdd = {
                        productForNotes = product
                        observationText = ""
                    }
                )
            }
        }

        // Bottom Bar Cart Summary (Mesa -> Conferir -> Enviar)
        if (cartCount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = WinePrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$cartCount", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Itens no Carrinho",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            "Subtotal: R$ ${String.format("%.2f", cartTotal)}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { showCartSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Conferir & Enviar",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // DIALOG: Observation before adding item
    productForNotes?.let { prod ->
        AlertDialog(
            onDismissRequest = { productForNotes = null },
            title = {
                Text(
                    prod.name,
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary
                )
            },
            text = {
                Column {
                    Text(
                        "Preço: R$ ${String.format("%.2f", prod.price)} • Setor: ${prod.productionSector.name}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = observationText,
                        onValueChange = { observationText = it },
                        label = { Text("Observação do Item") },
                        placeholder = { Text("Ex: Sem cebola, Ao ponto, Caneca bem gelada") },
                        modifier = Modifier.fillMaxWidth(),
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
                        onAddToCart(prod, observationText.trim())
                        productForNotes = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
                ) {
                    Text("Adicionar ao Pedido", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productForNotes = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // BOTTOM SHEET: Cart Review & Dispatch
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CardWhite,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Conferir Pedido • ${currentTable?.name ?: "Mesa"}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WinePrimary
                            )
                        )
                        Text(
                            text = "Cliente: ${currentTable?.customerName?.ifBlank { "Atendimento" }}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(onClick = onClearCart) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpar", tint = StatusRed)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                // Items list
                LazyColumn(modifier = Modifier.height(240.dp)) {
                    items(cartItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                if (item.notes.isNotBlank()) {
                                    Text(
                                        "Obs: ${item.notes}",
                                        fontSize = 11.sp,
                                        color = WinePrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    "R$ ${String.format("%.2f", item.product.price)} un • Setor: ${item.product.productionSector}",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            }

                            // Quantity Adjuster
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(WineContainer)
                                        .clickable {
                                            onUpdateQuantity(item.product, item.notes, item.quantity - 1)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = WinePrimary, modifier = Modifier.size(16.dp))
                                }

                                Text(
                                    text = "${item.quantity}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(WinePrimary)
                                        .clickable {
                                            onUpdateQuantity(item.product, item.notes, item.quantity + 1)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                // Breakdown: Subtotal + 10%
                val serviceFee = cartTotal * 0.10
                val finalOrderTotal = cartTotal + serviceFee

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = TextSecondary, fontSize = 13.sp)
                    Text("R$ ${String.format("%.2f", cartTotal)}", color = TextPrimary, fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Taxa de Serviço (10% opcional)", color = TextSecondary, fontSize = 12.sp)
                    Text("R$ ${String.format("%.2f", serviceFee)}", color = TextSecondary, fontSize = 12.sp)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Previsto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WinePrimary)
                    Text("R$ ${String.format("%.2f", finalOrderTotal)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WinePrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm & Send Order Button
                Button(
                    onClick = {
                        val tbl = currentTable ?: tables.firstOrNull()
                        if (tbl != null) {
                            onDispatchOrder(tbl.number, tbl.customerName, tbl.customerPhone)
                            showCartSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Enviar Pedido para Produção",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sector Icon Badge
            val (sectorBg, sectorColor, sectorIcon) = when (product.productionSector) {
                ProductionSector.CHURRASQUEIRA -> Triple(WineContainer, WinePrimary, Icons.Default.OutdoorGrill)
                ProductionSector.COZINHA -> Triple(GoldContainer, GoldDark, Icons.Default.Restaurant)
                ProductionSector.BAR -> Triple(StatusBlueContainer, StatusBlue, Icons.Default.SportsBar)
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(sectorBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = sectorIcon,
                    contentDescription = null,
                    tint = sectorColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Info: Name, description, price
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "R$ ${String.format("%.2f", product.price)}",
                        fontWeight = FontWeight.Bold,
                        color = WinePrimary,
                        fontSize = 14.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(sectorBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.productionSector.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = sectorColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Add button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(WinePrimary)
                    .clickable(onClick = onAdd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
