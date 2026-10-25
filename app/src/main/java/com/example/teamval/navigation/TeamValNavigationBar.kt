package com.example.teamval.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.teamval.ui.components.HairlineDivider
import com.example.teamval.ui.theme.LocalTeamValVisuals

/**
 * Barra inferior con acabado de vidrio: superficie translúcida, filete de luz
 * superior y píldora de acento en la pestaña activa.
 */
@Composable
fun TeamValNavigationBar(navController: NavHostController, enabled: Boolean = true) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val colors = MaterialTheme.colorScheme
    val visuals = LocalTeamValVisuals.current

    Column(Modifier.fillMaxWidth()) {
        HairlineDivider()
        NavigationBar(
            containerColor = colors.surface.copy(alpha = 0.88f),
            tonalElevation = 0.dp,
            modifier = Modifier.shadow(
                elevation = 14.dp,
                shape = RectangleShape,
                ambientColor = visuals.shadowColor,
                spotColor = visuals.shadowColor
            )
        ) {
            TopLevelDestination.entries.forEach { destination ->
                NavigationBarItem(
                    enabled = enabled,
                    selected = currentDestination?.hierarchy?.any {
                        it.route == destination.route
                    } == true,
                    onClick = { navController.navigateToTopLevel(destination) },
                    modifier = Modifier.testTag("nav_${destination.route}"),
                    icon = {
                        Icon(
                            painter = painterResource(destination.iconRes),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(destination.labelRes)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.primary.copy(alpha = 0.18f),
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}
