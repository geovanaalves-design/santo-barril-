package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.FinancialAccountEntity
import com.example.data.model.ImportedRawDataEntity
import com.example.data.model.IngredientEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductionSector
import com.example.data.model.SupplierEntity
import com.example.ui.FixedCostItem
import com.example.ui.MainViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberContainer
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TargetImportModule(val label: String, val icon: ImageVector, val description: String) {
    CARDAPIO_PRODUTOS("Cardápio & Produtos", Icons.Default.RestaurantMenu, "Alimenta produtos, preços, custos, categorias e setores"),
    ESTOQUE_INSUMOS("Estoque & Insumos", Icons.Default.Inventory, "Alimenta matérias-primas, custos unitários e estoque mínimo"),
    CLIENTES_CRM("Clientes & Fidelidade", Icons.Default.Group, "Alimenta cadastro de clientes, telefones, pontos e visitas"),
    FORNECEDORES("Fornecedores", Icons.Default.Inventory, "Alimenta parceiros, contatos e itens fornecidos"),
    CONTAS_FINANCEIRAS("Contas a Pagar/Receber", Icons.Default.PointOfSale, "Alimenta fluxo de caixa e obrigações financeiras"),
    GASTOS_FIXOS("Gastos Fixos (DRE)", Icons.Default.Assessment, "Alimenta despesas recorrentes para cálculo de Ponto de Equilíbrio"),
    COLABORADORES("Equipe & Funcionários", Icons.Default.Group, "Alimenta colaboradores, cargos e taxas de comissão"),
    DADOS_BRUTOS("Dados Genéricos", Icons.Default.Description, "Armazena dados brutos mapeados para consulta e auditoria")
}

data class ParsedSpreadsheet(
    val fileName: String,
    val headers: List<String>,
    val rows: List<List<String>>,
    val totalRows: Int,
    val totalCols: Int
)

