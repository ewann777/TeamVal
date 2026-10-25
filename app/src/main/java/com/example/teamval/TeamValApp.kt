package com.example.teamval

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.teamval.data.fake.DemoProfileData
import com.example.teamval.data.fake.DemoRewardData
import com.example.teamval.data.fake.DemoShopCatalog
import com.example.teamval.data.local.EconomyDatabase
import com.example.teamval.data.local.RoomEconomyStorage
import com.example.teamval.data.repository.InMemoryStackRepository
import com.example.teamval.data.repository.InMemoryRatingRepository
import com.example.teamval.data.repository.InMemoryTeamRepository
import com.example.teamval.data.repository.LocalEconomyRepository
import com.example.teamval.data.repository.LocalPreferencesRepository
import com.example.teamval.data.repository.teamValPreferences
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.navigation.AppearanceRoute
import com.example.teamval.navigation.TeamValNavHost
import com.example.teamval.navigation.TeamValNavigationBar
import com.example.teamval.ui.screens.ratings.RateScrimViewModel
import com.example.teamval.ui.screens.shop.EconomyViewModel
import com.example.teamval.ui.screens.stacks.StacksViewModel
import com.example.teamval.ui.screens.teams.TeamsViewModel
import com.example.teamval.ui.screens.profile.ProfileViewModel
import com.example.teamval.ui.components.AppBackdrop
import com.example.teamval.ui.theme.TeamValTheme

@Composable
fun TeamValApp() {
    val navController = rememberNavController()
    val appContext = LocalContext.current.applicationContext
    val preferencesRepository = remember { LocalPreferencesRepository(appContext.teamValPreferences) }
    val profileViewModel: ProfileViewModel = viewModel(factory = remember(preferencesRepository) {
        viewModelFactory {
            initializer {
                ProfileViewModel(preferencesRepository, DemoProfileData.stats, DemoProfileData.reputation)
            }
        }
    })
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val savedAppearance = profileState.saved?.appearance ?: AppAppearance()
    val preview = profileState.appearanceDraft.takeIf { entry?.destination?.route == AppearanceRoute }
    val appearance = (preview ?: savedAppearance).let {
        if (it.isValid()) it else it.copy(accentHex = savedAppearance.accentHex)
    }
    val stacksViewModel: StacksViewModel = viewModel(factory = remember {
        viewModelFactory { initializer { StacksViewModel(InMemoryStackRepository()) } }
    })
    val stacksState by stacksViewModel.uiState.collectAsStateWithLifecycle()
    val teamsViewModel: TeamsViewModel = viewModel(factory = remember {
        viewModelFactory { initializer { TeamsViewModel(InMemoryTeamRepository()) } }
    })
    val teamsState by teamsViewModel.uiState.collectAsStateWithLifecycle()
    val ratingViewModel: RateScrimViewModel = viewModel(factory = remember(teamsViewModel) {
        viewModelFactory {
            initializer {
                val teamsRepository = teamsViewModel.repository
                RateScrimViewModel(InMemoryRatingRepository(teamsRepository), teamsRepository)
            }
        }
    })
    val ratingState by ratingViewModel.uiState.collectAsStateWithLifecycle()
    val economyViewModel: EconomyViewModel = viewModel(factory = remember(teamsViewModel) {
        viewModelFactory {
            initializer {
                val economyRepository = LocalEconomyRepository(
                    storage = RoomEconomyStorage(EconomyDatabase.get(appContext)),
                    catalog = DemoShopCatalog.items,
                    missions = DemoRewardData.missions
                )
                EconomyViewModel(
                    economy = economyRepository,
                    preferences = preferencesRepository,
                    teams = teamsViewModel.repository,
                    startingBonus = DemoShopCatalog.StartingBonus
                )
            }
        }
    })
    val economyState by economyViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(profileState.saved?.profile?.name) {
        profileState.saved?.profile?.name?.let {
            stacksViewModel.updatePlayerName(it)
            teamsViewModel.updatePlayerName(it)
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val notice = stacksState.local.notice
    val noticeText = notice?.let { stringResource(it.textRes) }
    LaunchedEffect(notice?.id) {
        if (notice != null && noticeText != null) {
            snackbarHostState.showSnackbar(noticeText)
            stacksViewModel.dismissNotice(notice.id)
        }
    }
    val teamNotice = teamsState.local.notice
    val teamNoticeText = teamNotice?.let { stringResource(it.textRes) }
    LaunchedEffect(teamNotice?.id) {
        if (teamNotice != null && teamNoticeText != null) {
            snackbarHostState.showSnackbar(teamNoticeText)
            teamsViewModel.dismissNotice(teamNotice.id)
        }
    }
    val economyNotice = economyState.notice
    val economyNoticeText = economyNotice?.let { n ->
        if (n.arg != null) stringResource(n.textRes, n.arg) else stringResource(n.textRes)
    }
    LaunchedEffect(economyNotice?.id) {
        if (economyNotice != null && economyNoticeText != null) {
            snackbarHostState.showSnackbar(economyNoticeText)
            economyViewModel.dismissNotice(economyNotice.id)
        }
    }

    TeamValTheme(appearance) {
        val view = LocalView.current
        val activity = LocalActivity.current
        val lightBars = MaterialTheme.colorScheme.background.luminance() > 0.5f
        SideEffect {
            activity?.let {
                WindowCompat.getInsetsController(it.window, view).apply {
                    isAppearanceLightStatusBars = lightBars
                    isAppearanceLightNavigationBars = lightBars
                }
            }
        }
        if (profileState.saved == null || profileState.loadError) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (profileState.loadError) {
                        Text(stringResource(R.string.preferences_load_error))
                        Button(onClick = profileViewModel::load) { Text(stringResource(R.string.preferences_retry)) }
                    } else {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.preferences_loading))
                    }
                }
            }
        } else AppBackdrop(appearance.background) {
            Scaffold(
                modifier = Modifier.fillMaxSize().imePadding(),
                containerColor = Color.Transparent,
                snackbarHost = {
                    SnackbarHost(snackbarHostState) { data ->
                        Snackbar(
                            data,
                            modifier = Modifier.testTag("snackbar_content")
                        )
                    }
                },
                bottomBar = { TeamValNavigationBar(navController, enabled = !profileState.saving) }
            ) { innerPadding ->
                TeamValNavHost(
                    navController = navController,
                    stacksViewModel = stacksViewModel,
                    stacksState = stacksState,
                    profileViewModel = profileViewModel,
                    profileState = profileState,
                    teamsViewModel = teamsViewModel,
                    teamsState = teamsState,
                    ratingViewModel = ratingViewModel,
                    ratingState = ratingState,
                    economyViewModel = economyViewModel,
                    economyState = economyState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                )
            }
        }
    }
}
