package me.rosuh

import androidx.collection.LruCache
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.ktor.http.Url
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import me.rosuh.data.AuthService
import me.rosuh.data.NetworkManager
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError
import me.rosuh.data.api.EntriesApi
import me.rosuh.data.api.SubscriptionType
import me.rosuh.data.api.SubscriptionsApi
import me.rosuh.data.model.EntryData
import me.rosuh.data.model.PostEntriesResponse
import me.rosuh.data.model.SubscriptionsResponse
import me.rosuh.data.model.cover
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class LoginState {
    data object Idle : LoginState()
    data class Loading(val provider: String) : LoginState()
    data class Success(val first: Boolean, val token: String) : LoginState()
    data class Error(val throwable: Throwable, val provider: String) : LoginState()
    data class Cancel(val throwable: Throwable, val provider: String) : LoginState()
}

sealed class LoadState<R> {
    abstract val data: R?
    data class Loading<R>(
        val isRefresh: Boolean = false,
        val retryCount: Int = 0,
        val isAppend: Boolean = false,
        override val data: R? = null
    ) : LoadState<R>()

    data class Success<R>(override val data: R, val isAppend: Boolean = false) : LoadState<R>()
    data class Error<R>(val throwable: Throwable, override val data: R? = null) : LoadState<R>()
}

data class SubscriptionWithEntries(
    val subscriptionEntriesMap: Map<SubscriptionsResponse.Subscription, LoadState<PostEntriesResponse>>,
    val message: String = ""
) {
    @OptIn(ExperimentalUuidApi::class)
    val uuid by lazy {
        Uuid.random().toString()
    }

    val allEntries: List<EntryData> = subscriptionEntriesMap.values.mapNotNull {
        (it as? LoadState.Success)?.data?.data ?: emptyList()
    }.flatten().sortedByDescending {
        it.entries.publishedAt
    }
}

/**
 * @param articleState checkout [LoadState] and [me.rosuh.data.api.SubscriptionType]
 */