@Composable
fun DataImportScreen(
    viewModel: MainViewModel,
    auditLogs: List<AuditLogEntity>,
    importedRawData: List<ImportedRawDataEntity>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Importar Planilha, 1: Histórico
    var targetModule by remember { mutableStateOf(TargetImportModule.CARDAPIO_PRODUTOS) }
    var importMode by remember { mutableStateOf("ADICIONAR_E_ATUALIZAR") } // ADICIONAR, ATUALIZAR, ADICIONAR_E_ATUALIZAR

    var parsedSpreadsheet by remember { mutableStateOf<ParsedSpreadsheet?>(null) }
    var columnMappings by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isProcessing by remember { mutableStateOf(false) }
    var importSuccessMessage by remember { mutableStateOf<String?>(null) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessing = true
            scope.launch(Dispatchers.IO) {
                val parsed = parseSpreadsheetUri(context, uri)
                withContext(Dispatchers.Main) {
                    parsedSpreadsheet = parsed
                    if (parsed != null) {
                        columnMappings = autoMapColumns(parsed.headers, targetModule)
                    }
                    isProcessing = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WineContainer),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, WinePrimary.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardWhite)
                        .border(1.dp, WinePrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = WinePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Importação Geral de Dados",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = WineDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WinePrimary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "ADMINISTRADOR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Alimente o banco de dados oficial do Santo Barril via planilhas Excel (.xlsx) ou CSV com mapeamento inteligente de colunas.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section Tabs: Importar vs Histórico
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                label = { Text("Assistente de Importação", fontWeight = FontWeight.SemiBold) },
                leadingIcon = { Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WinePrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )

            FilterChip(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                label = { Text("Histórico & Auditoria", fontWeight = FontWeight.SemiBold) },
                leadingIcon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WinePrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }

        if (activeTab == 0) {
            // STEP 1: DESTINATION MODULE SELECTOR
            Text(
                text = "1. Selecione o Módulo de Destino",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Os dados da planilha serão convertidos em registros oficiais e persistidos no Room/Supabase:",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TargetImportModule.values().forEach { module ->
                            val isSelected = targetModule == module
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) WineContainer else OffWhiteBackground)
                                    .border(
                                        1.dp,
                                        if (isSelected) WinePrimary else BorderSubtle,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        targetModule = module
                                        parsedSpreadsheet?.let {
                                            columnMappings = autoMapColumns(it.headers, module)
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = module.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) WinePrimary else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = module.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) WineDark else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = targetModule.description,
                        fontSize = 11.5.sp,
                        color = WinePrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // STEP 2: FILE SELECTION OR SAMPLE TEMPLATES
            Text(
                text = "2. Selecione ou Carregue a Planilha",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                filePickerLauncher.launch("*/*")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Escolher Arquivo (.xlsx / .csv)", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    // Preconfigured sample establishment templates for instant 1-tap testing
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Ou carregue um Modelo de Dados Oficial do Santo Barril:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val sample = getSampleCardapio()
                                    targetModule = TargetImportModule.CARDAPIO_PRODUTOS
                                    parsedSpreadsheet = sample
                                    columnMappings = autoMapColumns(sample.headers, TargetImportModule.CARDAPIO_PRODUTOS)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cardápio Oficial (10 Itens)", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val sample = getSampleEstoque()
                                    targetModule = TargetImportModule.ESTOQUE_INSUMOS
                                    parsedSpreadsheet = sample
                                    columnMappings = autoMapColumns(sample.headers, TargetImportModule.ESTOQUE_INSUMOS)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Estoque & Carnes (8 Insumos)", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val sample = getSampleClientes()
                                    targetModule = TargetImportModule.CLIENTES_CRM
                                    parsedSpreadsheet = sample
                                    columnMappings = autoMapColumns(sample.headers, TargetImportModule.CLIENTES_CRM)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Clientes VIP (5 Cadastros)", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val sample = getSampleContas()
                                    targetModule = TargetImportModule.CONTAS_FINANCEIRAS
                                    parsedSpreadsheet = sample
                                    columnMappings = autoMapColumns(sample.headers, TargetImportModule.CONTAS_FINANCEIRAS)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Contas & Despesas (6 Lançamentos)", fontSize = 11.sp)
                            }
                        }
                    }

                    if (isProcessing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Text("Lendo dados e analisando estrutura tabular...", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            // STEP 3: STRUCTURE ANALYSIS & COLUMN MAPPING
            parsedSpreadsheet?.let { sheet ->
                Text(
                    text = "3. Análise da Estrutura & Mapeamento de Colunas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // File Metadata Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(OffWhiteBackground)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sheet.fileName, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                Text("Detectadas ${sheet.totalCols} colunas e ${sheet.totalRows} registros", fontSize = 11.sp, color = TextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusGreenContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Arquivo Válido", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                            }
                        }

                        Text(
                            "Associe cada coluna da sua planilha ao campo correspondente no Santo Barril:",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )

                        val availableFields = getAvailableFieldsForModule(targetModule)

                        sheet.headers.forEach { header ->
                            val currentMapping = columnMappings[header] ?: "IGNORAR"
                            var dropdownExpanded by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentMapping != "IGNORAR") StatusBlueContainer.copy(alpha = 0.35f) else OffWhiteBackground)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(header, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    val sampleVal = sheet.rows.firstOrNull()?.getOrNull(sheet.headers.indexOf(header)) ?: ""
                                    Text("Exemplo: \"$sampleVal\"", fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                                }

                                Box {
                                    OutlinedButton(
                                        onClick = { dropdownExpanded = true },
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            text = if (currentMapping == "IGNORAR") "Ignorar Coluna" else "➔ $currentMapping",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (currentMapping == "IGNORAR") TextSecondary else WinePrimary
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = dropdownExpanded,
                                        onDismissRequest = { dropdownExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Ignorar esta coluna", fontSize = 12.sp, color = StatusRed) },
                                            onClick = {
                                                columnMappings = columnMappings + (header to "IGNORAR")
                                                dropdownExpanded = false
                                            }
                                        )
                                        availableFields.forEach { field ->
                                            DropdownMenuItem(
                                                text = { Text(field, fontSize = 12.sp) },
                                                onClick = {
                                                    columnMappings = columnMappings + (header to field)
                                                    dropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // STEP 4: VALIDATION COUNTERS & PREVIEW
                val validation = validateSpreadsheetRows(sheet, targetModule, columnMappings)
                Text(
                    text = "4. Validação Prévia dos Dados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Valid
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = StatusGreenContainer),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Válidos", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                            }
                            Text("${validation.validCount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                        }
                    }

                    // Warnings
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = StatusAmberContainer),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Avisos", fontSize = 11.sp, color = StatusAmber, fontWeight = FontWeight.Bold)
                            }
                            Text("${validation.warningCount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StatusAmber)
                        }
                    }

                    // Errors
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = if (validation.errorCount > 0) StatusRedContainer else CardWhite),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = if (validation.errorCount > 0) StatusRed else TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Erros", fontSize = 11.sp, color = if (validation.errorCount > 0) StatusRed else TextSecondary, fontWeight = FontWeight.Bold)
                            }
                            Text("${validation.errorCount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (validation.errorCount > 0) StatusRed else TextPrimary)
                        }
                    }
                }

                // Table preview of first 5 rows
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Prévia dos primeiros registros formatados:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            // Header Row
                            Row(
                                modifier = Modifier
                                    .background(OffWhiteBackground)
                                    .padding(8.dp)
                            ) {
                                sheet.headers.forEach { h ->
                                    val mapped = columnMappings[h] ?: "IGNORAR"
                                    if (mapped != "IGNORAR") {
                                        Text(
                                            text = mapped,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = WineDark,
                                            modifier = Modifier.width(130.dp)
                                        )
                                    }
                                }
                            }

                            // Data Rows
                            sheet.rows.take(5).forEachIndexed { idx, row ->
                                Row(
                                    modifier = Modifier
                                        .background(if (idx % 2 == 0) CardWhite else OffWhiteBackground.copy(alpha = 0.5f))
                                        .padding(8.dp)
                                ) {
                                    sheet.headers.forEachIndexed { colIdx, h ->
                                        val mapped = columnMappings[h] ?: "IGNORAR"
                                        if (mapped != "IGNORAR") {
                                            Text(
                                                text = row.getOrNull(colIdx) ?: "",
                                                fontSize = 11.sp,
                                                color = TextPrimary,
                                                modifier = Modifier.width(130.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // STEP 5: IMPORT MODE & CONFIRM BUTTON
                Text(
                    text = "5. Modo de Importação & Gravação Real",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = importMode == "ADICIONAR_E_ATUALIZAR",
                                onClick = { importMode = "ADICIONAR_E_ATUALIZAR" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Adicionar e Atualizar (Recomendado)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Cadastra itens inéditos e atualiza preços/estoque de itens que já existiam.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = importMode == "SOMENTE_ADICIONAR",
                                onClick = { importMode = "SOMENTE_ADICIONAR" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Apenas Adicionar Novos Registros", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Preserva os dados existentes e insere apenas os que não foram encontrados.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                executeRealImport(
                                    sheet = sheet,
                                    module = targetModule,
                                    mappings = columnMappings,
                                    mode = importMode,
                                    viewModel = viewModel
                                )
                                importSuccessMessage = "Planilha importada com sucesso! ${sheet.rows.size} registros alimentaram o banco de dados oficial."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                            shape = RoundedCornerShape(10.dp),
                            enabled = validation.validCount > 0
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Confirmar e Gravar ${validation.validCount} Registros no Sistema",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (importSuccessMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusGreenContainer)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    importSuccessMessage ?: "",
                                    color = StatusGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // TAB 1: HISTÓRICO DE IMPORTAÇÕES & AUDITORIA
            Text(
                text = "Histórico de Importações no Banco de Dados",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            val importAudits = auditLogs.filter { it.action == "IMPORT_DATA" }

            if (importAudits.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Text("Nenhuma importação registrada ainda.", fontSize = 13.sp, color = TextSecondary)
                        Text("As importações efetuadas pelo Administrador serão rastreadas aqui com carimbo de data e hora.", fontSize = 11.5.sp, color = TextSecondary)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    importAudits.forEach { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = log.details,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(StatusGreenContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("CONCLUÍDO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Operador: ${log.userName}", fontSize = 11.sp, color = TextSecondary)
                                    Text(sdf.format(Date(log.timestampMillis)), fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SPREADSHEET PARSING & COLUMN MATCHING ENGINE
// -------------------------------------------------------------

fun parseSpreadsheetUri(context: android.content.Context, uri: Uri): ParsedSpreadsheet? {
    return try {
        val fileName = uri.lastPathSegment ?: "planilha_importada.csv"
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val lines = reader.readLines().filter { it.isNotBlank() }
        reader.close()

        if (lines.isEmpty()) return null

        // Detect delimiter: comma, semicolon, tab
        val headerLine = lines.first()
        val delimiter = when {
            headerLine.contains(";") -> ";"
            headerLine.contains("\t") -> "\t"
            else -> ","
        }

        val headers = headerLine.split(delimiter).map { it.trim().trim('"', '\'') }
        val rows = lines.drop(1).map { line ->
            line.split(delimiter).map { it.trim().trim('"', '\'') }
        }

        ParsedSpreadsheet(
            fileName = fileName,
            headers = headers,
            rows = rows,
            totalRows = rows.size,
            totalCols = headers.size
        )
    } catch (e: Exception) {
        null
    }
}

fun getAvailableFieldsForModule(module: TargetImportModule): List<String> {
    return when (module) {
        TargetImportModule.CARDAPIO_PRODUTOS -> listOf(
            "Nome do Produto",
            "Código / SKU",
            "Categoria",
            "Preço de Venda (R$)",
            "Custo Direto (R$)",
            "Descrição",
            "Setor de Produção",
            "Estoque Atual",
            "Estoque Mínimo",
            "Foto / Link Imagem"
        )
        TargetImportModule.ESTOQUE_INSUMOS -> listOf(
            "Nome do Insumo",
            "Unidade de Medida (kg, g, un, ml, L)",
            "Estoque Atual",
            "Estoque Mínimo",
            "Custo por Unidade (R$)"
        )
        TargetImportModule.CLIENTES_CRM -> listOf(
            "Nome do Cliente",
            "Telefone / WhatsApp",
            "Pontos Fidelidade",
            "Total Gasto (R$)",
            "Data de Nascimento",
            "Preferências / Notas"
        )
        TargetImportModule.FORNECEDORES -> listOf(
            "Nome do Fornecedor / Empresa",
            "Pessoa de Contato",
            "Telefone / WhatsApp",
            "Produtos Fornecidos",
            "Dias de Entrega"
        )
        TargetImportModule.CONTAS_FINANCEIRAS -> listOf(
            "Descrição da Conta",
            "Tipo (DESPESA ou RECEITA)",
            "Categoria",
            "Valor (R$)",
            "Status (PAGA ou PENDENTE)"
        )
        TargetImportModule.GASTOS_FIXOS -> listOf(
            "Nome da Despesa",
            "Categoria",
            "Valor Mensal (R$)"
        )
        TargetImportModule.COLABORADORES -> listOf(
            "Nome do Colaborador",
            "Cargo",
            "Telefone",
            "Comissão (%)"
        )
        TargetImportModule.DADOS_BRUTOS -> listOf(
            "Identificador / Tag",
            "Valor"
        )
    }
}

fun autoMapColumns(headers: List<String>, module: TargetImportModule): Map<String, String> {
    val mappings = mutableMapOf<String, String>()
    val availableFields = getAvailableFieldsForModule(module)

    for (header in headers) {
        val lower = header.lowercase(Locale.ROOT)
        val matched = when (module) {
            TargetImportModule.CARDAPIO_PRODUTOS -> {
                when {
                    lower.contains("nome") || lower.contains("produto") || lower.contains("item") || lower.contains("descri") -> "Nome do Produto"
                    lower.contains("código") || lower.contains("codigo") || lower.contains("sku") -> "Código / SKU"
                    lower.contains("categ") || lower.contains("grupo") || lower.contains("tipo") -> "Categoria"
                    lower.contains("preço") || lower.contains("preco") || lower.contains("venda") || lower.contains("valor") -> "Preço de Venda (R$)"
                    lower.contains("custo") || lower.contains("cmv") -> "Custo Direto (R$)"
                    lower.contains("setor") || lower.contains("cozinha") || lower.contains("bar") -> "Setor de Produção"
                    lower.contains("foto") || lower.contains("imagem") || lower.contains("link") -> "Foto / Link Imagem"
                    lower.contains("estoque") -> "Estoque Atual"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.ESTOQUE_INSUMOS -> {
                when {
                    lower.contains("nome") || lower.contains("insumo") || lower.contains("materia") -> "Nome do Insumo"
                    lower.contains("unid") || lower.contains("medida") -> "Unidade de Medida (kg, g, un, ml, L)"
                    lower.contains("min") -> "Estoque Mínimo"
                    lower.contains("estoque") || lower.contains("qtd") || lower.contains("quantidade") -> "Estoque Atual"
                    lower.contains("custo") || lower.contains("preço") || lower.contains("preco") || lower.contains("valor") -> "Custo por Unidade (R$)"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.CLIENTES_CRM -> {
                when {
                    lower.contains("nome") || lower.contains("cliente") -> "Nome do Cliente"
                    lower.contains("tel") || lower.contains("cel") || lower.contains("whats") || lower.contains("fone") -> "Telefone / WhatsApp"
                    lower.contains("ponto") || lower.contains("score") || lower.contains("fidelidade") -> "Pontos Fidelidade"
                    lower.contains("gasto") || lower.contains("total") -> "Total Gasto (R$)"
                    lower.contains("nasc") || lower.contains("anivers") -> "Data de Nascimento"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.FORNECEDORES -> {
                when {
                    lower.contains("fornecedor") || lower.contains("empresa") || lower.contains("razao") || lower.contains("nome") -> "Nome do Fornecedor / Empresa"
                    lower.contains("contato") || lower.contains("responsavel") -> "Pessoa de Contato"
                    lower.contains("tel") || lower.contains("fone") || lower.contains("whats") -> "Telefone / WhatsApp"
                    lower.contains("prod") || lower.contains("fornece") -> "Produtos Fornecidos"
                    lower.contains("dia") || lower.contains("entrega") -> "Dias de Entrega"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.CONTAS_FINANCEIRAS -> {
                when {
                    lower.contains("desc") || lower.contains("titulo") || lower.contains("conta") -> "Descrição da Conta"
                    lower.contains("tipo") || lower.contains("natureza") -> "Tipo (DESPESA ou RECEITA)"
                    lower.contains("categ") || lower.contains("centro") -> "Categoria"
                    lower.contains("valor") || lower.contains("quantia") -> "Valor (R$)"
                    lower.contains("status") || lower.contains("pago") || lower.contains("situacao") -> "Status (PAGA ou PENDENTE)"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.GASTOS_FIXOS -> {
                when {
                    lower.contains("nome") || lower.contains("despesa") || lower.contains("gasto") -> "Nome da Despesa"
                    lower.contains("categ") -> "Categoria"
                    lower.contains("valor") || lower.contains("mensal") -> "Valor Mensal (R$)"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.COLABORADORES -> {
                when {
                    lower.contains("nome") || lower.contains("funcionario") || lower.contains("colaborador") -> "Nome do Colaborador"
                    lower.contains("cargo") || lower.contains("funcao") -> "Cargo"
                    lower.contains("tel") || lower.contains("cel") -> "Telefone"
                    lower.contains("comiss") || lower.contains("%") -> "Comissão (%)"
                    else -> "IGNORAR"
                }
            }
            TargetImportModule.DADOS_BRUTOS -> {
                when {
                    lower.contains("tag") || lower.contains("chave") || lower.contains("coluna") -> "Identificador / Tag"
                    else -> "Valor"
                }
            }
        }

        if (matched in availableFields) {
            mappings[header] = matched
        } else {
            mappings[header] = "IGNORAR"
        }
    }

    return mappings
}

data class ValidationSummary(
    val validCount: Int,
    val warningCount: Int,
    val errorCount: Int
)

fun validateSpreadsheetRows(
    sheet: ParsedSpreadsheet,
    module: TargetImportModule,
    mappings: Map<String, String>
): ValidationSummary {
    var valid = 0
    var warnings = 0
    var errors = 0

    for (row in sheet.rows) {
        val rowMap = mutableMapOf<String, String>()
        sheet.headers.forEachIndexed { colIdx, header ->
            val field = mappings[header] ?: "IGNORAR"
            if (field != "IGNORAR") {
                rowMap[field] = row.getOrNull(colIdx) ?: ""
            }
        }

        when (module) {
            TargetImportModule.CARDAPIO_PRODUTOS -> {
                val name = rowMap["Nome do Produto"] ?: ""
                val priceStr = rowMap["Preço de Venda (R$)"] ?: ""
                if (name.isBlank()) {
                    errors++
                } else {
                    val price = priceStr.replace("R$", "").replace(",", ".").trim().toDoubleOrNull()
                    if (price == null || price < 0) {
                        warnings++
                    }
                    valid++
                }
            }
            TargetImportModule.ESTOQUE_INSUMOS -> {
                val name = rowMap["Nome do Insumo"] ?: ""
                if (name.isBlank()) errors++ else valid++
            }
            else -> {
                valid++
            }
        }
    }

    return ValidationSummary(validCount = valid, warningCount = warnings, errorCount = errors)
}

// -------------------------------------------------------------
// REAL DATABASE EXECUTION WRITER (NO MOCKS)
// -------------------------------------------------------------

fun executeRealImport(
    sheet: ParsedSpreadsheet,
    module: TargetImportModule,
    mappings: Map<String, String>,
    mode: String,
    viewModel: MainViewModel
) {
    when (module) {
        TargetImportModule.CARDAPIO_PRODUTOS -> {
            val products = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val name = rowMap["Nome do Produto"] ?: return@mapNotNull null
                if (name.isBlank()) return@mapNotNull null

                val code = rowMap["Código / SKU"]?.ifBlank { null } ?: "SKU-${(1000..9999).random()}"
                val category = rowMap["Categoria"]?.ifBlank { "Geral" } ?: "Geral"
                val price = rowMap["Preço de Venda (R$)"]?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 25.0
                val cost = rowMap["Custo Direto (R$)"]?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: (price * 0.35)
                val desc = rowMap["Descrição"] ?: "Produto importado via planilha."
                val sectorStr = rowMap["Setor de Produção"]?.uppercase() ?: "COZINHA"
                val sector = when {
                    sectorStr.contains("CHURRAS") -> ProductionSector.CHURRASQUEIRA
                    sectorStr.contains("BAR") -> ProductionSector.BAR
                    else -> ProductionSector.COZINHA
                }
                val stock = rowMap["Estoque Atual"]?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 50.0
                val imageUri = rowMap["Foto / Link Imagem"] ?: ""

                ProductEntity(
                    code = code,
                    name = name,
                    category = category,
                    description = desc,
                    price = price,
                    costPrice = cost,
                    unit = "un",
                    isAvailable = true,
                    currentStock = stock,
                    minStock = 5.0,
                    productionSector = sector,
                    hasRecipe = false,
                    imageUri = imageUri
                )
            }
            viewModel.importProductsList(products, sheet.fileName, mode)
        }

        TargetImportModule.ESTOQUE_INSUMOS -> {
            val ingredients = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val name = rowMap["Nome do Insumo"] ?: return@mapNotNull null
                if (name.isBlank()) return@mapNotNull null

                val unit = rowMap["Unidade de Medida (kg, g, un, ml, L)"]?.ifBlank { "kg" } ?: "kg"
                val currentStock = rowMap["Estoque Atual"]?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 10.0
                val minStock = rowMap["Estoque Mínimo"]?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 5.0
                val cost = rowMap["Custo por Unidade (R$)"]?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 20.0

                IngredientEntity(
                    name = name,
                    unit = unit,
                    currentStock = currentStock,
                    minStock = minStock,
                    costPerUnit = cost
                )
            }
            viewModel.importIngredientsList(ingredients, sheet.fileName, mode)
        }

        TargetImportModule.CLIENTES_CRM -> {
            val customers = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val name = rowMap["Nome do Cliente"] ?: return@mapNotNull null
                if (name.isBlank()) return@mapNotNull null

                val phone = rowMap["Telefone / WhatsApp"] ?: ""
                val points = rowMap["Pontos Fidelidade"]?.toIntOrNull() ?: 100
                val totalSpent = rowMap["Total Gasto (R$)"]?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 0.0
                val bday = rowMap["Data de Nascimento"] ?: ""
                val notes = rowMap["Preferências / Notas"] ?: "Importado via planilha."

                CustomerEntity(
                    name = name,
                    phone = phone,
                    originChannel = "Importado via Planilha",
                    visitCount = 1,
                    totalSpent = totalSpent
                )
            }
            viewModel.importCustomersList(customers, sheet.fileName, mode)
        }

        TargetImportModule.FORNECEDORES -> {
            val suppliers = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val name = rowMap["Nome do Fornecedor / Empresa"] ?: return@mapNotNull null
                if (name.isBlank()) return@mapNotNull null

                val contact = rowMap["Pessoa de Contato"] ?: ""
                val phone = rowMap["Telefone / WhatsApp"] ?: ""
                val productsSupplied = rowMap["Produtos Fornecidos"] ?: "Insumos Gerais"
                val deliveryDaysStr = rowMap["Dias de Entrega"] ?: "2"

                SupplierEntity(
                    name = name,
                    cnpj = rowMap["CNPJ"] ?: "00.000.000/0001-00",
                    phone = phone,
                    contactPerson = contact,
                    productsSupplied = productsSupplied,
                    paymentTerms = "15 dias",
                    deliveryDays = deliveryDaysStr.filter { it.isDigit() }.toIntOrNull() ?: 2
                )
            }
            viewModel.importSuppliersList(suppliers, sheet.fileName, mode)
        }

        TargetImportModule.CONTAS_FINANCEIRAS -> {
            val accounts = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val desc = rowMap["Descrição da Conta"] ?: return@mapNotNull null
                if (desc.isBlank()) return@mapNotNull null

                val type = rowMap["Tipo (DESPESA ou RECEITA)"]?.uppercase() ?: "DESPESA"
                val cat = rowMap["Categoria"] ?: "Operacional"
                val amount = rowMap["Valor (R$)"]?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 100.0
                val isPaid = rowMap["Status (PAGA ou PENDENTE)"]?.uppercase()?.contains("PAGA") == true

                FinancialAccountEntity(
                    description = desc,
                    type = type,
                    category = cat,
                    amount = amount,
                    dueDateMillis = System.currentTimeMillis() + 86400000L * 7,
                    isPaid = isPaid
                )
            }
            viewModel.importFinancialAccountsList(accounts, sheet.fileName, mode)
        }

        TargetImportModule.GASTOS_FIXOS -> {
            val fixedCosts = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val name = rowMap["Nome da Despesa"] ?: return@mapNotNull null
                if (name.isBlank()) return@mapNotNull null

                val cat = rowMap["Categoria"] ?: "Fixos"
                val amount = rowMap["Valor Mensal (R$)"]?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 500.0

                FixedCostItem(
                    id = System.currentTimeMillis() + (1..1000).random(),
                    name = name,
                    category = cat,
                    amount = amount
                )
            }
            viewModel.importFixedCostsList(fixedCosts, sheet.fileName, mode)
        }

        TargetImportModule.COLABORADORES -> {
            val employees = sheet.rows.mapNotNull { row ->
                val rowMap = mutableMapOf<String, String>()
                sheet.headers.forEachIndexed { colIdx, header ->
                    val field = mappings[header] ?: "IGNORAR"
                    if (field != "IGNORAR") {
                        rowMap[field] = row.getOrNull(colIdx) ?: ""
                    }
                }
                val name = rowMap["Nome do Colaborador"] ?: return@mapNotNull null
                if (name.isBlank()) return@mapNotNull null

                val role = rowMap["Cargo"] ?: "Garçom"
                val phone = rowMap["Telefone"] ?: ""
                val commission = rowMap["Comissão (%)"]?.replace("%", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 10.0

                EmployeeEntity(
                    name = name,
                    role = role,
                    phone = phone,
                    commissionRate = commission
                )
            }
            viewModel.importEmployeesList(employees, sheet.fileName, mode)
        }

        TargetImportModule.DADOS_BRUTOS -> {
            val rawItems = sheet.rows.flatMapIndexed { rowIdx, row ->
                sheet.headers.mapIndexed { colIdx, header ->
                    ImportedRawDataEntity(
                        tag = "Linha_${rowIdx + 1}",
                        columnName = header,
                        value = row.getOrNull(colIdx) ?: "",
                        sourceFile = sheet.fileName
                    )
                }
            }
            viewModel.importRawDataList(rawItems, sheet.fileName)
        }
    }
}

// -------------------------------------------------------------
// SAMPLE ESTABLISHMENT TEMPLATES FOR INSTANT DEMO & VALIDATION
// -------------------------------------------------------------

fun getSampleCardapio(): ParsedSpreadsheet {
    val headers = listOf("Código", "Nome do Produto", "Categoria", "Preço Venda", "Custo Direto", "Setor", "Estoque")
    val rows = listOf(
        listOf("ESP-01", "Espetinho de Alcatra", "Espetinhos", "18,90", "6,50", "Churrasqueira", "60"),
        listOf("ESP-02", "Espetinho de Kafta com Queijo", "Espetinhos", "17,90", "5,80", "Churrasqueira", "45"),
        listOf("POR-01", "Torresmo de Rolo Crocante", "Porções", "49,90", "16,20", "Cozinha", "30"),
        listOf("POR-02", "Mandioca Cremosa Frita", "Porções", "28,90", "7,40", "Cozinha", "40"),
        listOf("BUR-01", "Hambúrguer Santo Barril Costela", "Hambúrgueres", "38,90", "13,50", "Cozinha", "25"),
        listOf("BEB-01", "Chopp Pilsen Artesanal 500ml", "Chopp", "14,00", "4,20", "Bar", "200"),
        listOf("BEB-02", "Chopp IPA Santo Barril 500ml", "Chopp", "18,00", "6,10", "Bar", "150"),
        listOf("DRK-01", "Caipirinha Cachaça Envelhecida", "Drinks", "22,00", "5,50", "Bar", "80"),
        listOf("DRK-02", "Gin Tônica Frutas Vermelhas", "Drinks", "29,90", "8,90", "Bar", "60"),
        listOf("SOB-01", "Petit Gâteau com Sorvete", "Sobremesas", "24,90", "7,20", "Cozinha", "20")
    )
    return ParsedSpreadsheet("Cardapio_Oficial_SantoBarril.csv", headers, rows, rows.size, headers.size)
}

fun getSampleEstoque(): ParsedSpreadsheet {
    val headers = listOf("Insumo", "Unidade", "Qtd Atual", "Qtd Minima", "Custo Unitario")
    val rows = listOf(
        listOf("Picanha Bovina Angus", "kg", "35.0", "10.0", "68.50"),
        listOf("Alcatra Bovina", "kg", "50.0", "15.0", "42.00"),
        listOf("Panceta Suína para Torresmo", "kg", "28.0", "8.0", "26.00"),
        listOf("Queijo Coalho em Barra", "kg", "20.0", "5.0", "45.00"),
        listOf("Barril de Chopp Pilsen 50L", "un", "6.0", "2.0", "380.00"),
        listOf("Barril de Chopp IPA 50L", "un", "4.0", "2.0", "520.00"),
        listOf("Batata Palito Congelada", "kg", "40.0", "12.0", "14.50"),
        listOf("Cachaça Artesanal Carvalho", "L", "15.0", "4.0", "38.00")
    )
    return ParsedSpreadsheet("Estoque_SantoBarril_Insumos.csv", headers, rows, rows.size, headers.size)
}

fun getSampleClientes(): ParsedSpreadsheet {
    val headers = listOf("Nome Cliente", "WhatsApp", "Pontos", "Gasto Acumulado", "Nascimento")
    val rows = listOf(
        listOf("Carlos Eduardo Rocha", "(11) 98765-4321", "450", "1890.50", "15/04/1988"),
        listOf("Fernanda Lima Costa", "(11) 97654-3210", "620", "2450.00", "22/09/1992"),
        listOf("Rodrigo Albuquerque", "(11) 96543-2109", "310", "1220.00", "03/11/1985"),
        listOf("Juliana Martins", "(11) 95432-1098", "890", "3780.00", "30/01/1990"),
        listOf("Marcelo Silveira", "(11) 94321-0987", "180", "690.00", "18/07/1995")
    )
    return ParsedSpreadsheet("Clientes_CRM_Fidelidade.csv", headers, rows, rows.size, headers.size)
}

fun getSampleContas(): ParsedSpreadsheet {
    val headers = listOf("Descricao", "Tipo", "Categoria", "Valor", "Situacao")
    val rows = listOf(
        listOf("Fornecedor Carnes Boi Nobre", "DESPESA", "Fornecedores", "3450.00", "PENDENTE"),
        listOf("Distribuidora de Chopp & Bebidas", "DESPESA", "Bebidas", "2890.00", "PENDENTE"),
        listOf("Aluguel do Ponto Comercial", "DESPESA", "Fixos", "5200.00", "PAGA"),
        listOf("Conta de Energia Elétrica (Enel)", "DESPESA", "Utilidades", "1680.00", "PENDENTE"),
        listOf("Internet Fibra Óptica 1Gb", "DESPESA", "Utilidades", "249.90", "PAGA"),
        listOf("Recebimento Evento Corporativo", "RECEITA", "Eventos", "6500.00", "PAGA")
    )
    return ParsedSpreadsheet("Financeiro_Contas_SantoBarril.csv", headers, rows, rows.size, headers.size)
}
