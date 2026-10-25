package com.example.teamval.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.teamval.domain.model.Team
import com.example.teamval.domain.model.TeamRole
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.ProfileFrame

/**
 * Logo del equipo: dibuja la identidad visual personalizada de la etapa 4.1
 * (símbolo original o imagen recortada). Mismo tamaño en todas las pantallas.
 */
@Composable
fun TeamMonogram(
    team: Team,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    TeamIdentityLogo(team.identity, size = size, modifier = modifier)
}

/** Los marcos adquiridos rodean el logo, tanto en fichas como en la vista previa. */
@Composable
fun TeamIdentityLogo(identity: TeamIdentity, size: Dp, modifier: Modifier = Modifier) {
    val primary = argb(identity.primaryColor)
    val secondary = argb(identity.secondaryColor)
    val frame = when (identity.frame) {
        ProfileFrame.Orbit -> Modifier.border(2.dp, primary, CircleShape).padding(4.dp)
            .border(1.dp, secondary, CircleShape).padding(4.dp)
        ProfileFrame.Prism -> Modifier.border(3.dp,
            Brush.sweepGradient(listOf(primary, secondary, primary)), CircleShape).padding(6.dp)
        else -> Modifier
    }
    Box(modifier.size(size).then(frame)) {
        TeamLogoView(identity.logo, size = size)
    }
}

@Composable
fun TeamRoleBadge(role: TeamRole, modifier: Modifier = Modifier) {
    StatusBadge(text = stringResource(role.labelRes), modifier = modifier)
}