@Stable
class MainState(
    isInitialized: Boolean = false,
    loginState: LoginState = LoginState.Idle,
    articleState: LoadState<SubscriptionWithEntries> = LoadState.Loading(),
    socialMediaState: LoadState<SubscriptionWithEntries> = LoadState.Loading(),
    imageState: LoadState<SubscriptionWithEntries> = LoadState.Loading(),
    videoState: LoadState<SubscriptionWithEntries> = LoadState.Loading(),
    audioState: LoadState<SubscriptionWithEntries> = LoadState.Loading(),
    notificationState: LoadState<SubscriptionWithEntries> = LoadState.Loading()
) {

    var loginState by mutableStateOf(loginState)

    var articleState by mutableStateOf(articleState)
    var socialMediaState by mutableStateOf(socialMediaState)
    var imageState by mutableStateOf(imageState)
    var videoState by mutableStateOf(videoState)
    var audioState by mutableStateOf(audioState)
    var notificationState by mutableStateOf(notificationState)

    fun getViewState(subscriptionType: SubscriptionType): LoadState<SubscriptionWithEntries> {
        return when (subscriptionType) {
            SubscriptionType.Article -> articleState
            SubscriptionType.SocialMedia -> socialMediaState
            SubscriptionType.Image -> imageState
            SubscriptionType.Video -> videoState
            SubscriptionType.Audio -> audioState
            SubscriptionType.Notification -> notificationState
        }
    }

    fun isLoading(subscriptionType: SubscriptionType): Boolean {
        return getViewState(subscriptionType) is LoadState.Loading
    }


    fun updateLoginState(newState: LoginState) {
        loginState = newState
    }

    fun updateSubscriptionState(
        subscriptionType: SubscriptionType,
        newState: LoadState<SubscriptionWithEntries>
    ) {
        when (subscriptionType) {
            SubscriptionType.Article -> {
                articleState = newState
            }

            SubscriptionType.SocialMedia -> {
                socialMediaState = newState
            }

            SubscriptionType.Image -> {
                imageState = newState
            }

            SubscriptionType.Video -> {
                videoState = newState
            }

            SubscriptionType.Audio -> {
                audioState = newState
            }

            SubscriptionType.Notification -> {
                notificationState = newState
            }
        }
    }

    private val subscriptionStateMap = mutableMapOf<SubscriptionType, Pair<String, LazyListState>>()

    fun getOrPutLazyColumnState(
        type: SubscriptionType,
        uuid: String
    ): LazyListState {
        if (subscriptionStateMap.get(type)?.first == uuid) {
            return subscriptionStateMap[type]?.second!!
        }
        val state = LazyListState()
        subscriptionStateMap[type] = uuid to state
        return state
    }
}

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val authService = AuthService()
    private val entriesApi = EntriesApi()
    private val subscriptionApi = SubscriptionsApi()
    private val scope = CoroutineScope(Dispatchers.Main)

    private var loadSubscriptionDeffer: Deferred<Unit>? = null

    private var subscriptionsListLoadState: LoadState<SubscriptionsResponse>? = null

    private val homeReqMap: ConcurrentMap<SubscriptionType, List<Deferred<*>>> = ConcurrentMap()

    sealed class Action {
        data object Initialize : Action()

        data class Login(val provider: String) : Action()

        data class LoadHome(
            val subscriptionType: SubscriptionType,
            val append: Boolean = false,
            val retryCount: Int = 0,
            val isRefresh: Boolean = false
        ) : Action()
    }

    val mainState = MainState(loginState = if (getSessionData()?.isExpired() == false) {
        LoginState.Success(false, getSessionToken()!!)
    } else {
        LoginState.Idle
    })

    fun processAction(action: Action) {
        when (action) {
            is Action.Login -> {
                scope.launch {
                    login(action.provider)
                }
            }

            Action.Initialize -> {
                scope.launch {
                    initialize()
                }
            }

            is Action.LoadHome -> {
                scope.launch {
                    loadHome(action)
                }
            }
        }
    }

    private suspend fun loadHome(action: Action.LoadHome) = withContext(Dispatchers.IO) {
        // load all subscription from server
        val subscriptionType = action.subscriptionType
        val append = action.append
        val retryCount = action.retryCount
        if (action.isRefresh.not() && action.append.not() && mainState.getViewState(subscriptionType) is LoadState.Success) {
            // direct return if not refresh and not append
            FLog.d(TAG, "load home: $subscriptionType, append: $append, not refresh and not append")
            return@withContext
        }
        updateMainState {
            updateSubscriptionState(subscriptionType, LoadState.Loading(action.isRefresh, retryCount, false))
        }
        FLog.d(TAG, "load home: $subscriptionType, append: $append")
        val currentSubscriptions = (mainState.getViewState(subscriptionType) as? LoadState.Success)?.data?.subscriptionEntriesMap?.keys ?: emptyList()
        val subscription = if (action.isRefresh || currentSubscriptions.isEmpty()) {
            // 刷新时，重新加载订阅列表
            val subscription = loadSubscription(view = subscriptionType.view)
            if (subscription is LoadState.Success) {
                subscription.data.data
            } else {
                emptyList()
            }
        } else {
            // 使用当前订阅列表
            currentSubscriptions
        }
        if (subscriptionsListLoadState is LoadState.Error) {
            updateMainState {
                updateSubscriptionState(
                    subscriptionType, LoadState.Error(
                        (subscriptionsListLoadState as LoadState.Error).throwable
                    )
                )
            }
            return@withContext
        }
        FLog.d(TAG, "load home: $subscriptionType, append: $append, retryCount: $retryCount")
        homeReqMap[subscriptionType]?.forEach { it.cancel() }
        homeReqMap[subscriptionType] = emptyList()
        // 并发获取每个列表的数据
        subscription.map {
            async {
                try {
                    val entriesState = LoadState.Loading<PostEntriesResponse>(isRefresh = action.isRefresh)
                    var loadState = mainState.getViewState(subscriptionType)
                    var newMap = ConcurrentMap<SubscriptionsResponse.Subscription, LoadState<PostEntriesResponse>>().apply {
                        putAll(loadState.data?.subscriptionEntriesMap ?: emptyMap())
                        put(it, entriesState)
                    }
                    FLog.d(TAG, "load home: $subscriptionType, append: $append, mapSize: ${newMap.size}")
                    val result = when (subscriptionType) {
                        SubscriptionType.Article, SubscriptionType.Notification -> {
                            LoadState.Success(entriesApi.postListEntries(listId = it.feedId, view = it.view))
                        }

                        SubscriptionType.SocialMedia, SubscriptionType.Image, SubscriptionType.Audio, SubscriptionType.Video -> {
                            LoadState.Success(entriesApi.postFeedEntries(feedId = it.feedId, view = it.view))
                        }
                    }
                    result.data.data?.forEach {
                        val icon = getUrlIcon(it.entries.url ?: "")
                        it.entries.icon = icon
                    }
                    loadState = mainState.getViewState(subscriptionType)
                    newMap = ConcurrentMap<SubscriptionsResponse.Subscription, LoadState<PostEntriesResponse>>().apply {
                        putAll(loadState.data?.subscriptionEntriesMap ?: emptyMap())
                        put(it, result)
                    }
                    FLog.d(TAG, "load home success: $subscriptionType, append: $append, mapSize: ${newMap.size}, result.size=${result.data.data?.size}")
                    updateMainState {
                        updateSubscriptionState(
                            subscriptionType,
                            LoadState.Success(
                                SubscriptionWithEntries(newMap),
                                append
                            )
                        )
                    }
                } catch (e: Exception) {
                    FLog.e(TAG, "load home error: ${e.message}")
                    val loadState = mainState.getViewState(subscriptionType)
                    val newMap = ConcurrentMap<SubscriptionsResponse.Subscription, LoadState<PostEntriesResponse>>().apply {
                        putAll(loadState.data?.subscriptionEntriesMap ?: emptyMap())
                        put(it, LoadState.Error(e))
                    }
                    if (loadState is LoadState.Success) {
                        updateMainState {
                            updateSubscriptionState(
                                subscriptionType,
                                loadState.copy(data = SubscriptionWithEntries(newMap))
                            )
                        }
                    } else {
                        updateMainState {
                            updateSubscriptionState(subscriptionType, LoadState.Error(e))
                        }
                    }
                }
            }
        }.let { deffer ->
            homeReqMap[subscriptionType] = deffer
        }
    }

    private suspend fun initialize() = withContext(Dispatchers.IO) {
        FLog.i(TAG, "initialize")
        if (getSessionData()?.isExpired() == false) {
            NetworkManager.setSessionToken(getSessionToken()!!)
            loadSubscriptionDeffer = async {
                loadSubscription()
            }
            updateMainState {
                loginState = LoginState.Success(false, getSessionToken()!!)
            }
        } else {
            updateMainState {
                loginState = LoginState.Idle
            }
        }
    }

    private suspend fun login(provider: String) = withContext(Dispatchers.IO) {
        if (getSessionData()?.isExpired() == false) {
            // 通知 UI 登录成功
            NetworkManager.setSessionToken(getSessionToken()!!)
            updateMainState {
                loginState = LoginState.Success(false, getSessionToken()!!)
            }
            return@withContext
        }
        updateMainState {
            loginState = LoginState.Loading(provider)
        }
        FLog.v(TAG, "login with provider: $provider")
        val token = kotlin.runCatching {
            suspendCancellableCoroutine<String> {
                callbackFlow<String> {
                    FLog.w(TAG, "login canceled")
                    it.resumeWith(Result.failure(Throwable("login canceled")))
                }
                startOAuth(provider, object : OAuthCallback {
                    override fun onSuccess(token: String) {
                        FLog.i(TAG, "login success, token: $token")
                        it.resumeWith(Result.success(token))
                    }

                    override fun onError(errorType: OAuthError, message: String) {
                        // 处理错误
                        FLog.e(TAG, "login error: ${errorType}, message: $message")
                        when (errorType) {
                            OAuthError.UserCancel -> {
                                FLog.w(TAG, "login canceled")
                                it.resumeWith(Result.failure(Throwable("login canceled")))
                            }

                            else -> {
                                FLog.e(TAG, "login error: $errorType, message: $message")
                                it.resumeWith(Result.failure(Throwable("login error: $errorType, message: $message")))
                            }
                        }
                    }

                    override fun onCancel() {
                        // 处理取消
                        FLog.w(TAG, "login cancel")
                        it.resumeWith(Result.failure(Throwable("login canceled")))
                    }
                })
            }
        }.getOrElse {
            updateMainState {
                FLog.d(TAG, "login OAuth error: ${it.message}")
                loginState = LoginState.Error(it, provider)
            }
            return@withContext
        }
        FLog.i(TAG, "login OAuth success, token: ${token.take(3)}")
        // 保存 token
        saveSessionToken(token)

        // 获取 session
        try {
            val session = authService.getSession(token)
            saveSessionData(session)
            // 通知 UI 登录成功
            updateMainState {
                loginState = LoginState.Success(true, token)
            }
        } catch (e: Exception) {
            // 处理错误
            FLog.e(TAG, "get session error: ${e.message}")
            updateMainState {
                loginState = LoginState.Error(e, provider)
            }
        }
    }

    private suspend fun loadSubscription(
        view: Int? = null
    ): LoadState<SubscriptionsResponse> = withContext(Dispatchers.IO) {
        if (loadSubscriptionDeffer?.isActive == true) {
            return@withContext LoadState.Loading(false)
        }
        FLog.d(TAG, "load subscription")
        subscriptionsListLoadState = LoadState.Loading(false)
        val subscriptions = kotlin.runCatching { subscriptionApi.getSubscriptions(view) }
            .getOrElse {
                FLog.e(TAG, "load subscription error: ${it.message}")
                subscriptionsListLoadState = LoadState.Error(it)
                return@withContext LoadState.Error(it)
            }
        FLog.d(TAG, "load subscription success ${subscriptions.data.size}")
        subscriptions.data.forEach {
            feedIconCache.put(
                it.feedId,
                it.cover
            )
        }
        subscriptionsListLoadState = LoadState.Success(subscriptions)
        return@withContext LoadState.Success(subscriptions)
    }

    private val urlCache by lazy {
        LruCache<String, Pair<String, String>>(10)
    }
    private val feedIconCache by lazy {
        LruCache<String, String>(10)
    }

    private suspend fun getUrlIcon(url: String): Pair<String, String> =
        withContext(Dispatchers.IO) {
            // 1. 首先尝试从 URL 中提取主机名（host）
            val host = try {
                Url(url).host
            } catch (e: Exception) {
                url
            }
            // 1.1 如果缓存中有，直接返回
            urlCache[host]?.let {
                return@withContext it
            }
            // 2. 获取纯域名（例如：从 www.example.com 获取 example）
            val pureDomain = host.split(".").let {
                if (it.size > 2) {
                    it[it.size - 2]
                } else {
                    it.first()
                }
            }

            // 3. 生成备用图标 URL（使用域名前两个字母作为文本）
            val fallbackUrl =
                "https://avatar.vercel.sh/$pureDomain.svg?text=${pureDomain.take(2).uppercase()}"

            // 4. 如果 host 与原始 url 相同（说明 URL 解析失败），使用备用图标
            // 否则使用 unavatar.webp.se 服务获取图标
            val src = if (host == url) {
                fallbackUrl
            } else {
                "https://unavatar.webp.se/$host?fallback=$fallbackUrl"
            }

            // 5. 主图标 URL 和备用图标 URL 的配对写入缓存
            urlCache.put(host, src to fallbackUrl)
            return@withContext src to fallbackUrl
        }

    private suspend fun updateMainState(reducer: MainState.() -> Unit) {
        coroutineScope {
            withContext(Dispatchers.Main) {
                mainState.reducer()
            }
        }
    }
}