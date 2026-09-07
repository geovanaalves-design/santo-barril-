package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProductEntity
import com.example.data.model.ProductionSector
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardVariant
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary

@Composable
fun ClientModeScreen(
    products: List<ProductEntity>,
    onCallWaiter: (Int) -> Unit
) {
    var selectedTableNumber by remember { mutableIntStateOf(1) }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var waiterCalledFeedback by remember { mutableStateOf(false) }

    val categories = listOf("Todos", "Espetinhos", "Porções", "Chopp", "Cervejas", "Drinks", "Bebidas", "Sobremesas")

    val filteredProducts = products.filter {
        selectedCategory == "Todos" || it.category == selectedCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        // Hero Bento Banner: Welcome to Santo Barril
        Box(modifier = Modifier.padding(14.dp)) {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.WineFilled,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardWhite)
                            .border(1.5.dp, GoldAccent, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_santo_barril_logo),
                            contentDescription = "Santo Barril Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "BEM-VINDO AO SANTO BARRIL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Cardápio Digital Interativo • Mesa $selectedTableNumber",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prominent Bento Call Waiter Button
                    Button(
                        onClick = {
                            onCallWaiter(selectedTableNumber)
                            waiterCalledFeedback = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (waiterCalledFeedback) "Garçom Notificado! Já vamos te atender." else "CHAMAR GARÇOM NA MESA",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Category Filter with Bento aesthetics
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WinePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = CardWhite,
                        labelColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) WinePrimary else BorderSubtle
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Products List in Bento Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredProducts) { prod ->
                BentoCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = BentoCardVariant.Default,
                    cornerRadius = 20.dp,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (sectorBg, sectorColor, sectorIcon) = when (prod.productionSector) {
                            ProductionSector.CHURRASQUEIRA -> Triple(WineContainer, WinePrimary, Icons.Default.OutdoorGrill)
                            ProductionSector.COZINHA -> Triple(GoldContainer, GoldDark, Icons.Default.Restaurant)
                            ProductionSector.BAR -> Triple(StatusBlueContainer, StatusBlue, Icons.Default.SportsBar)
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(sectorBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(sectorIcon, contentDescription = null, tint = sectorColor, modifier = Modifier.size(26.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text(prod.description, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("R$ ${String.format("%.2f", prod.price)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WinePrimary)
                        }
                    }
                }
            }
        }
    }
}
