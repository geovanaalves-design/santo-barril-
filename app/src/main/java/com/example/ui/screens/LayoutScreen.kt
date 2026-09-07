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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardHeader
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.BentoMetricCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WinePrimary

data class ColorThemeOption(
    val name: String,
    val hexColor: Color,
    val description: String
)

@Composable
fun LayoutScreen(
    onSaveLayoutConfig: (brandName: String, primaryColorName: String, cornerRadiusDp: Int, density: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorOptions = listOf(
        ColorThemeOption("Vinho Santo Barril", WinePrimary, "Identidade clássica do estabelecimento"),
        ColorThemeOption("Âmbar Dourado", Color(0xFFC88214), "Tonalidade quente de espetaria e chopp"),
        ColorThemeOption("Verde Imperial", Color(0xFF2E6F40), "Moderno, natural e equilibrado"),
        ColorThemeOption("Azul Meia-Noite", Color(0xFF1B365D), "Elegante, corporativo e neutro")
    )

    var selectedColor by remember { mutableStateOf(colorOptions[0]) }
    var brandTitle by remember { mutableStateOf("SANTO BARRIL") }
    var brandSubtitle by remember { mutableStateOf("Bar & Espetaria Artesanal") }
    var selectedCornerRadius by remember { mutableStateOf(16) } // 12, 16, 20, 24
    var selectedDensity by remember { mutableStateOf("Confortável (Espaçoso)") } // Confortável vs Compacto

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(WineContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = WinePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Layout & Identidade Visual",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Exclusivo Administrador • Personalização de cores, tipografia e Bento Grid",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Live Preview Bento Card
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = selectedCornerRadius.dp
            ) {
                BentoCardHeader(
                    title = "Pré-visualização em Tempo Real",
                    subtitle = "Assim os cartões e títulos aparecem no app",
                    icon = Icons.Default.DashboardCustomize,
                    iconTint = selectedColor.hexColor,
                    iconBg = selectedColor.hexColor.copy(alpha = 0.12f)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(12.dp))

                // Simulated Bento Metric inside Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(selectedCornerRadius.dp))
                            .background(selectedColor.hexColor.copy(alpha = 0.08f))
                            .border(1.dp, selectedColor.hexColor.copy(alpha = 0.2f), RoundedCornerShape(selectedCornerRadius.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(brandTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = selectedColor.hexColor)
                            Text(brandSubtitle, fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(selectedCornerRadius.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(selectedCornerRadius.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Exemplo de Botão", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(selectedCornerRadius.dp / 2))
                                    .background(selectedColor.hexColor)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Ação Principal", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Palette Color Selection
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = selectedCornerRadius.dp
            ) {
                BentoCardHeader(
                    title = "Paleta de Cores Primária",
                    subtitle = "Define a tonalidade dos botões de ação, realces e ícones principais",
                    icon = Icons.Default.ColorLens
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    colorOptions.forEach { option ->
                        val isSelected = selectedColor.name == option.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) option.hexColor.copy(alpha = 0.08f) else CardWhite)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) option.hexColor else BorderSubtle,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedColor = option }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(option.hexColor)
                                        .border(1.dp, BorderSubtle, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = option.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = option.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = option.hexColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Corner Radius & Density
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = selectedCornerRadius.dp
            ) {
                BentoCardHeader(
                    title = "Estrutura dos Bento Cards",
                    subtitle = "Controle de curvatura dos cantos e espaçamento interno",
                    icon = Icons.Default.ViewAgenda
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Arredondamento dos Cantos:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(12 to "12dp (Sutil)", 16 to "16dp (Padrão)", 20 to "20dp (Moderno)", 24 to "24dp (Amplo)").forEach { (radius, label) ->
                        val isSelected = selectedCornerRadius == radius
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) selectedColor.hexColor.copy(alpha = 0.12f) else CardWhite)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) selectedColor.hexColor else BorderSubtle,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCornerRadius = radius }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) selectedColor.hexColor else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Espaçamento / Respiro das Telas:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Confortável (Espaçoso)", "Compacto (Maior densidade)").forEach { density ->
                        val isSelected = selectedDensity == density
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) selectedColor.hexColor.copy(alpha = 0.12f) else CardWhite)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) selectedColor.hexColor else BorderSubtle,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDensity = density }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = density,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) selectedColor.hexColor else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Branding and Title
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = selectedCornerRadius.dp
            ) {
                BentoCardHeader(
                    title = "Nome do Estabelecimento & Subtítulo",
                    subtitle = "Define a identidade impressa e exibida nos cabeçalhos",
                    icon = Icons.Default.FormatPaint
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = brandTitle,
                    onValueChange = { brandTitle = it },
                    label = { Text("Nome Principal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = selectedColor.hexColor,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = brandSubtitle,
                    onValueChange = { brandSubtitle = it },
                    label = { Text("Subtítulo / Descrição") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = selectedColor.hexColor,
                        unfocusedBorderColor = BorderSubtle
                    )
                )
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    onSaveLayoutConfig(brandTitle, selectedColor.name, selectedCornerRadius, selectedDensity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor.hexColor,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salvar e Aplicar Layout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
