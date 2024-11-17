package me.rosuh

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dokar.sonner.rememberToasterState
import kfollow.composeapp.generated.resources.Res
import kfollow.composeapp.generated.resources.logo
import kotlinx.coroutines.launch
import me.rosuh.data.api.SubscriptionType
import me.rosuh.data.api.Subscriptions
import me.rosuh.data.api.subscriptionType
import me.rosuh.data.api.subscriptionTypeListTitle
import me.rosuh.data.model.EntryData
import me.rosuh.data.model.SubscriptionsResponse
import me.rosuh.data.model.cover
import me.rosuh.data.model.realTitle
import me.rosuh.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App(mainViewModel: MainViewModel = koinInject()) {
    AppTheme {
        FLog.d("App", "mainViewModel: $mainViewModel")
        LaunchedEffect(Unit) {
            mainViewModel.processAction(MainViewModel.Action.Initialize)
        }
        // when login state is success, animating navigate to the main screen
        when (val loginState = mainViewModel.mainState.loginState) {
            is LoginState.Success -> MainScreen()
            else -> SplashScreen(loginState, goLogin = {
                mainViewModel.processAction(MainViewModel.Action.Login(it))
            }, resetToast = {
                mainViewModel.mainState.updateLoginState(LoginState.Idle)
            })
        }
    }
}

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : Screen("home") {
        val icon by lazy { me.rosuh.Icon.Home }
        val title by lazy { "Home" }
    }

    data object Search : Screen("search") {
        val icon by lazy { me.rosuh.Icon.Search }
        val title by lazy { "Search" }
    }

    data object Subscription : Screen("subscription") {
        val icon by lazy { me.rosuh.Icon.Subscription }
        val title by lazy { "Subscription" }
    }

    data object Setting : Screen("setting") {
        val icon by lazy { me.rosuh.Icon.Setting }
        val title by lazy { "Setting" }
    }
}


