package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardWhite
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WineContainer
import com.example.ui.theme.WinePrimary

/**
 * Variantes de estilo para o BentoCard que definem as combinações
 * de cores mantendo a paleta padrão do Santo Barril (off-white e vinho).
 */
enum class BentoCardVariant {
    /** Superfície branca padrão com borda sutil e detalhes refinados */
    Default,

    /** Fundo com leve tonalidade off-white (#F8F7F2) para contraste suave */
    OffWhite,

    /** Destaque hero com fundo vinho (#800020) e conteúdo de alto contraste */
    WineHero,

    /** Variante vinho preenchido equivalente ao WineHero */
    WineFilled,

    /** Fundo com tonalidade suave de vinho (#F6EAEF) e borda vinho */
    WineTinted,

    /** Fundo transparente apenas com borda sutil */
    Outlined
}

/**
 * Componente Compose base 'BentoCard' que encapsula o design Bento Grid do Santo Barril.
 *
 * Aceita modificadores de tamanho livremente (ex: [Modifier.fillMaxWidth], [Modifier.height],
 * [Modifier.weight], [Modifier.aspectRatio], etc.), oferecendo cantos arredondados generosos (24.dp),
 * fundo off-white/branco puro, borda sutil e detalhes em vinho conforme as diretrizes do projeto.
 *
 * @param modifier Modificador aplicado ao card (tamanho, peso em grids, padding externo, etc.).
 * @param onClick Ação de clique opcional; se fornecida, ativa efeito ripple e semântica acessível.
 * @param variant Variante visual do card (Default, OffWhite, WineHero, WineFilled, WineTinted, Outlined).
 * @param cornerRadius Raio dos cantos arredondados (padrão: 24.dp).
 * @param shape Formato dos cantos (padrão: RoundedCornerShape com cornerRadius).
 * @param borderStroke Borda customizada opcional; se nula, utiliza a borda padrão da variante.
 * @param elevation Elevação da sombra em dp (padrão: 2.dp para profundidade sutil).
 * @param contentPadding Espaçamento interno do conteúdo (padrão: 16.dp).
 * @param content Conteúdo em escopo de [ColumnScope], permitindo organização vertical fluida.
 */
@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: BentoCardVariant = BentoCardVariant.Default,
    cornerRadius: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    borderStroke: BorderStroke? = null,
    elevation: Dp = 2.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val containerColor = when (variant) {
        BentoCardVariant.Default -> CardWhite
        BentoCardVariant.OffWhite -> OffWhiteBackground
        BentoCardVariant.WineHero, BentoCardVariant.WineFilled -> WinePrimary
        BentoCardVariant.WineTinted -> Color(0xFFFBF4F6)
        BentoCardVariant.Outlined -> Color.Transparent
    }

    val contentColor = when (variant) {
        BentoCardVariant.WineHero, BentoCardVariant.WineFilled -> Color.White
        else -> TextPrimary
    }

    val effectiveBorder = borderStroke ?: when (variant) {
        BentoCardVariant.Default -> BorderStroke(1.dp, BorderSubtle)
        BentoCardVariant.OffWhite -> BorderStroke(1.dp, BorderSubtle)
        BentoCardVariant.WineHero, BentoCardVariant.WineFilled -> null
        BentoCardVariant.WineTinted -> BorderStroke(1.5.dp, WinePrimary.copy(alpha = 0.35f))
        BentoCardVariant.Outlined -> BorderStroke(1.dp, BorderSubtle)
    }

    val cardModifier = if (onClick != null) {
        modifier.clickable(
            role = Role.Button,
            onClick = onClick
        )
    } else {
        modifier
    }

    val effectiveElevation = when (variant) {
        BentoCardVariant.Outlined -> 0.dp
        BentoCardVariant.WineHero, BentoCardVariant.WineFilled -> 3.dp
        else -> elevation.coerceAtMost(2.dp)
    }

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = effectiveBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = effectiveElevation
        )
    ) {
        Column(
            modifier = Modifier
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Cabeçalho padronizado para uso dentro de um [BentoCard].
 *
 * @param title Título do bloco em destaque.
 * @param subtitle Subtítulo opcional com tracking estendido.
 * @param icon Ícone opcional à esquerda do título.
 * @param iconTint Cor do ícone.
 * @param iconBg Cor de fundo do contêiner do ícone.
 * @param badgeText Texto de etiqueta/badge opcional à direita.
 * @param badgeBg Cor de fundo da etiqueta/badge.
 * @param badgeFg Cor do texto da etiqueta/badge.
 * @param actionSlot Slot opcional para botões de ação ou badges no canto superior direito.
 */
@Composable
fun BentoCardHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleColor: Color = WinePrimary,
    icon: ImageVector? = null,
    iconTint: Color = WinePrimary,
    iconBg: Color = WineContainer,
    badgeText: String? = null,
    badgeBg: Color? = null,
    badgeFg: Color? = null,
    actionSlot: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column {
                if (subtitle != null) {
                    Text(
                        text = subtitle.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeText != null) {
                val effectiveBadgeBg = badgeBg ?: WineContainer
                val effectiveBadgeFg = badgeFg ?: WinePrimary
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(effectiveBadgeBg)
                        .border(1.dp, effectiveBadgeFg.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = effectiveBadgeFg
                    )
                }
                if (actionSlot != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            if (actionSlot != null) {
                actionSlot()
            }
        }
    }
}

/**
 * Tile métrico Bento padronizado construído sobre [BentoCard].
 *
 * @param title Rótulo da métrica em caixa alta.
 * @param value Valor numérico ou texto principal em destaque.
 * @param icon Ícone representativo.
 * @param modifier Modificador de tamanho e layout.
 * @param badge Texto opcional de tag/badge (ex: "+12%", "Urgente", "Alvo").
 * @param badgeColor Cor da tag/badge.
 * @param isHighlighted Se true, utiliza variante [BentoCardVariant.WineHero] em vinho contrastante.
 * @param onClick Ação de toque opcional.
 */
@Composable
fun BentoMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    badge: String? = null,
    badgeColor: Color = WinePrimary,
    isHighlighted: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BentoCard(
        modifier = modifier.height(130.dp),
        variant = if (isHighlighted) BentoCardVariant.WineHero else BentoCardVariant.Default,
        onClick = onClick,
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isHighlighted) Color.White.copy(alpha = 0.2f) else WineContainer
                        )
                        .border(
                            1.dp,
                            if (isHighlighted) Color.White.copy(alpha = 0.35f) else WinePrimary.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isHighlighted) Color.White else WinePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isHighlighted) Color.White.copy(alpha = 0.2f) else badgeColor.copy(alpha = 0.12f)
                            )
                            .border(
                                1.dp,
                                if (isHighlighted) Color.White.copy(alpha = 0.35f) else badgeColor.copy(alpha = 0.35f),
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHighlighted) Color.White else badgeColor
                        )
                    }
                }
            }

            Column {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = if (isHighlighted) Color.White.copy(alpha = 0.75f) else TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted) Color.White else TextPrimary
                )
            }
        }
    }
}
