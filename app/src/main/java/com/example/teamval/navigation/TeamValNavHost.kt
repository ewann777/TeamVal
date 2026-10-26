package com.example.teamval.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.teamval.R
import com.example.teamval.domain.repository.RatingResult
import com.example.teamval.ui.screens.home.HomeScreen
import com.example.teamval.ui.screens.ratings.RateScrimScreen
import com.example.teamval.ui.screens.ratings.RateScrimViewModel
import com.example.teamval.ui.screens.ratings.RateUiState
import com.example.teamval.ui.screens.stacks.CreateStackScreen
import com.example.teamval.ui.screens.stacks.StackExplorerScreen
import com.example.teamval.ui.screens.stacks.StackRequestsScreen
import com.example.teamval.ui.screens.stacks.StacksUiState
import com.example.teamval.ui.screens.stacks.StacksViewModel
import com.example.teamval.ui.screens.profile.AppearanceScreen
import com.example.teamval.ui.screens.profile.EditProfileScreen
import com.example.teamval.ui.screens.profile.ProfileScreen
import com.example.teamval.ui.screens.profile.ProfileUiState
import com.example.teamval.ui.screens.profile.ProfileViewModel
import com.example.teamval.ui.screens.scrims.CreateScrimScreen
import com.example.teamval.ui.screens.scrims.ScrimDetailScreen
import com.example.teamval.ui.screens.teams.CreateTeamScreen
import com.example.teamval.ui.screens.teams.ImageCropScreen
import com.example.teamval.ui.screens.teams.LogoEditorScreen
import com.example.teamval.ui.screens.teams.TeamDetailScreen
import com.example.teamval.ui.screens.teams.TeamProfileEditorScreen
import com.example.teamval.ui.screens.teams.TeamsHubScreen
import com.example.teamval.ui.screens.teams.TeamsUiState
import com.example.teamval.ui.screens.teams.TeamsViewModel
import com.example.teamval.ui.screens.inventory.InventoryScreen
import com.example.teamval.ui.screens.rewards.RewardsScreen
import com.example.teamval.ui.screens.shop.EconomyUiState
import com.example.teamval.ui.screens.shop.EconomyViewModel
import com.example.teamval.ui.screens.shop.ShopScreen
import com.example.teamval.ui.screens.wallet.WalletScreen
import com.example.teamval.ui.components.labelRes

private const val StackExplorerRoute = "stacks/explore"
private const val CreateStackRoute = "stacks/create"
private const val StackRequestsRoute = "stacks/requests"
const val AppearanceRoute = "profile/appearance"
private const val ProfileOverviewRoute = "profile/overview"
private const val ProfileEditRoute = "profile/edit"

private const val ShopRoute = "shop"
private const val InventoryRoute = "shop/inventory"
private const val WalletRoute = "shop/wallet"
private const val RewardsRoute = "shop/rewards"

private const val TeamsHubRoute = "scrims/home"
private const val CreateTeamRoute = "scrims/team/create"
private const val TeamIdentityEditRoute = "scrims/team/identity"
private const val LogoEditorRoute = "scrims/team/identity/logo"
private const val ImageCropRoute = "scrims/team/identity/logo/crop"
private const val TeamDetailRoute = "scrims/team/{teamId}"
private const val CreateScrimRoute = "scrims/scrim/create"
private const val ScrimDetailRoute = "scrims/scrim/{scrimId}"
private const val RateScrimRoute = "scrims/rate/{scrimId}"

