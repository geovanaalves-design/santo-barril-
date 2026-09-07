package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoCardVariant
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WineDark
import com.example.ui.theme.WinePrimary
import kotlinx.coroutines.delay

enum class LoginRoleTab {
    GERENTE,
    GARCOM,
    COZINHA,
    ADMINISTRADOR
}

@Composable
fun LoginScreen(
    users: List<UserEntity>,
    userLockouts: Map<String, Long> = emptyMap(),
    userFailedAttempts: Map<String, Int> = emptyMap(),
    onLogin: (UserEntity, String) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    val effectiveUsers = if (users.isNotEmpty()) users else listOf(
        UserEntity(1, "admin", "Carlos Silva (Admin)", UserRole.ADMINISTRADOR, "000"),
        UserEntity(2, "gerente", "Marcos Oliveira (Gerente)", UserRole.GERENTE, "000"),
        UserEntity(3, "gabriel", "Gabriel Santos (Garçom)", UserRole.GARCOM, "000"),
        UserEntity(4, "ryan", "Ryan Alves (Garçom)", UserRole.GARCOM, "000"),
        UserEntity(5, "cozinha", "Equipe da Cozinha", UserRole.COZINHA_BAR, "000")
    )

    var selectedRoleTab by remember { mutableStateOf(LoginRoleTab.GERENTE) }

    val gerentes = effectiveUsers.filter { it.role == UserRole.GERENTE }
    val garcons = effectiveUsers.filter { it.role == UserRole.GARCOM }
    val cozinhaUsers = effectiveUsers.filter { it.role == UserRole.COZINHA_BAR }
    val admins = effectiveUsers.filter { it.role == UserRole.ADMINISTRADOR }

    var selectedGarcom by remember {
        mutableStateOf(garcons.firstOrNull() ?: effectiveUsers.first())
    }

    val activeUser: UserEntity = when (selectedRoleTab) {
        LoginRoleTab.GERENTE -> gerentes.firstOrNull() ?: effectiveUsers.first()
        LoginRoleTab.GARCOM -> selectedGarcom
        LoginRoleTab.COZINHA -> cozinhaUsers.firstOrNull() ?: UserEntity(5, "cozinha", "Equipe da Cozinha", UserRole.COZINHA_BAR, "000")
        LoginRoleTab.ADMINISTRADOR -> admins.firstOrNull() ?: effectiveUsers.first()
    }

    var pin by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Countdown timer for locked user (C.2)
    val unlockTime = userLockouts[activeUser.username.lowercase()] ?: 0L
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(unlockTime) {
        while (unlockTime > currentTimeMillis) {
            delay(1000L)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    val isLocked = unlockTime > currentTimeMillis
    val remainingSeconds = if (isLocked) ((unlockTime - currentTimeMillis) / 1000).coerceAtLeast(1) else 0

    fun submitLogin() {
        if (isLocked) {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            errorMessage = "Usuário bloqueado. Aguarde %02d:%02d ou contate o Administrador.".format(mins, secs)
            return
        }

        if (pin.isBlank()) {
            errorMessage = "Digite o PIN de segurança para continuar."
            return
        }

        val (success, message) = onLogin(activeUser, pin.trim())
        if (!success) {
            errorMessage = message
            pin = ""
        } else {
            errorMessage = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Logo with ample padding, elegant framing, and breathing room
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(CardWhite)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(26.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_santo_barril_logo),
                    contentDescription = "Santo Barril Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SANTO BARRIL",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp,
                    color = WinePrimary
                )
                Text(
                    text = "Sistema de Gestão & Operação",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Main Login Card
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BentoCardVariant.Default,
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Selecione seu perfil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = TextPrimary
                    )

                    // Role Options with clean initials and subtle selection indicators
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileSelectCard(
                            initial = "GE",
                            roleName = "Gerente",
                            roleDescription = "Gestão operacional, financeiro e estoque",
                            isSelected = selectedRoleTab == LoginRoleTab.GERENTE,
                            onClick = {
                                selectedRoleTab = LoginRoleTab.GERENTE
                                errorMessage = null
                                pin = ""
                            }
                        )

                        ProfileSelectCard(
                            initial = "GA",
                            roleName = "Garçom",
                            roleDescription = "Atendimento de mesas, pedidos e clientes",
                            isSelected = selectedRoleTab == LoginRoleTab.GARCOM,
                            onClick = {
                                selectedRoleTab = LoginRoleTab.GARCOM
                                errorMessage = null
                                pin = ""
                            }
                        )

                        ProfileSelectCard(
                            initial = "CZ",
                            roleName = "Equipe da Cozinha",
                            roleDescription = "KDS de produção, preparo e despacho",
                            isSelected = selectedRoleTab == LoginRoleTab.COZINHA,
                            onClick = {
                                selectedRoleTab = LoginRoleTab.COZINHA
                                errorMessage = null
                                pin = ""
                            }
                        )

                        ProfileSelectCard(
                            initial = "AD",
                            roleName = "Administrador",
                            roleDescription = "Configurações gerais, usuários e layout",
                            isSelected = selectedRoleTab == LoginRoleTab.ADMINISTRADOR,
                            onClick = {
                                selectedRoleTab = LoginRoleTab.ADMINISTRADOR
                                errorMessage = null
                                pin = ""
                            }
                        )
                    }

                    // Sub-selection: Specific Garçom Selection with clean initials
                    if (selectedRoleTab == LoginRoleTab.GARCOM) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(OffWhiteBackground)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Selecione o atendente:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )

                            garcons.forEach { garcom ->
                                val isSelected = selectedGarcom.id == garcom.id
                                val initials = garcom.name.split(" ")
                                    .filter { it.isNotBlank() }
                                    .take(2)
                                    .map { it.first().uppercaseChar() }
                                    .joinToString("")
                                    .ifBlank { garcom.name.take(1).uppercase() }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CardWhite else Color.Transparent)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) WinePrimary else BorderSubtle.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedGarcom = garcom
                                            errorMessage = null
                                            pin = ""
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) WinePrimary else CardWhite)
                                            .border(1.dp, if (isSelected) WinePrimary else BorderSubtle, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initials,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = garcom.name,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "@${garcom.username}",
                                            fontSize = 10.5.sp,
                                            color = TextTertiary
                                        )
                                    }

                                    // Subtle indicator dot
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 4.5.dp else 1.dp,
                                                color = if (isSelected) WinePrimary else BorderSubtle,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // PIN Input Section: Clear, elegant, and minimalist
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PIN DE ACESSO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = TextTertiary,
                                    fontSize = 10.5.sp
                                )
                            )

                            // Subtle active user indicator pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(WineContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = activeUser.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WinePrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Elegant PIN digit indicator cells
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until 3) {
                                val isFilled = pin.length > i
                                val isCurrent = pin.length == i && !isLocked
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isFilled) WineContainer.copy(alpha = 0.45f) else CardWhite)
                                        .border(
                                            width = if (isCurrent) 1.5.dp else 1.dp,
                                            color = if (isCurrent) WinePrimary else if (isFilled) WinePrimary.copy(alpha = 0.5f) else BorderSubtle,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isFilled) {
                                        if (showPin) {
                                            Text(
                                                text = pin[i].toString(),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WinePrimary
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(9.dp)
                                                    .clip(CircleShape)
                                                    .background(WinePrimary)
                                            )
                                        }
                                    } else if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(16.dp)
                                                .background(WinePrimary.copy(alpha = 0.7f))
                                        )
                                    }
                                }
                            }
                        }

                        // Clear, elegant input field with direct numeric entry
                        OutlinedTextField(
                            value = pin,
                            onValueChange = {
                                if (it.length <= 6 && !isLocked && it.all { char -> char.isDigit() }) {
                                    pin = it
                                    errorMessage = null
                                }
                            },
                            enabled = !isLocked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            placeholder = {
                                Text(
                                    "Digite o PIN de 3 dígitos",
                                    fontSize = 13.sp,
                                    color = TextTertiary
                                )
                            },
                            singleLine = true,
                            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation('\u2022'),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            keyboardActions = KeyboardActions(onDone = { submitLogin() }),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isLocked) TextTertiary else WinePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showPin = !showPin },
                                    enabled = !isLocked
                                ) {
                                    Icon(
                                        imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Alternar visibilidade",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WinePrimary,
                                unfocusedBorderColor = BorderSubtle,
                                disabledBorderColor = BorderSubtle.copy(alpha = 0.5f),
                                focusedContainerColor = CardWhite,
                                unfocusedContainerColor = CardWhite,
                                disabledContainerColor = OffWhiteBackground
                            )
                        )

                        // Subtle hint note and quick clear
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PIN padrão do sistema: 000",
                                fontSize = 11.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Normal
                            )
                            if (pin.isNotEmpty() && !isLocked) {
                                Text(
                                    text = "Limpar",
                                    fontSize = 11.5.sp,
                                    color = WinePrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable { pin = "" }
                                )
                            }
                        }

                        // Lockout or Error Banner
                        if (isLocked) {
                            val mins = remainingSeconds / 60
                            val secs = remainingSeconds % 60
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusRedContainer)
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = StatusRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bloqueado por tentativas incorretas. Aguarde %02d:%02d ou contate o Admin.".format(mins, secs),
                                    color = StatusRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else if (errorMessage != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusRedContainer)
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = StatusRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = StatusRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Button: Solid Wine, Radius 14dp, subtle depth
                    Button(
                        onClick = { submitLogin() },
                        enabled = !isLocked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinePrimary,
                            contentColor = Color.White,
                            disabledContainerColor = WinePrimary.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 1.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = if (isLocked) "Acesso Bloqueado" else "Entrar no Sistema",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }

            // Minimalist Footer
            Text(
                text = "Santo Barril • Acesso Seguro por Cargo",
                fontSize = 11.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileSelectCard(
    initial: String,
    roleName: String,
    roleDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) WineContainer.copy(alpha = 0.35f) else CardWhite)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) WinePrimary else BorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Discreet circular avatar with clean initial monogram
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) WinePrimary else OffWhiteBackground)
                .border(1.dp, if (isSelected) WinePrimary else BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = if (isSelected) Color.White else WineDark
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = roleName,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = roleDescription,
                fontSize = 11.5.sp,
                color = if (isSelected) WineDark else TextSecondary,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Subtle indicator dot / radio indicator
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 5.dp else 1.dp,
                    color = if (isSelected) WinePrimary else BorderSubtle,
                    shape = CircleShape
                )
        )
    }
}