@Composable
fun MainScreen(mainViewModel: MainViewModel = koinInject()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        var subscriptionEnterTransition = remember {
            AnimatedContentTransitionScope.SlideDirection.Left
        }
        Box(
            Modifier.fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            var selectedItem by remember { mutableIntStateOf(0) }
            val items = listOf(
                Screen.Home.title to Screen.Home.icon,
                Screen.Subscription.title to Screen.Subscription.icon,
                Screen.Setting.title to Screen.Setting.icon
            )
            val transitionDuration = 350
            val navigationHeight = remember { mutableStateOf(0.dp) }
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(
                    Screen.Home.route,
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(transitionDuration)
                        )
                    }
                ) { backStackEntry ->
                    HomeScreen(
                        mainViewModel = mainViewModel,
                        navigationHeight = navigationHeight.value,
                        onPullToRefresh = { subscriptionType ->
                            mainViewModel.processAction(MainViewModel.Action.LoadHome(subscriptionType, isRefresh = true))
                        }
                    )
                }
                composable(
                    Screen.Subscription.route,
                    enterTransition = {
                        slideIntoContainer(
                            subscriptionEnterTransition,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(transitionDuration)
                        )
                    }
                ) {
                    Text("Subscription")
                }
                composable(
                    Screen.Setting.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(transitionDuration)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(transitionDuration)
                        )
                    }
                ) {
                    Text("Setting")
                }
            }
            val localDensity = LocalDensity.current
            Row(
                Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, shape = MaterialTheme.shapes.large)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.large
                    )
                    .onGloballyPositioned { coordinates ->
                        navigationHeight.value =
                            with(localDensity) { coordinates.size.height.toDp() }
                    }
                    .align(Alignment.BottomCenter)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                item.first,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        }
                    )
                }
            }
            when (selectedItem) {
                0 -> {
                    navController.popBackStack(Screen.Home.route, false)
                }

                1 -> {
                    subscriptionEnterTransition =
                        if (navController.currentDestination?.route != Screen.Setting.route) {
                            AnimatedContentTransitionScope.SlideDirection.Left
                        } else {
                            AnimatedContentTransitionScope.SlideDirection.Right
                        }
                    navController.navigate(Screen.Subscription.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                2 -> {
                    navController.navigate(Screen.Setting.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    navigationHeight: Dp,
    onPullToRefresh: (SubscriptionType) -> Unit,
) {
    LaunchedEffect(Unit) {
        mainViewModel.processAction(MainViewModel.Action.LoadHome(subscriptionType = SubscriptionType.Article))
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var state by remember { mutableStateOf(0) }
            val titles = subscriptionTypeListTitle
            Column(modifier = Modifier.fillMaxSize()) {
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState(pageCount = {
                    subscriptionTypeListTitle.size
                })
                PrimaryScrollableTabRow(
                    selectedTabIndex = state,
                    edgePadding = 8.dp,
                    divider = {},
                    indicator = @Composable {
                        Box(
                            modifier = Modifier
                                .offset(x = 8.dp, y = (-4).dp)
                                .size(4.dp)
                                .background(
                                    color = Color(162, 201, 133),
                                    shape = MaterialTheme.shapes.small
                                )
                                .shadow(
                                    3.dp,
                                    shape = MaterialTheme.shapes.small,
                                    clip = false,
                                    ambientColor = Color(162, 201, 133),
                                    spotColor = Color(162, 201, 133)
                                )
                        )
                    }
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = {
                                state = index
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                if (state == index) {
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        )
                    }
                }
                HorizontalPager(state = pagerState) { page ->
                    when (val type = page.subscriptionType) {
                        SubscriptionType.Article -> {
                            ArticleScreen(mainViewModel, navigationHeight)
                        }

                        SubscriptionType.SocialMedia -> {
                            SocialMediaScreen(mainViewModel, onPullToRefresh)
                        }

                        SubscriptionType.Image -> {
                            ImageScreen(mainViewModel, onPullToRefresh)
                        }

                        SubscriptionType.Video -> {
                            VideoScreen(mainViewModel, onPullToRefresh)
                        }

                        SubscriptionType.Audio -> {
                            AudioScreen(mainViewModel, onPullToRefresh)
                        }

                        SubscriptionType.Notification -> {
                            NotificationScreen(mainViewModel, onPullToRefresh)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationScreen(mainViewModel: MainViewModel, onPullToRefresh: (SubscriptionType) -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun AudioScreen(mainViewModel: MainViewModel, onPullToRefresh: (SubscriptionType) -> Unit) {

}

@Composable
fun VideoScreen(mainViewModel: MainViewModel, onPullToRefresh: (SubscriptionType) -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun ImageScreen(mainViewModel: MainViewModel, onPullToRefresh: (SubscriptionType) -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun SocialMediaScreen(mainViewModel: MainViewModel, onPullToRefresh: (SubscriptionType) -> Unit) {
    LaunchedEffect(Unit) {
        mainViewModel.processAction(MainViewModel.Action.LoadHome(SubscriptionType.SocialMedia))
    }
    BaseHomeContentScreen(SubscriptionType.SocialMedia, mainViewModel) {
        val socialState = mainViewModel.mainState.socialMediaState
        when {
            socialState is LoadState.Error && socialState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(socialState) {
                    mainViewModel.processAction(MainViewModel.Action.LoadHome(SubscriptionType.SocialMedia))
                }
            }
            socialState is LoadState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(socialState.data.allEntries) { item ->
                        SocialMediaItem(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialMediaItem(entryData: EntryData) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header with avatar and user info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FLog.d("SocialMediaItem", "entryData: ${entryData.entries.icon}")
                // Avatar
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(entryData.entries.icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Username and time
                Column {
                    Text(
                        text = entryData.feeds.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = entryData.entries.publishedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = entryData.entries.realTitle,
                style = MaterialTheme.typography.bodyMedium
            )

            // Image if available
            if (entryData.entries.media.isNullOrEmpty().not()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entryData.entries.media ?: emptyList()) { mediaItem ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(mediaItem.url)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interaction buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SocialButton(
                    icon = Icons.Default.ThumbUp,
                    count = "800",
                    onClick = { /* TODO */ }
                )
                SocialButton(
                    icon = Icons.Default.Favorite,
                    count = "102",
                    onClick = { /* TODO */ }
                )
                SocialButton(
                    icon = Icons.Default.Share,
                    count = "200",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
private fun SocialButton(
    icon: ImageVector,
    count: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseHomeContentScreen(
    type: SubscriptionType,
    mainViewModel: MainViewModel,
    content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = mainViewModel.mainState.isLoading(type),
        onRefresh = {
            mainViewModel.processAction(MainViewModel.Action.LoadHome(type, isRefresh = true))
        }
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleScreen(
    mainViewModel: MainViewModel,
    navigationHeight: Dp
) {
    BaseHomeContentScreen(SubscriptionType.Article, mainViewModel) {
        val articleState = mainViewModel.mainState.articleState
        when {
            articleState is LoadState.Error && articleState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(articleState) {
                    mainViewModel.processAction(MainViewModel.Action.LoadHome(SubscriptionType.Article))
                }
            }

            articleState is LoadState.Loading && articleState.isAppend -> {
                // 加载更多
            }

            articleState is LoadState.Success -> {
                Column {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        articleState.data.subscriptionEntriesMap.entries.forEach { feedWithList ->
                            val (subscription, entries) = feedWithList
                            stickyHeader(subscription.title) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = subscription.realTitle,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            when {
                                entries is LoadState.Success
                                        || entries is LoadState.Loading && entries.isAppend
                                        || entries is LoadState.Loading && entries.isRefresh -> {
                                            val data = (entries as? LoadState.Success)?.data ?: (entries as? LoadState.Loading)?.data
                                    items(data?.data ?: emptyList()) { item ->
                                        Row(
                                            modifier = Modifier.clickable {
                                                // todo jump to detail page
                                            }.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            ).defaultMinSize(minHeight = 68.dp).animateItem(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                                    .data(item.entries.icon.first).crossfade(true)
                                                    .build(),
                                                contentScale = ContentScale.Crop,
                                                contentDescription = "Website icon",
                                                modifier = Modifier.size(60.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    text = item.entries.realTitle,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = item.entries.publishedDate,
                                                        fontSize = 12.sp,
                                                        maxLines = 1,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = "·",
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = item.feeds.title ?: "",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }
                                entries is LoadState.Loading -> {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(16.dp).size(24.dp),
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    item {
                                        Text(
                                            text = "No data: ${articleState.data.message}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(navigationHeight)) }
                    }
                }

            }
        }
    }
}

@Composable
fun ErrorScreen(
    error: LoadState.Error<SubscriptionWithEntries>,
    retry: () -> Unit
) {
    // center the error message, with retry button
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val scrollState = rememberScrollState()
        Text(
            modifier = Modifier.padding(16.dp).verticalScroll(scrollState),
            text = "Error: ${error.throwable.message}",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            retry()
        }) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun SplashScreen(
    loginState: LoginState,
    goLogin: (provider: String) -> Unit,
    resetToast: (LoginState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(loginState, snackbarHostState) {
        // Listen to messages changes and map to toasts
        if (loginState is LoginState.Error) {
            snackbarHostState.showSnackbar(
                message = "Login failed: ${loginState.throwable.message}",
            )
        } else if (loginState is LoginState.Cancel) {
            snackbarHostState.showSnackbar(
                message = "Login canceled",
            )
        }
        resetToast(loginState)
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(2f).padding(16.dp)
                ) {
                    // Logo
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "KFollow Logo",
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Brand name
                    Text(
                        text = "KFollow",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(top = 16.dp)
                ) {
                    val isLoginGoogle =
                        loginState is LoginState.Loading && loginState.provider == "google"
                    val isLoginGithub =
                        loginState is LoginState.Loading && loginState.provider == "github"
                    val toaster = rememberToasterState()
                    Button(
                        onClick = {
                            // start the OAuth flow
                            goLogin("google")
                        },
                        enabled = !isLoginGoogle && !isLoginGithub
                    ) {
                        if (isLoginGoogle) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(text = "Login with Google")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // start the OAuth flow
                            goLogin("github")
                        },
                        enabled = !isLoginGoogle && !isLoginGithub
                    ) {
                        if (isLoginGithub) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(text = "Login with Github")
                        }
                    }
                }
            }
        }
    }
}