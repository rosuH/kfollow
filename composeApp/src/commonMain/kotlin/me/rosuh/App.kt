package me.rosuh

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kfollow.composeapp.generated.resources.Res
import kfollow.composeapp.generated.resources.logo
import kotlinx.coroutines.launch
import me.rosuh.Icon.AudioMoreFeeds
import me.rosuh.Icon.AudioPlaySolid
import me.rosuh.Icon.ImgPlaceholder
import me.rosuh.Icon.Play
import me.rosuh.data.api.SubscriptionType
import me.rosuh.data.api.subscriptionType
import me.rosuh.data.api.subscriptionTypeList
import me.rosuh.data.api.subscriptionTypeListTitle
import me.rosuh.data.model.EntryData
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
        Surface {
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
}

sealed class Screen(
    val route: String, val navArguments: List<NamedNavArgument> = emptyList()
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


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = koinInject()) {
    val navController = rememberNavController()
    var subscriptionEnterTransition = remember {
        AnimatedContentTransitionScope.SlideDirection.Left
    }
    Box(Modifier.fillMaxSize()) {
        val hazeState = remember { HazeState() }
        var selectedItem by remember { mutableIntStateOf(0) }
        val items = listOf(
            Screen.Home.title to Screen.Home.icon,
            Screen.Subscription.title to Screen.Subscription.icon,
            Screen.Setting.title to Screen.Setting.icon
        )
        val transitionDuration = 350
        val navigationHeight = remember { mutableStateOf(0.dp) }
        NavHost(
            modifier = Modifier.haze(hazeState),
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(transitionDuration)
                )
            }, popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(transitionDuration)
                )
            }) { backStackEntry ->
                HomeScreen(mainViewModel = mainViewModel,
                    navigationHeight = navigationHeight.value,
                    onPullToRefresh = { subscriptionType ->
                        mainViewModel.processAction(
                            MainViewModel.Action.LoadHome(
                                subscriptionType, isRefresh = true
                            )
                        )
                    })
            }
            composable(Screen.Subscription.route, enterTransition = {
                slideIntoContainer(
                    subscriptionEnterTransition, animationSpec = tween(transitionDuration)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(transitionDuration)
                )
            }, popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(transitionDuration)
                )
            }, popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(transitionDuration)
                )
            }) {
                Text("Subscription")
            }
            composable(Screen.Setting.route, enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(transitionDuration)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(transitionDuration)
                )
            }, popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(transitionDuration)
                )
            }, popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(transitionDuration)
                )
            }) {
                Text("Setting")
            }
        }
        val localDensity = LocalDensity.current
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding()
                .shadow(1.dp, shape = MaterialTheme.shapes.large).background(
                    Color.Transparent, shape = MaterialTheme.shapes.large.copy(
                        bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
                    )
                ).onGloballyPositioned { coordinates ->
                    navigationHeight.value = with(localDensity) { coordinates.size.height.toDp() }
                }.align(Alignment.BottomCenter).hazeChild(
                    hazeState, style = HazeMaterials.thin(MaterialTheme.colorScheme.background)
                )
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(icon = {
                    Icon(
                        item.second,
                        contentDescription = item.first,
                        modifier = Modifier.size(24.dp)
                    )
                }, label = {
                    Text(
                        item.first, style = MaterialTheme.typography.bodySmall, maxLines = 1
                    )
                }, selected = selectedItem == index, onClick = {
                    selectedItem = index
                })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onPullToRefresh: (SubscriptionType) -> Unit,
    navigationHeight: Dp = 0.dp
) {
    var tabState by remember { mutableStateOf(0) }
    val titles = subscriptionTypeListTitle
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = {
        subscriptionTypeListTitle.size
    })
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            tabState = page
            mainViewModel.processAction(
                MainViewModel.Action.LoadHome(
                    subscriptionTypeList[page], isRefresh = false
                )
            )
        }
    }
    Scaffold(topBar = {
        PrimaryScrollableTabRow(modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding(),
            containerColor = MaterialTheme.colorScheme.surface,
            selectedTabIndex = tabState,
            edgePadding = 0.dp,
            divider = {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    thickness = 0.5.dp
                )
            },
            indicator = { FancyAnimatedIndicatorWithModifier(tabState) }) {
            titles.forEachIndexed { index, title ->
                Tab(modifier = Modifier, selected = tabState == index, onClick = {
                    tabState = index
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = {
                    Column {
                        if (tabState == index) {
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
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                })
            }
        }
    }) { contentPadding ->
        HorizontalPager(state = pagerState, contentPadding = contentPadding) { page ->
            val pageModifier = Modifier.fillMaxSize().padding(bottom = navigationHeight)
            when (page.subscriptionType) {
                SubscriptionType.Article -> {
                    ArticleScreen(
                        mainViewModel.mainState.articleState,
                        modifier = pageModifier,
                        onLoadHome = { isRefresh: Boolean, isAppend: Boolean ->
                            mainViewModel.processAction(
                                MainViewModel.Action.LoadHome(
                                    SubscriptionType.Article,
                                    append = isAppend,
                                    isRefresh = isRefresh
                                )
                            )
                        },
                        onGoDetail = { mainViewModel.processAction(MainViewModel.Action.GoDetail(it)) },
                    )
                }

                SubscriptionType.SocialMedia -> {
                    SocialMediaScreen(
                        mainViewModel.mainState.socialMediaState,
                        modifier = pageModifier,
                        onLoadHome = { isRefresh: Boolean, isAppend: Boolean ->
                            mainViewModel.processAction(
                                MainViewModel.Action.LoadHome(
                                    SubscriptionType.SocialMedia,
                                    append = isAppend,
                                    isRefresh = isRefresh
                                )
                            )
                        },
                        onGoDetail = { mainViewModel.processAction(MainViewModel.Action.GoDetail(it)) },
                        getOrPutLazyColumnState = { type, uuid ->
                            mainViewModel.mainState.getOrPutLazyColumnState(type, uuid)
                        }
                    )
                }

                SubscriptionType.Image -> {
                    ImageScreen(
                        mainViewModel.mainState.imageState,
                        modifier = pageModifier,
                        onLoadHome = { isRefresh: Boolean, isAppend: Boolean ->
                            mainViewModel.processAction(
                                MainViewModel.Action.LoadHome(
                                    SubscriptionType.Image,
                                    append = isAppend,
                                    isRefresh = isRefresh
                                )
                            )
                        },
                        onGoDetail = { mainViewModel.processAction(MainViewModel.Action.GoDetail(it)) },
                    )
                }

                SubscriptionType.Video -> {
                    VideoScreen(
                        mainViewModel.mainState.videoState,
                        modifier = pageModifier,
                        onLoadHome = { isRefresh: Boolean, isAppend: Boolean ->
                            mainViewModel.processAction(
                                MainViewModel.Action.LoadHome(
                                    SubscriptionType.Video,
                                    append = isAppend,
                                    isRefresh = isRefresh
                                )
                            )
                        },
                        onGoDetail = { mainViewModel.processAction(MainViewModel.Action.GoDetail(it)) },
                    )
                }

                SubscriptionType.Audio -> {
                    AudioScreen(
                        mainViewModel.mainState.audioState,
                        modifier = pageModifier,
                        onLoadHome = { isRefresh: Boolean, isAppend: Boolean ->
                            mainViewModel.processAction(
                                MainViewModel.Action.LoadHome(
                                    SubscriptionType.Audio,
                                    append = isAppend,
                                    isRefresh = isRefresh
                                )
                            )
                        },
                        onGoDetail = { mainViewModel.processAction(MainViewModel.Action.GoDetail(it)) },
                        goFeedList = {
                            // todo
                        }
                    )
                }

                SubscriptionType.Notification -> {
                    NotificationScreen(mainViewModel,
                        onPullToRefresh,
                        onGoDetail = { mainViewModel.processAction(MainViewModel.Action.GoDetail(it)) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabIndicatorScope.FancyAnimatedIndicatorWithModifier(index: Int) {
    var startAnimatable by remember { mutableStateOf<Animatable<Dp, AnimationVector1D>?>(null) }
    var endAnimatable by remember { mutableStateOf<Animatable<Dp, AnimationVector1D>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val indicatorColor: Color by animateColorAsState(MaterialTheme.colorScheme.primary)

    // 添加一个动画值来控制水滴变形程度
    val deformationAnimatable = remember { Animatable(0f) }

    Box(Modifier.fillMaxWidth().tabIndicatorLayout { measurable, constraints, tabPositions ->
        val tabWidth = (tabPositions[index].right - tabPositions[index].left)
        val tabCenter = tabPositions[index].left + (tabWidth / 2)
        val indicatorWidthPx = 4.dp
        val newStartPx = tabCenter - (indicatorWidthPx / 2)
        val newStart = newStartPx
        val newEnd = newStartPx + indicatorWidthPx

        val startAnim = startAnimatable ?: Animatable(
            newStart,
            Dp.VectorConverter
        ).also { startAnimatable = it }

        val endAnim = endAnimatable ?: Animatable(newEnd, Dp.VectorConverter).also {
            endAnimatable = it
        }

        // 计算移动方向和距离
        val isMovingRight = startAnim.targetValue > startAnim.value

        // 启动变形动画
        coroutineScope.launch {
            deformationAnimatable.animateTo(
                targetValue = if (isMovingRight) 1f else -1f, animationSpec = spring(
                    dampingRatio = 0.7f, stiffness = 300f
                )
            )
            // 动画结束后恢复原形
            deformationAnimatable.animateTo(
                targetValue = 0f, animationSpec = spring(
                    dampingRatio = 0.7f, stiffness = 300f
                )
            )
        }

        if (endAnim.targetValue != newEnd) {
            coroutineScope.launch {
                endAnim.animateTo(
                    newEnd, animationSpec = spring(dampingRatio = 1f, stiffness = 500f)
                )
            }
        }

        if (startAnim.targetValue != newStart) {
            coroutineScope.launch {
                startAnim.animateTo(
                    newStart, animationSpec = spring(dampingRatio = 1f, stiffness = 500f)
                )
            }
        }

        val indicatorEnd = endAnim.value.roundToPx()
        val indicatorStart = startAnim.value.roundToPx()

        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = indicatorEnd - indicatorStart,
                minWidth = indicatorEnd - indicatorStart,
            )
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(indicatorStart, constraints.maxHeight - 10.dp.roundToPx())
        }
    }.drawBehind {
        val deformation = deformationAnimatable.value

        // 计算水滴变形
        val baseRadius = 2.dp.toPx()
        val leftRadius = baseRadius * (1f - deformation * 0.3f)
        val rightRadius = baseRadius * (1f + deformation * 0.3f)

        // Draw glow effect
        for (i in 5 downTo 0) {
            val glowRadius = (4 + i).dp.toPx()
            val leftGlowRadius = glowRadius * (1f - deformation * 0.3f)
            val rightGlowRadius = glowRadius * (1f + deformation * 0.3f)

            // 绘制变形的发光效果
            drawOval(
                color = indicatorColor.copy(alpha = 0.1f / (i + 1)), topLeft = Offset(
                    center.x - leftGlowRadius, center.y - glowRadius
                ), size = Size(
                    leftGlowRadius + rightGlowRadius, glowRadius * 2
                )
            )
        }

        // 绘制变形的主点
        drawOval(
            color = indicatorColor, topLeft = Offset(
                center.x - leftRadius, center.y - baseRadius
            ), size = Size(
                leftRadius + rightRadius, baseRadius * 2
            )
        )
    })
}

@Composable
fun NotificationScreen(
    mainViewModel: MainViewModel,
    onPullToRefresh: (SubscriptionType) -> Unit,
    onGoDetail: (EntryData) -> Unit = {}
) {

}

@Composable
fun AudioScreen(
    audioState: LoadState<SubscriptionWithEntries>,
    modifier: Modifier = Modifier.fillMaxSize(),
    onLoadHome: (isRefresh: Boolean, isAppend: Boolean) -> Unit,
    onGoDetail: (EntryData) -> Unit = {},
    goFeedList: (type: SubscriptionType) -> Unit = { _ -> }
) {
    BaseHomeContentScreen(
        modifier = modifier, isRefreshing = audioState is LoadState.Loading, onLoadHome = onLoadHome
    ) {
        when {
            audioState is LoadState.Error && audioState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(audioState) {
                    onLoadHome(true, false)
                }
            }

            audioState is LoadState.Success -> {
                val feedList = remember {
                    audioState.data.feeds.take(7)
                }
                val isMoreThan7 = remember {
                    audioState.data.feeds.size > 7
                }
                val cellSize = remember {
                    72.dp
                }
                val gridHeight = remember {
                    derivedStateOf {
                        if (feedList.size >= 4) {
                            cellSize * 2 + 16.dp
                        } else {
                            cellSize + 16.dp
                        }
                    }
                }
                val size = calculateWindowSizeClass()
                val gridWidthModifier = remember {
                    derivedStateOf {
                        if (size.widthSizeClass == WindowWidthSizeClass.Compact) {
                            Modifier.fillMaxWidth().padding(bottom = 10.dp).height(gridHeight.value)
                        } else {
                            Modifier.width(360.dp).padding(bottom = 10.dp).height(gridHeight.value)
                        }
                    }
                }
                val gridColumns = remember {
                    if (size.widthSizeClass == WindowWidthSizeClass.Compact) {
                        GridCells.Fixed(4)
                    } else {
                        GridCells.Adaptive(72.dp)
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    item {
                        LazyVerticalGrid(
                            columns = gridColumns,
                            modifier = gridWidthModifier.value,
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            userScrollEnabled = false
                        ) {
                            items(feedList) { item ->
                                AsyncImage(
                                    model = item.cover,
                                    contentDescription = item.title,
                                    modifier = Modifier.height(72.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (isMoreThan7) {
                                item {
                                    // More button
                                    IconButton(
                                        onClick = {
                                            goFeedList(SubscriptionType.Audio)
                                        },
                                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            AudioMoreFeeds,
                                            contentDescription = "More",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    items(audioState.data.allEntries) { item ->
                        AudioFeedItem(Modifier.clickable { onGoDetail(item) }, item, onClick = {
                            onGoDetail(item)
                        })
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioFeedItem(
    clickable: Modifier,
    item: EntryData,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = clickable
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = item.feeds.cover,
            contentDescription = item.entries.title,
            modifier = Modifier.size(90.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(20.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = item.entries.realTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.feeds.title ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = item.entries.publishedDate,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(36.dp).align(Alignment.BottomEnd),
                colors = iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Icon(
                    AudioPlaySolid,
                    contentDescription = "Play",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun VideoScreen(
    videoState: LoadState<SubscriptionWithEntries>,
    modifier: Modifier = Modifier.fillMaxSize(),
    onLoadHome: (isRefresh: Boolean, isAppend: Boolean) -> Unit,
    onGoDetail: (EntryData) -> Unit = {}
) {
    BaseHomeContentScreen(
        modifier = modifier, isRefreshing = videoState is LoadState.Loading, onLoadHome = onLoadHome
    ) {
        val size = calculateWindowSizeClass()
        val cellWidth = remember {
            if (size.widthSizeClass == WindowWidthSizeClass.Compact) {
                StaggeredGridCells.Fixed(2)
            } else {
                StaggeredGridCells.Adaptive(190.dp)
            }
        }
        when {
            videoState is LoadState.Error && videoState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(videoState) {
                    onLoadHome(true, false)
                }
            }

            videoState is LoadState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = cellWidth,
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(videoState.data.allEntries) { item ->
                        ImageFeedItem(Modifier.clickable { onGoDetail(item) }, item, true)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageScreen(
    imageState: LoadState<SubscriptionWithEntries>,
    modifier: Modifier = Modifier.fillMaxSize(),
    onLoadHome: (isRefresh: Boolean, isAppend: Boolean) -> Unit,
    onGoDetail: (EntryData) -> Unit = {}
) {
    BaseHomeContentScreen(
        modifier = modifier, isRefreshing = imageState is LoadState.Loading, onLoadHome = onLoadHome
    ) {
        val size = calculateWindowSizeClass()
        val cellWidth = remember {
            if (size.widthSizeClass == WindowWidthSizeClass.Compact) {
                StaggeredGridCells.Fixed(2)
            } else {
                StaggeredGridCells.Adaptive(190.dp)
            }
        }
        when {
            imageState is LoadState.Error && imageState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(imageState) {
                    onLoadHome(true, false)
                }
            }

            imageState is LoadState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = cellWidth,
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(imageState.data.allEntries) { item ->
                        ImageFeedItem(Modifier.clickable { onGoDetail(item) }, item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageFeedItem(
    modifier: Modifier = Modifier, item: EntryData, isVideo: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(250.dp)) {
            AsyncImage(
                model = item.entries.media?.firstOrNull()?.url
                    ?: item.entries.attachments?.firstOrNull()?.url ?: item.feeds.cover,
                contentDescription = item.entries.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (isVideo) {
                Image(
                    Play,
                    contentDescription = "Play",
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd).size(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                )
            } else {
                Text(
                    text = item.entries.publishedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(8.dp)
        ) {
            Text(
                text = item.entries.realTitle, style = MaterialTheme.typography.titleSmall,
//                color = Color.White,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.feeds.title ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SocialMediaScreen(
    socialMediaState: LoadState<SubscriptionWithEntries>,
    modifier: Modifier = Modifier.fillMaxSize(),
    onLoadHome: (isRefresh: Boolean, isAppend: Boolean) -> Unit,
    onGoDetail: (EntryData) -> Unit = {},
    getOrPutLazyColumnState: (SubscriptionType, String) -> LazyListState = { _, _ ->
        LazyListState()
    }
) {
    BaseHomeContentScreen(
        modifier = modifier,
        isRefreshing = socialMediaState is LoadState.Loading,
        onLoadHome = onLoadHome
    ) {
        when {
            socialMediaState is LoadState.Error && socialMediaState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(socialMediaState) {
                    onLoadHome(true, false)
                }
            }

            socialMediaState is LoadState.Success -> {
                LazyColumn(
                    state = getOrPutLazyColumnState(
                        SubscriptionType.SocialMedia, socialMediaState.data.uuid
                    ), modifier = Modifier.fillMaxSize()
                ) {
                    items(socialMediaState.data.allEntries, key = {
                        it.entries.id
                    }, contentType = {
                        SubscriptionType.Image
                    }) { item ->
                        Column(modifier = Modifier.clickable {
                            onGoDetail(item)
                        }) {
                            SocialMediaItem(modifier = Modifier.animateItem(), item)
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialMediaItem(
    modifier: Modifier = Modifier, entryData: EntryData, onClickReadMore: (EntryData) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        // Header with avatar and user info
        Row(modifier = Modifier.fillMaxWidth()) {
            // Avatar
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(entryData.entries.icon.first).crossfade(true).build(),
                contentDescription = "Avatar",
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.offset(y = (-4).dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Username and time
                    Text(
                        text = entryData.feeds.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " · ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entryData.entries.publishedDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                // Content
                Text(
                    text = entryData.entries.realTitle, style = MaterialTheme.typography.bodyLarge
                )

                if (entryData.entries.media?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    when (entryData.entries.media.size) {
                        1 -> {
                            // Single image, fill the width and aspect ratio 16:9
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(entryData.entries.media.first().url).crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f).border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = MaterialTheme.shapes.medium
                                ).clip(MaterialTheme.shapes.medium),
                                placeholder = rememberVectorPainter(ImgPlaceholder),
                                error = rememberVectorPainter(ImgPlaceholder),
                                contentScale = ContentScale.Crop
                            )
                        }

                        else -> {
                            // Two images, fill the width and aspect ratio 16:9
                            val imageHeight = remember { mutableStateOf(0.dp) }
                            val density = LocalDensity.current
                            Row(modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f).border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.medium
                            ).clip(MaterialTheme.shapes.medium).onGloballyPositioned {
                                imageHeight.value = if (entryData.entries.media.size <= 2) {
                                    with(density) { it.size.height.toDp() }
                                } else {
                                    with(density) { it.size.height.toDp() / 2 }
                                }
                            }) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(entryData.entries.media.take(4)) { mediaItem ->
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                                .data(mediaItem.url).crossfade(true).build(),
                                            placeholder = rememberVectorPainter(ImgPlaceholder),
                                            error = rememberVectorPainter(ImgPlaceholder),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxWidth()
                                                .height(imageHeight.value),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseHomeContentScreen(
    modifier: Modifier,
    isRefreshing: Boolean,
    onLoadHome: (isRefresh: Boolean, isAppend: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            onLoadHome(true, false)
        }) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleScreen(
    articleState: LoadState<SubscriptionWithEntries>,
    modifier: Modifier = Modifier.fillMaxSize(),
    onLoadHome: (isRefresh: Boolean, isAppend: Boolean) -> Unit,
    onGoDetail: (EntryData) -> Unit = {}
) {
    BaseHomeContentScreen(
        modifier = modifier,
        isRefreshing = articleState is LoadState.Loading,
        onLoadHome = onLoadHome
    ) {
        val listState: LazyListState = rememberLazyListState()
        when {
            articleState is LoadState.Error && articleState.data?.subscriptionEntriesMap.isNullOrEmpty() -> {
                ErrorScreen(articleState) {
                    onLoadHome(true, false)
                }
            }

            articleState is LoadState.Loading && articleState.isAppend -> {
                // 加载更多
            }

            articleState is LoadState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), state = listState
                ) {
                    FLog.d("ArticleScreen", "stickyOffset")
                    articleState.data.subscriptionEntriesMap.entries.forEach { feedWithList ->
                        val (subscription, entries) = feedWithList
                        stickyHeader(subscription.feedId, contentType = "sticky") {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.background,
                                            MaterialTheme.colorScheme.background,
                                            MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                        )
                                    )
                                ).padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = subscription.realTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                TextButton(onClick = {
                                    // todo jump to subscription detail page
                                }) {
                                    Text(
                                        text = "更多 >",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                        when {
                            entries is LoadState.Success || entries is LoadState.Loading && entries.isAppend || entries is LoadState.Loading && entries.isRefresh -> {
                                val data = (entries as? LoadState.Success)?.data
                                    ?: (entries as? LoadState.Loading)?.data
                                items(data?.data ?: emptyList(),
                                    key = { item: EntryData -> item.entries.id }) { item ->
                                    Row(
                                        modifier = Modifier.clickable {
                                            onGoDetail.invoke(item)
                                        }.padding(
                                            horizontal = 16.dp, vertical = 8.dp
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
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.8f
                                                    ),
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "·",
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.8f
                                                    ),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = item.feeds.title ?: "",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.8f
                                                    ),
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
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
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
//                        item { Spacer(modifier = Modifier.height(navigationHeight)) }
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(
    error: LoadState.Error<SubscriptionWithEntries>, retry: () -> Unit
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
    loginState: LoginState, goLogin: (provider: String) -> Unit, resetToast: (LoginState) -> Unit
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
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
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
                        }, enabled = !isLoginGoogle && !isLoginGithub
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
                        }, enabled = !isLoginGoogle && !isLoginGithub
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