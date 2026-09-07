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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardHeader
import com.example.ui.components.BentoCardVariant
import com.example.ui.components.BentoMetricCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WinePrimary

@Composable
fun UsersScreen(
    users: List<UserEntity>,
    onCreateUser: (String, String, UserRole, String) -> Unit,
    onUpdateUser: (UserEntity) -> Unit,
    onDeleteUser: (UserEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val adminCount = users.count { it.role == UserRole.ADMINISTRADOR }
    val managerCount = users.count { it.role == UserRole.GERENTE }
    val waiterCount = users.count { it.role == UserRole.GARCOM }
    val kitchenCount = users.count { it.role == UserRole.COZINHA_BAR }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header & Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CONTROLE DE ACESSO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = WinePrimary
                    )
                    Text(
                        text = "Gestão de Usuários",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Novo Usuário", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Temporary Password Notice
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(WineContainer.copy(alpha = 0.5f))
                    .border(1.dp, WinePrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = WinePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Senha provisória de acesso para todos: 000",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WinePrimary
                        )
                        Text(
                            text = "Todos os perfis (Administrador, Gerente, Garçons e Cozinha) estão com o PIN padrão 000 configurado.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Summary Metric Bento Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Total",
                    value = "${users.size}",
                    icon = Icons.Default.Person,
                    badge = "Ativos",
                    badgeColor = WinePrimary
                )
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Gerentes",
                    value = "$managerCount",
                    icon = Icons.Default.Shield,
                    badge = "Operação",
                    badgeColor = Color(0xFF2E6F40)
                )
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Garçons",
                    value = "$waiterCount",
                    icon = Icons.Default.Security,
                    badge = "Salão",
                    badgeColor = Color(0xFFC88214)
                )
                BentoMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Cozinha",
                    value = "$kitchenCount",
                    icon = Icons.Default.AdminPanelSettings,
                    badge = "KDS",
                    badgeColor = WinePrimary
                )
            }
        }

        // Roles & Permissions Overview Card
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.OffWhite,
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Perfis e Níveis de Permissão (RBAC)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WinePrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RolePermissionBadge(
                            title = "🔑 Administrador",
                            subtitle = "Acesso total aos módulos, custos, configurações e usuários.",
                            modifier = Modifier.weight(1f)
                        )
                        RolePermissionBadge(
                            title = "👔 Gerente",
                            subtitle = "Dashboard, mesas, caixa, estoque e relatórios operacionais.",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RolePermissionBadge(
                            title = "🍽️ Garçom",
                            subtitle = "Acesso restrito a mesas, cardápio e lançamento de pedidos.",
                            modifier = Modifier.weight(1f)
                        )
                        RolePermissionBadge(
                            title = "🍳 Equipe Cozinha",
                            subtitle = "Painel KDS de produção, preparo e despacho de pedidos em tempo real.",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section Title: Usuários Cadastrados
        item {
            Text(
                text = "Colaboradores do Sistema",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // List of Users
        items(users, key = { it.id }) { user ->
            UserListItemCard(
                user = user,
                onEdit = { userToEdit = user },
                onDelete = { userToDelete = user }
            )
        }
    }

    // Create User Dialog
    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, username, role, pin ->
                onCreateUser(name, username, role, pin)
                showCreateDialog = false
            }
        )
    }

    // Edit User Dialog
    userToEdit?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { userToEdit = null },
            onConfirm = { updatedUser ->
                onUpdateUser(updatedUser)
                userToEdit = null
            }
        )
    }

    // Delete User Confirmation
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = {
                Text(
                    text = "Excluir Usuário",
                    fontWeight = FontWeight.Bold,
                    color = WinePrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Deseja realmente remover o acesso de '${user.name}' (${user.role.name})? Esta ação não pode ser desfeita.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(user)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun UserListItemCard(
    user: UserEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val (roleTag, roleColor) = when (user.role) {
        UserRole.ADMINISTRADOR -> "Administrador" to WinePrimary
        UserRole.GERENTE -> "Gerente" to Color(0xFF2E6F40)
        UserRole.GARCOM -> "Garçom" to Color(0xFFC88214)
        UserRole.COZINHA_BAR -> "Cozinha & Bar" to Color(0xFF4355B9)
    }

    val isProtectedAdmin = user.id == 1L || user.username.lowercase() == "admin"

    BentoCard(
        modifier = Modifier.fillMaxWidth(),
        variant = BentoCardVariant.Default,
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(roleColor.copy(alpha = 0.12f))
                        .border(1.dp, roleColor.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (user.role) {
                            UserRole.ADMINISTRADOR -> "👑"
                            UserRole.GERENTE -> "👔"
                            UserRole.GARCOM -> "🍽️"
                            UserRole.COZINHA_BAR -> "🍳"
                        },
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(roleColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = roleTag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = roleColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "@${user.username}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "PIN",
                                tint = TextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PIN: ${user.pin}",
                                fontSize = 11.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = WinePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (!isProtectedAdmin) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Excluir",
                            tint = Color(0xFFB3261E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RolePermissionBadge(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardWhite)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextSecondary,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun CreateUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, username: String, role: UserRole, pin: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.GARCOM) }
    var pin by remember { mutableStateOf("000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cadastrar Novo Usuário",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    placeholder = { Text("Ex: Mariana Souza") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nome de Usuário (Login)") },
                    placeholder = { Text("Ex: mariana") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Cargo no Estabelecimento",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RoleSelectOption(
                        title = "👔 Gerente",
                        isSelected = selectedRole == UserRole.GERENTE,
                        onSelect = { selectedRole = UserRole.GERENTE },
                        modifier = Modifier.weight(1f)
                    )
                    RoleSelectOption(
                        title = "🍽️ Garçom",
                        isSelected = selectedRole == UserRole.GARCOM,
                        onSelect = { selectedRole = UserRole.GARCOM },
                        modifier = Modifier.weight(1f)
                    )
                    RoleSelectOption(
                        title = "🍳 Cozinha",
                        isSelected = selectedRole == UserRole.COZINHA_BAR,
                        onSelect = { selectedRole = UserRole.COZINHA_BAR },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 10) pin = it },
                    label = { Text("PIN de Segurança (padrão: 000)") },
                    placeholder = { Text("000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && username.isNotBlank()) {
                        onConfirm(name, username, selectedRole, pin)
                    }
                },
                enabled = name.isNotBlank() && username.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
            ) {
                Text("Cadastrar Usuário", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EditUserDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (UserEntity) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var selectedRole by remember { mutableStateOf(user.role) }
    var pin by remember { mutableStateOf(user.pin) }

    val isProtectedAdmin = user.id == 1L || user.username.lowercase() == "admin"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar Usuário: @${user.username}",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (!isProtectedAdmin) {
                    Text(
                        text = "Cargo no Estabelecimento",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoleSelectOption(
                            title = "👔 Gerente",
                            isSelected = selectedRole == UserRole.GERENTE,
                            onSelect = { selectedRole = UserRole.GERENTE },
                            modifier = Modifier.weight(1f)
                        )
                        RoleSelectOption(
                            title = "🍽️ Garçom",
                            isSelected = selectedRole == UserRole.GARCOM,
                            onSelect = { selectedRole = UserRole.GARCOM },
                            modifier = Modifier.weight(1f)
                        )
                        RoleSelectOption(
                            title = "🍳 Cozinha",
                            isSelected = selectedRole == UserRole.COZINHA_BAR,
                            onSelect = { selectedRole = UserRole.COZINHA_BAR },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 10) pin = it },
                    label = { Text("PIN de Segurança Individual (até 10 dígitos)") },
                    placeholder = { Text("Ex: 1234, 01020304") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(user.copy(name = name, role = selectedRole, pin = pin))
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = WinePrimary)
            ) {
                Text("Salvar Alterações", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun RoleSelectOption(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) WineContainer else CardWhite)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) WinePrimary else BorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) WinePrimary else TextSecondary
        )
    }
}
