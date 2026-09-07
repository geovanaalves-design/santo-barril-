package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.MainDestination
import com.example.ui.MainViewModel
import com.example.ui.components.AdminSecondaryNavRow
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.SantoBarrilBottomNavigation
import com.example.ui.components.SantoBarrilTopBar
import com.example.ui.screens.CashierScreen
import com.example.ui.screens.ClientModeScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DataImportScreen
import com.example.ui.screens.FinancialDreScreen
import com.example.ui.screens.KitchenBarScreen
import com.example.ui.screens.LayoutScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MenuOrderScreen
import com.example.ui.screens.MyOrdersScreen
import com.example.ui.screens.SettingsAuditScreen
import com.example.ui.screens.StockRecipeScreen
import com.example.ui.screens.TablesScreen
import com.example.ui.screens.TeamCrmScreen
import com.example.ui.screens.UsersScreen
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WinePrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SantoBarrilApp()
            }
        }
    }
}

@Composable
fun SantoBarrilApp(viewModel: MainViewModel = viewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentDestination by viewModel.currentDestination.collectAsState()
    val users by viewModel.users.collectAsState()

    val tables by viewModel.tables.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val orderItems by viewModel.orderItems.collectAsState()
    val currentShift by viewModel.currentShift.collectAsState()
    val cashierMovements by viewModel.cashierMovements.collectAsState()
    val financialAccounts by viewModel.financialAccounts.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    val purchaseOrders by viewModel.purchaseOrders.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val printJobs by viewModel.printJobs.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val importedRawData by viewModel.importedRawData.collectAsState()

    val selectedTableNumber by viewModel.selectedTableNumber.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val aiInsight by viewModel.aiInsight.collectAsState()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()

    val userLockouts by viewModel.userLockouts.collectAsState()
    val userFailedAttempts by viewModel.userFailedAttempts.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAiDialog by remember { mutableStateOf(false) }
    var showAdminMoreModal by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Authentication Guard: Login Screen is mandatory initial state
    if (!isLoggedIn) {
        LoginScreen(
            users = users,
            userLockouts = userLockouts,
            userFailedAttempts = userFailedAttempts,
            onLogin = { user, pin -> viewModel.attemptLogin(user, pin) }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OffWhiteBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                val unreadNotifs = notifications.count { !it.isRead }
                SantoBarrilTopBar(
                    currentUser = currentUser,
                    unreadNotificationsCount = unreadNotifs,
                    syncStatus = syncStatus,
                    onOpenNotifications = { viewModel.navigateTo(MainDestination.CONFIG_AUDIT) },
                    onOpenAiInsights = {
                        viewModel.generateAiInsight()
                        showAiDialog = true
                    },
                    onLogout = { viewModel.logout() }
                )
            }
        },
        bottomBar = {
            SantoBarrilBottomNavigation(
                currentDestination = currentDestination,
                userRole = currentUser.role,
                onDestinationSelected = { dest -> viewModel.navigateTo(dest) },
                onOpenAdminMoreSheet = { showAdminMoreModal = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Check Access Authorization (RBAC security barrier)
            if (!viewModel.isAuthorized(currentUser.role, currentDestination)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BentoCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = BentoCardVariant.WineTinted,
                        cornerRadius = 24.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(WineContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Acesso Negado",
                                    tint = WinePrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "Acesso Restrito",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "O seu cargo (${currentUser.role.name}) não possui autorização para visualizar este módulo.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Button(
                                onClick = {
                                    when (currentUser.role) {
                                        UserRole.ADMINISTRADOR -> viewModel.navigateTo(MainDestination.LAYOUT)
                                        UserRole.GERENTE -> viewModel.navigateTo(MainDestination.DASHBOARD)
                                        UserRole.GARCOM -> viewModel.navigateTo(MainDestination.MESAS)
                                        UserRole.COZINHA_BAR -> viewModel.navigateTo(MainDestination.COZINHA_BAR)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Voltar ao Início do Meu Cargo", color = Color.White)
                            }
                        }
                    }
                }
            } else {
                when (currentDestination) {
                    MainDestination.MESAS -> {
                        TablesScreen(
                            tables = tables,
                            orders = orders,
                            customers = customers,
                            onOpenTableOrder = { tableNum ->
                                viewModel.setSelectedTable(tableNum)
                                viewModel.navigateTo(MainDestination.PEDIDO_CARDAPIO)
                            },
                            onOpenBillPayment = { tableNum ->
                                viewModel.setSelectedTable(tableNum)
                                viewModel.navigateTo(MainDestination.CAIXA_PDV)
                            },
                            viewModel = viewModel
                        )
                    }
                    MainDestination.PEDIDO_CARDAPIO -> {
                        MenuOrderScreen(
                            products = products,
                            tables = tables,
                            selectedTableNumber = selectedTableNumber,
                            cartItems = cartItems,
                            onAddToCart = { product, notes -> viewModel.addToCart(product, notes) },
                            onUpdateQuantity = { product, notes, qty -> viewModel.updateCartItemQuantity(product, notes, qty) },
                            onClearCart = { viewModel.clearCart() },
                            onDispatchOrder = { tableNum, custName, custPhone ->
                                viewModel.dispatchOrder(tableNum, custName, custPhone)
                            },
                            onSelectTable = { tableNum -> viewModel.setSelectedTable(tableNum) },
                            onBack = { viewModel.navigateTo(MainDestination.MESAS) }
                        )
                    }
                    MainDestination.MEUS_PEDIDOS -> {
                        MyOrdersScreen(
                            currentUser = currentUser,
                            orders = orders,
                            orderItems = orderItems,
                            onUpdateOrderStatus = { orderId, status -> viewModel.updateOrderStatus(orderId, status) },
                            onNavigateToTable = { tableNum ->
                                viewModel.setSelectedTable(tableNum)
                                viewModel.navigateTo(MainDestination.MESAS)
                            }
                        )
                    }
                    MainDestination.COZINHA_BAR -> {
                        KitchenBarScreen(
                            orders = orders,
                            orderItems = orderItems,
                            printJobs = printJobs,
                            onUpdateItemStatus = { itemId, status -> viewModel.updateItemStatus(itemId, status) }
                        )
                    }
                    MainDestination.CAIXA_PDV -> {
                        CashierScreen(
                            currentShift = currentShift,
                            movements = cashierMovements,
                            tables = tables,
                            orders = orders,
                            orderItems = orderItems,
                            selectedTableNumber = selectedTableNumber,
                            onOpenCashier = { balance -> viewModel.openCashier(balance) },
                            onCloseCashier = { counted -> viewModel.closeCashier(counted) },
                            onAddMovement = { type, amount, reason -> viewModel.addCashierMovement(type, amount, reason) },
                            onFinalizePayment = { tableNum, subtotal, discount, feeIncluded, fee, total, method ->
                                viewModel.finalizeTablePayment(tableNum, subtotal, discount, feeIncluded, fee, total, method)
                            },
                            onSelectTable = { tableNum -> viewModel.setSelectedTable(tableNum) }
                        )
                    }
                    MainDestination.DASHBOARD -> {
                        DashboardScreen(
                            tables = tables,
                            orders = orders,
                            products = products,
                            ingredients = ingredients,
                            employees = employees,
                            financialAccounts = financialAccounts,
                            aiInsight = aiInsight,
                            isGeneratingAi = isGeneratingAi,
                            onGenerateAi = { viewModel.generateAiInsight() },
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }
                    MainDestination.USUARIOS -> {
                        UsersScreen(
                            users = users,
                            onCreateUser = { name, username, role, pin ->
                                viewModel.createUser(name, username, role, pin)
                            },
                            onUpdateUser = { updatedUser ->
                                viewModel.updateUser(updatedUser)
                            },
                            onDeleteUser = { userToDelete ->
                                viewModel.deleteUser(userToDelete)
                            }
                        )
                    }
                    MainDestination.ESTOQUE_FICHAS -> {
                        StockRecipeScreen(
                            ingredients = ingredients,
                            products = products,
                            suppliers = suppliers,
                            purchaseOrders = purchaseOrders,
                            onAddIngredient = { name, unit, currentStock, minStock, costPerUnit ->
                                viewModel.addIngredient(name, unit, currentStock, minStock, costPerUnit)
                            },
                            onUpdateIngredient = { ing -> viewModel.updateIngredient(ing) },
                            onAdjustStock = { id, newStock, reason -> viewModel.adjustStock(id, newStock, reason) },
                            onDeleteIngredient = { ing -> viewModel.deleteIngredient(ing) },
                            onAddSupplier = { name, contact, phone, productsSupplied, deliveryDays ->
                                viewModel.addSupplier(name, contact, phone, productsSupplied, deliveryDays)
                            },
                            onUpdateSupplier = { sup -> viewModel.updateSupplier(sup) },
                            onDeleteSupplier = { sup -> viewModel.deleteSupplier(sup) },
                            onAddPurchaseOrder = { supName, amount, notes -> viewModel.addPurchaseOrder(supName, amount, notes) },
                            onUpdateProduct = { prod -> viewModel.updateProduct(prod) }
                        )
                    }
                    MainDestination.FINANCEIRO_DRE -> {
                        FinancialDreScreen(
                            financialAccounts = financialAccounts,
                            orders = orders,
                            onAddFinancialAccount = { desc, type, cat, amount, isPaid ->
                                viewModel.addFinancialAccount(desc, type, cat, amount, isPaid)
                            },
                            onUpdateFinancialAccount = { acc -> viewModel.updateFinancialAccount(acc) },
                            onToggleFinancialAccountPaid = { acc -> viewModel.toggleFinancialAccountPaid(acc) },
                            onDeleteFinancialAccount = { acc -> viewModel.deleteFinancialAccount(acc) }
                        )
                    }
                    MainDestination.EQUIPE_CRM -> {
                        TeamCrmScreen(
                            employees = employees,
                            customers = customers,
                            onAddEmployee = { name, role, phone, commissionRate ->
                                viewModel.addEmployee(name, role, phone, commissionRate)
                            },
                            onUpdateEmployee = { emp -> viewModel.updateEmployee(emp) },
                            onDeleteEmployee = { emp -> viewModel.deleteEmployee(emp) }
                        )
                    }
                    MainDestination.MODO_CLIENTE -> {
                        ClientModeScreen(
                            products = products,
                            onCallWaiter = { tableNum -> viewModel.callWaiter(tableNum) }
                        )
                    }
                    MainDestination.CONFIG_AUDIT -> {
                        SettingsAuditScreen(
                            printJobs = printJobs,
                            auditLogs = auditLogs,
                            notifications = notifications,
                            onMarkNotificationRead = { id -> viewModel.markNotificationAsRead(id) }
                        )
                    }
                    MainDestination.LAYOUT -> {
                        LayoutScreen(
                            onSaveLayoutConfig = { brand, color, radius, density ->
                                viewModel.saveLayoutConfig(brand, color, radius, density)
                            }
                        )
                    }
                    MainDestination.IMPORTAR_DADOS -> {
                        DataImportScreen(
                            viewModel = viewModel,
                            auditLogs = auditLogs,
                            importedRawData = importedRawData
                        )
                    }
                }
            }
        }
    }

    // AI Insight Dialog
    if (showAiDialog) {
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            title = {
                Text(
                    text = "Inteligência Estratégica Santo Barril",
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = aiInsight ?: "Analisando histórico de vendas, margem de contribuição e giro de estoque...",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAiDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
                ) {
                    Text("Entendido", color = Color.White)
                }
            }
        )
    }

    // Role-Aware "Mais Módulos" Modal Menu
    if (showAdminMoreModal) {
        AlertDialog(
            onDismissRequest = { showAdminMoreModal = false },
            title = {
                Text(
                    text = if (currentUser.role == UserRole.GERENTE) "Mais Módulos da Operação" else "Mais Módulos do Sistema",
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentUser.role == UserRole.GERENTE) {
                        AdminMoreMenuItem(
                            icon = Icons.Default.Palette,
                            title = "Layout & Identidade Visual",
                            subtitle = "Personalize cores, bordas e tema da casa",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.LAYOUT)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.Settings,
                            title = "Configurações & Auditoria",
                            subtitle = "Impressoras, filas e registros de auditoria",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.CONFIG_AUDIT)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.Group,
                            title = "Equipe & CRM de Clientes",
                            subtitle = "Controle de garçons, comissões e clientes",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.EQUIPE_CRM)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.PointOfSale,
                            title = "Frente de Caixa (PDV)",
                            subtitle = "Abertura, fechamento e recebimento",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.CAIXA_PDV)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.DinnerDining,
                            title = "Cozinha & Bar (KDS)",
                            subtitle = "Painel de comandas de produção em tempo real",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.COZINHA_BAR)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.QrCodeScanner,
                            title = "Cardápio Digital (QR Code)",
                            subtitle = "Simulação do autoatendimento na mesa",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.MODO_CLIENTE)
                            }
                        )
                    } else {
                        AdminMoreMenuItem(
                            icon = Icons.Default.CloudUpload,
                            title = "Importação Geral de Planilhas",
                            subtitle = "Alimente o app via Excel (.xlsx) ou CSV",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.IMPORTAR_DADOS)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.TableRestaurant,
                            title = "Salão & Mesas",
                            subtitle = "Status de mesas, comandas e ocupação",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.MESAS)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.RestaurantMenu,
                            title = "Cardápio & Lançamento",
                            subtitle = "Visualização e lançamento de comandas",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.PEDIDO_CARDAPIO)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.DinnerDining,
                            title = "Cozinha & Bar (KDS)",
                            subtitle = "Painel de comandas de produção em tempo real",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.COZINHA_BAR)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.PointOfSale,
                            title = "Frente de Caixa (PDV)",
                            subtitle = "Abertura, fechamento e recebimento de contas",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.CAIXA_PDV)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.Inventory,
                            title = "Estoque & Fichas Técnicas",
                            subtitle = "Gestão de ingredientes, custos e fornecedores",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.ESTOQUE_FICHAS)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.Group,
                            title = "Equipe & CRM de Clientes",
                            subtitle = "Controle de garçons, gorjetas e fidelidade",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.EQUIPE_CRM)
                            }
                        )
                        AdminMoreMenuItem(
                            icon = Icons.Default.QrCodeScanner,
                            title = "Cardápio Digital (QR Code)",
                            subtitle = "Simulação do autoatendimento na mesa pelo cliente",
                            onClick = {
                                showAdminMoreModal = false
                                viewModel.navigateTo(MainDestination.MODO_CLIENTE)
                            }
                        )
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showAdminMoreModal = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

@Composable
private fun AdminMoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(WineContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = WinePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
