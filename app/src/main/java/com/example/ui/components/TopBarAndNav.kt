package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.MainDestination
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WinePrimary

@Composable
fun SantoBarrilTopBar(
    currentUser: UserEntity,
    unreadNotificationsCount: Int,
    syncStatus: String = "Sincronizado",
    onOpenNotifications: () -> Unit,
    onOpenAiInsights: () -> Unit,
    onLogout: () -> Unit
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    // Bento Header Container with generous whitespace & clean borders
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .border(width = 1.dp, color = BorderSubtle)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Typography
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { roleMenuExpanded = true }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WineContainer)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_santo_barril_logo),
                        contentDescription = "Santo Barril",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SANTO BARRIL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.3).sp,
                            color = WinePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Discreet connection status indicator (Section C.1)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (syncStatus) {
                                        "Sincronizado" -> StatusGreen.copy(alpha = 0.12f)
                                        "Sincronizando..." -> StatusAmber.copy(alpha = 0.15f)
                                        else -> StatusRed.copy(alpha = 0.12f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (syncStatus) {
                                            "Sincronizado" -> StatusGreen
                                            "Sincronizando..." -> StatusAmber
                                            else -> StatusRed
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = syncStatus,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (syncStatus) {
                                    "Sincronizado" -> StatusGreen
                                    "Sincronizando..." -> StatusAmber
                                    else -> StatusRed
                                }
                            )
                        }
                    }
                    Text(
                        text = when (currentUser.role) {
                            UserRole.ADMINISTRADOR -> "Administrador • Acesso Total"
                            UserRole.GERENTE -> "Gerente • Operação"
                            UserRole.GARCOM -> "Garçom • Atendimento"
                            UserRole.COZINHA_BAR -> "Cozinha & Bar"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            // Right: Role Actions, Insights & Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // AI Intelligence Pill (Only visible for Admin & Gerente)
                if (currentUser.role == UserRole.ADMINISTRADOR || currentUser.role == UserRole.GERENTE) {
                    IconButton(
                        onClick = onOpenAiInsights,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(WineContainer)
                                .border(1.dp, WinePrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Insights IA",
                                tint = WinePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Notifications Bell (Only visible for Admin & Gerente)
                if (currentUser.role == UserRole.ADMINISTRADOR || currentUser.role == UserRole.GERENTE) {
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.size(36.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(containerColor = WinePrimary) {
                                        Text("$unreadNotificationsCount", color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificações",
                                tint = WinePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Logout Button
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(OffWhiteBackground)
                            .border(1.dp, BorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sair do Sistema",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // User Info and Logout Menu Dropdown
                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Conectado como: ${currentUser.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Cargo: ${currentUser.role.name}", fontSize = 11.sp, color = TextSecondary)
                            }
                        },
                        onClick = {}
                    )
                    DropdownMenuItem(
                        text = { Text("Encerrar Sessão (Logout)", fontSize = 13.sp, color = Color(0xFFB3261E), fontWeight = FontWeight.Bold) },
                        onClick = {
                            roleMenuExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Role-Based Bottom Navigation Bar strictly complying with user specifications:
 * - Administrador: Layout | Usuários | Configurações
 * - Gerente: Dashboard | Financeiro | Cardápio | Estoque | Mesas
 * - Garçom: Mesas | Cardápio | Meus Pedidos
 */
@Composable
fun SantoBarrilBottomNavigation(
    currentDestination: MainDestination,
    userRole: UserRole,
    onDestinationSelected: (MainDestination) -> Unit,
    onOpenAdminMoreSheet: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .border(width = 1.dp, color = BorderSubtle)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (userRole) {
                UserRole.ADMINISTRADOR -> {
                    // Administrador: Layout | Usuários | Importar | Configurações
                    BentoNavItem(
                        icon = Icons.Default.Palette,
                        label = "Layout",
                        isSelected = currentDestination == MainDestination.LAYOUT,
                        onClick = { onDestinationSelected(MainDestination.LAYOUT) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.Group,
                        label = "Usuários",
                        isSelected = currentDestination == MainDestination.USUARIOS,
                        onClick = { onDestinationSelected(MainDestination.USUARIOS) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.CloudUpload,
                        label = "Importar",
                        isSelected = currentDestination == MainDestination.IMPORTAR_DADOS,
                        onClick = { onDestinationSelected(MainDestination.IMPORTAR_DADOS) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.Settings,
                        label = "Configurações",
                        isSelected = currentDestination == MainDestination.CONFIG_AUDIT,
                        onClick = { onDestinationSelected(MainDestination.CONFIG_AUDIT) }
                    )
                }

                UserRole.GERENTE -> {
                    // Gerente: Dashboard | Financeiro | Cardápio | Estoque | Mesas | Mais
                    BentoNavItem(
                        icon = Icons.Default.Assessment,
                        label = "Dashboard",
                        isSelected = currentDestination == MainDestination.DASHBOARD,
                        onClick = { onDestinationSelected(MainDestination.DASHBOARD) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.PointOfSale,
                        label = "Financeiro",
                        isSelected = currentDestination == MainDestination.FINANCEIRO_DRE,
                        onClick = { onDestinationSelected(MainDestination.FINANCEIRO_DRE) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.RestaurantMenu,
                        label = "Cardápio",
                        isSelected = currentDestination == MainDestination.PEDIDO_CARDAPIO,
                        onClick = { onDestinationSelected(MainDestination.PEDIDO_CARDAPIO) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.Inventory,
                        label = "Estoque",
                        isSelected = currentDestination == MainDestination.ESTOQUE_FICHAS,
                        onClick = { onDestinationSelected(MainDestination.ESTOQUE_FICHAS) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.TableRestaurant,
                        label = "Mesas",
                        isSelected = currentDestination == MainDestination.MESAS,
                        onClick = { onDestinationSelected(MainDestination.MESAS) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.MoreHoriz,
                        label = "Mais",
                        isSelected = false,
                        onClick = onOpenAdminMoreSheet
                    )
                }

                UserRole.GARCOM -> {
                    // Garçom: Mesas | Cardápio | Meus Pedidos
                    BentoNavItem(
                        icon = Icons.Default.TableRestaurant,
                        label = "Mesas",
                        isSelected = currentDestination == MainDestination.MESAS,
                        onClick = { onDestinationSelected(MainDestination.MESAS) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.RestaurantMenu,
                        label = "Cardápio",
                        isSelected = currentDestination == MainDestination.PEDIDO_CARDAPIO,
                        onClick = { onDestinationSelected(MainDestination.PEDIDO_CARDAPIO) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.ReceiptLong,
                        label = "Meus Pedidos",
                        isSelected = currentDestination == MainDestination.MEUS_PEDIDOS,
                        onClick = { onDestinationSelected(MainDestination.MEUS_PEDIDOS) }
                    )
                }

                UserRole.COZINHA_BAR -> {
                    BentoNavItem(
                        icon = Icons.Default.DinnerDining,
                        label = "Produção",
                        isSelected = currentDestination == MainDestination.COZINHA_BAR,
                        onClick = { onDestinationSelected(MainDestination.COZINHA_BAR) }
                    )
                    BentoNavItem(
                        icon = Icons.Default.TableRestaurant,
                        label = "Mesas",
                        isSelected = currentDestination == MainDestination.MESAS,
                        onClick = { onDestinationSelected(MainDestination.MESAS) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) WinePrimary else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) WinePrimary else TextSecondary
        )
    }
}

@Composable
fun AdminSecondaryNavRow(
    currentDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit
) {
    val items = listOf(
        MainDestination.DASHBOARD to "Dashboard",
        MainDestination.FINANCEIRO_DRE to "Financeiro & DRE",
        MainDestination.USUARIOS to "Usuários & RBAC",
        MainDestination.ESTOQUE_FICHAS to "Estoque & Custos",
        MainDestination.MESAS to "Mesas",
        MainDestination.PEDIDO_CARDAPIO to "Cardápio / PDV",
        MainDestination.COZINHA_BAR to "Cozinha & Bar",
        MainDestination.CAIXA_PDV to "Caixa PDV",
        MainDestination.EQUIPE_CRM to "Garçons & CRM",
        MainDestination.CONFIG_AUDIT to "Configurações & Auditoria",
        MainDestination.MODO_CLIENTE to "Modo Cliente QR"
    )

    ScrollableTabRow(
        selectedTabIndex = items.indexOfFirst { it.first == currentDestination }.coerceAtLeast(0),
        containerColor = CardWhite,
        contentColor = WinePrimary,
        edgePadding = 16.dp,
        divider = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderSubtle)
            )
        }
    ) {
        items.forEach { (destination, label) ->
            val isSelected = currentDestination == destination
            Tab(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                text = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) WineContainer else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) WinePrimary else TextSecondary
                        )
                    }
                }
            )
        }
    }
}