@Composable
fun TeamValNavHost(
    navController: NavHostController,
    stacksViewModel: StacksViewModel,
    stacksState: StacksUiState,
    profileViewModel: ProfileViewModel,
    profileState: ProfileUiState,
    teamsViewModel: TeamsViewModel,
    teamsState: TeamsUiState,
    ratingViewModel: RateScrimViewModel,
    ratingState: RateUiState,
    economyViewModel: EconomyViewModel,
    economyState: EconomyUiState,
    modifier: Modifier = Modifier
) {
    val myTeamId = teamsState.snapshot.teams
        .find { it.captain?.id == teamsViewModel.currentPlayer.id }?.id
    val openTeamEditor: () -> Unit = {
        if (myTeamId != null) {
            teamsViewModel.startEditingIdentity(myTeamId)
            navController.navigate(TeamIdentityEditRoute) { launchSingleTop = true }
        } else {
            teamsViewModel.notify(R.string.shop_error_no_permission)
        }
    }
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.Home.route,
        modifier = modifier
    ) {
        composable(TopLevelDestination.Home.route) {
            HomeScreen(
                onOpenStacks = { navController.navigateToTopLevel(TopLevelDestination.Stacks) },
                onOpenScrims = { navController.navigateToTopLevel(TopLevelDestination.Scrims) },
                onOpenStore = { navController.navigateToTopLevel(TopLevelDestination.Store) },
                balance = economyState.wallet.balance,
                onOpenWallet = { navController.navigate(WalletRoute) { launchSingleTop = true } }
            )
        }
        navigation(startDestination = StackExplorerRoute, route = TopLevelDestination.Stacks.route) {
            composable(StackExplorerRoute) {
                StackExplorerScreen(
                    state = stacksState,
                    currentPlayerId = stacksViewModel.currentPlayer.id,
                    currentPlayerName = profileState.saved?.profile?.name ?: stacksViewModel.currentPlayer.name,
                    onFiltersChange = stacksViewModel::setFilters,
                    onCategoryChange = stacksViewModel::showCategory,
                    onShowOwnPosts = stacksViewModel::showOwnPosts,
                    onCreate = {
                        stacksViewModel.startCreating()
                        navController.navigate(CreateStackRoute) { launchSingleTop = true }
                    },
                    onRequests = { postId ->
                        stacksViewModel.showSentRequests(false)
                        val route = if (postId == null) StackRequestsRoute else "$StackRequestsRoute?postId=$postId"
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onJoin = stacksViewModel::requestToJoin,
                    onSimulate = stacksViewModel::simulateRequest
                )
            }
            composable(CreateStackRoute) {
                CreateStackScreen(
                    draft = stacksState.local.draft,
                    invalidFields = stacksState.local.invalidFields,
                    onDraftChange = stacksViewModel::updateDraft,
                    onPublish = { if (stacksViewModel.createPost()) navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "$StackRequestsRoute?postId={postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) { entry ->
                StackRequestsScreen(
                    state = stacksState,
                    currentPlayerId = stacksViewModel.currentPlayer.id,
                    postId = entry.arguments?.getString("postId"),
                    onShowSent = stacksViewModel::showSentRequests,
                    onResolve = stacksViewModel::resolveRequest,
                    onSimulate = stacksViewModel::simulateRequest,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        navigation(startDestination = ProfileOverviewRoute, route = TopLevelDestination.Profile.route) {
            composable(ProfileOverviewRoute) {
                profileState.saved?.let { preferences ->
                    ProfileScreen(
                        profile = preferences.profile,
                        stats = profileViewModel.stats,
                        reputation = profileViewModel.reputation,
                        onEdit = {
                            profileViewModel.editProfile()
                            navController.navigate(ProfileEditRoute) { launchSingleTop = true }
                        },
                        onAppearance = {
                            profileViewModel.editAppearance()
                            navController.navigate(AppearanceRoute) { launchSingleTop = true }
                        }
                    )
                }
            }
            composable(ProfileEditRoute) {
                EditProfileScreen(
                    profileState,
                    economyState.lockedProfileVariants,
                    profileViewModel::updateProfile,
                    profileViewModel::saveProfile,
                    onDiscard = { profileViewModel.discardProfile(); navController.popBackStack() }
                )
            }
            composable(AppearanceRoute) {
                // Toda entrada parte de las preferencias guardadas y toda
                // salida (Volver, pestaña o gesto atrás) descarta lo no
                // guardado: el tema y el resto de la aplicación conservan
                // siempre la última selección confirmada con Guardar.
                LaunchedEffect(Unit) { profileViewModel.editAppearance() }
                DisposableEffect(Unit) { onDispose { profileViewModel.discardAppearance() } }
                AppearanceScreen(
                    profileState, profileViewModel::updateAppearance, profileViewModel::saveAppearance,
                    onDiscard = { profileViewModel.discardAppearance(); navController.popBackStack() },
                    onReset = profileViewModel::resetAppearance
                )
            }
        }
        navigation(startDestination = TeamsHubRoute, route = TopLevelDestination.Scrims.route) {
            composable(TeamsHubRoute) {
                TeamsHubScreen(
                    state = teamsState,
                    onSegmentChange = teamsViewModel::setSegment,
                    onCreateTeam = {
                        teamsViewModel.startCreatingTeam()
                        navController.navigate(CreateTeamRoute) { launchSingleTop = true }
                    },
                    onOpenTeam = { teamId -> navController.navigate("scrims/team/$teamId") { launchSingleTop = true } },
                    onCreateScrim = { rivalId ->
                        teamsViewModel.startProposingScrim(rivalId)
                        navController.navigate(CreateScrimRoute) { launchSingleTop = true }
                    },
                    onOpenScrim = { scrimId -> navController.navigate("scrims/scrim/$scrimId") { launchSingleTop = true } },
                    onSimulateScrim = teamsViewModel::simulateIncomingScrim
                )
            }
            composable(CreateTeamRoute) {
                CreateTeamScreen(
                    draft = teamsState.local.teamDraft,
                    invalidFields = teamsState.local.teamInvalid,
                    onDraftChange = teamsViewModel::updateTeamDraft,
                    onCreate = { if (teamsViewModel.createTeam()) navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TeamIdentityEditRoute) {
                val editingTeam = teamsState.local.identityTeamId?.let { teamsState.snapshot.team(it) }
                TeamProfileEditorScreen(
                    identity = teamsState.local.identityDraft,
                    invalid = teamsState.local.identityInvalid,
                    teamName = editingTeam?.name.orEmpty(),
                    teamTag = editingTeam?.tag.orEmpty(),
                    regionLabel = editingTeam?.region?.labelRes?.let { stringResource(it) }.orEmpty(),
                    ownedTeamCosmetics = economyState.ownedTeamCosmetics,
                    lockedTeamVariants = economyState.lockedTeamVariants,
                    onDraftChange = teamsViewModel::updateIdentityDraft,
                    onSave = {
                        if (teamsViewModel.saveIdentity()) {
                            navController.popBackStack()
                            true
                        } else false
                    },
                    onReset = teamsViewModel::resetIdentity,
                    onDiscard = teamsViewModel::cancelIdentity,
                    onEditLogo = { navController.navigate(LogoEditorRoute) { launchSingleTop = true } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(LogoEditorRoute) {
                LogoEditorScreen(
                    identity = teamsState.local.identityDraft,
                    tab = teamsState.local.logoTab,
                    onTabChange = teamsViewModel::setLogoTab,
                    onBuiltinChange = teamsViewModel::updateBuiltinLogo,
                    onImagePicked = { uri, width, height ->
                        teamsViewModel.setPendingImage(uri.toString(), width, height)
                        navController.navigate(ImageCropRoute) { launchSingleTop = true }
                    },
                    onRemoveImage = teamsViewModel::removeImageLogo,
                    onSave = {
                        if (teamsViewModel.saveLogo()) {
                            navController.popBackStack()
                            true
                        } else false
                    },
                    onDiscard = teamsViewModel::revertLogo,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ImageCropRoute) {
                ImageCropScreen(
                    uri = teamsState.local.pendingImageUri,
                    imageWidth = teamsState.local.pendingImageWidth,
                    imageHeight = teamsState.local.pendingImageHeight,
                    shape = teamsState.local.identityDraft.logo.builtin.shape,
                    onApplied = { fileName, crop ->
                        teamsViewModel.applyImageLogo(fileName, crop)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = TeamDetailRoute,
                arguments = listOf(navArgument("teamId") { type = NavType.StringType })
            ) { entry ->
                TeamDetailScreen(
                    state = teamsState,
                    teamId = entry.arguments?.getString("teamId").orEmpty(),
                    currentPlayerId = teamsViewModel.currentPlayer.id,
                    reputation = ratingViewModel.reputationOf(entry.arguments?.getString("teamId").orEmpty()),
                    onReport = { ratingId ->
                        val reported = ratingViewModel.reportComment(ratingId)
                        if (reported is RatingResult.Success) {
                            teamsViewModel.notify(R.string.reputation_report_done)
                        }
                    },
                    onCreateScrim = { rivalId ->
                        teamsViewModel.startProposingScrim(rivalId)
                        navController.navigate(CreateScrimRoute) { launchSingleTop = true }
                    },
                    onCustomize = {
                        teamsViewModel.startEditingIdentity(entry.arguments?.getString("teamId").orEmpty())
                        navController.navigate(TeamIdentityEditRoute) { launchSingleTop = true }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(CreateScrimRoute) {
                CreateScrimScreen(
                    state = teamsState,
                    draft = teamsState.local.scrimDraft,
                    invalidFields = teamsState.local.scrimInvalid,
                    onDraftChange = teamsViewModel::updateScrimDraft,
                    onSend = { if (teamsViewModel.proposeScrim()) navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = ScrimDetailRoute,
                arguments = listOf(navArgument("scrimId") { type = NavType.StringType })
            ) { entry ->
                val currentScrimId = entry.arguments?.getString("scrimId").orEmpty()
                ScrimDetailScreen(
                    state = teamsState,
                    scrimId = currentScrimId,
                    ratingBlock = ratingViewModel.blockFor(currentScrimId),
                    hasRated = ratingViewModel.hasRated(currentScrimId),
                    onRespond = teamsViewModel::respondToScrim,
                    onCancel = teamsViewModel::cancelScrim,
                    onResultChange = teamsViewModel::updateResult,
                    onComplete = { teamsViewModel.completeScrim(it) },
                    onRate = { scrimId ->
                        ratingViewModel.startRating(scrimId)
                        navController.navigate("scrims/rate/$scrimId") { launchSingleTop = true }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = RateScrimRoute,
                arguments = listOf(navArgument("scrimId") { type = NavType.StringType })
            ) { entry ->
                RateScrimScreen(
                    state = ratingState,
                    onScoreChange = ratingViewModel::setScore,
                    onCommentChange = ratingViewModel::setComment,
                    onSubmit = { ratingViewModel.submit() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        navigation(startDestination = ShopRoute, route = TopLevelDestination.Store.route) {
            composable(ShopRoute) {
                ShopScreen(
                    state = economyState,
                    catalog = economyViewModel.catalog,
                    onBuy = economyViewModel::purchase,
                    onEquip = economyViewModel::equip,
                    onUnequip = economyViewModel::unequip,
                    onOpenInventory = { navController.navigate(InventoryRoute) { launchSingleTop = true } },
                    onOpenWallet = { navController.navigate(WalletRoute) { launchSingleTop = true } },
                    onOpenRewards = { navController.navigate(RewardsRoute) { launchSingleTop = true } },
                    onOpenTeamEditor = openTeamEditor,
                    onClearUnlocked = economyViewModel::clearUnlocked
                )
            }
            composable(InventoryRoute) {
                InventoryScreen(
                    state = economyState,
                    catalog = economyViewModel.catalog,
                    onBack = { navController.popBackStack() },
                    onBuy = economyViewModel::purchase,
                    onEquip = economyViewModel::equip,
                    onUnequip = economyViewModel::unequip,
                    onOpenTeamEditor = openTeamEditor,
                    onClearUnlocked = economyViewModel::clearUnlocked
                )
            }
            composable(WalletRoute) {
                WalletScreen(
                    wallet = economyState.wallet,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(RewardsRoute) {
                RewardsScreen(
                    state = economyState,
                    missions = economyViewModel.missions,
                    onBack = { navController.popBackStack() },
                    onClaim = economyViewModel::claim,
                    onSimulate = economyViewModel::simulate
                )
            }
        }
    }
}
