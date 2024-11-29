package me.rosuh

import platform.WebKit.*
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*
import kotlinx.cinterop.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError
import me.rosuh.data.SessionResponse
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.darwin.NSObject
import platform.objc.sel_registerName

private val defaults = NSUserDefaults.standardUserDefaults

actual fun saveSessionToken(token: String) {
    defaults.setObject(token, "session_token")
}

actual fun getSessionToken(): String? {
    return defaults.stringForKey("session_token")
}

actual fun saveSessionData(data: SessionResponse) {
    defaults.setObject(Json.encodeToString(data), "session_data")
}

actual fun getSessionData(): SessionResponse? {
    return defaults.stringForKey("session_data")?.let {
        Json.decodeFromString(it)
    }
}

actual fun clearData() {
    defaults.removeObjectForKey("session_token")
    defaults.removeObjectForKey("session_data")
}

private var webAuthSession: ASWebAuthenticationSession? = null

@ExperimentalForeignApi
actual fun startOAuth(provider: String, callback: OAuthCallback) {
    val url = NSURL.URLWithString("https://app.follow.is/login?provider=$provider") ?: run {
        callback.onError(OAuthError.InvalidURL, "Invalid URL")
        return
    }
    webAuthSession = ASWebAuthenticationSession(
        uRL = url,
        callbackURLScheme = "follow"
    ) { callbackURL: NSURL?, error: NSError? ->
        when {
            error != null -> {
                when (error.code) {
                    1L -> callback.onCancel()
                    else -> callback.onError(OAuthError.Unknown, error.localizedDescription)
                }
            }

            callbackURL != null -> {
                val components = NSURLComponents(uRL = callbackURL, resolvingAgainstBaseURL = false)
                val queryItems = components.queryItems as? List<NSURLQueryItem>
                val token = queryItems?.firstOrNull {
                    it.name == "token"
                }?.value

                if (token != null) {
                    callback.onSuccess(token)
                } else {
                    callback.onError(OAuthError.NoToken, "No token received")
                }
            }

            else -> callback.onCancel()
        }
    }

    val contextProvider =
        object : NSObject(), ASWebAuthenticationPresentationContextProvidingProtocol {
            override fun presentationAnchorForWebAuthenticationSession(
                session: ASWebAuthenticationSession
            ): UIWindow {
                return UIApplication.sharedApplication.keyWindow!!
            }

            override fun description(): String {
                return "ASWebAuthenticationPresentationContextProvider"
            }

            override fun hash(): ULong {
                return hashCode().toULong()
            }

            override fun isEqual(other: Any?): Boolean {
                return this === other
            }
        }

    webAuthSession?.presentationContextProvider = contextProvider
    webAuthSession?.prefersEphemeralWebBrowserSession = false
    webAuthSession?.start()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun openWebPage(url: String, callback: (WebPageState) -> Unit) {
    val configuration = WKWebViewConfiguration()

    val viewController = object : UIViewController(nibName = null, bundle = null), WKNavigationDelegateProtocol {
        private lateinit var webView: WKWebView
        private lateinit var toolBar: UIToolbar
        private lateinit var backButton: UIBarButtonItem
        private lateinit var forwardButton: UIBarButtonItem
        private lateinit var urlButton: UIButton

        override fun loadView() {
            view = UIView()
            view.backgroundColor = UIColor.systemBackgroundColor

            // 创建 webView
            val rect = CGRectMake(0.0, 0.0, 0.0, 0.0)
            webView = WKWebView(frame = rect, configuration = configuration)
            webView.setNavigationDelegate(this)
            webView.setTranslatesAutoresizingMaskIntoConstraints(false)
            view.addSubview(webView)

            // 创建底部工具栏
            toolBar = UIToolbar()
            toolBar.setTranslatesAutoresizingMaskIntoConstraints(false)
            view.addSubview(toolBar)

            // 设置约束
            NSLayoutConstraint.activateConstraints(listOf(
                webView.topAnchor.constraintEqualToAnchor(view.safeAreaLayoutGuide.topAnchor),
                webView.leadingAnchor.constraintEqualToAnchor(view.leadingAnchor),
                webView.trailingAnchor.constraintEqualToAnchor(view.trailingAnchor),
                webView.bottomAnchor.constraintEqualToAnchor(toolBar.topAnchor),

                toolBar.leadingAnchor.constraintEqualToAnchor(view.leadingAnchor),
                toolBar.trailingAnchor.constraintEqualToAnchor(view.trailingAnchor),
                toolBar.bottomAnchor.constraintEqualToAnchor(view.safeAreaLayoutGuide.bottomAnchor)
            ))
        }

        override fun viewDidLoad() {
            super.viewDidLoad()

            // 设置顶部导航栏
            val closeButton = UIBarButtonItem(
                image = UIImage.systemImageNamed("xmark"),
                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                target = this,
                action = sel_registerName("closeButtonTapped")
            )
            val it = this
            // 创建中间的 URL 按钮
            urlButton = UIButton.buttonWithType(UIButtonTypeSystem).apply {
                setTitle(NSURL.URLWithString(url)?.host ?: url, UIControlStateNormal)
                setTranslatesAutoresizingMaskIntoConstraints(false)
                addTarget(
                    target = it,
                    action = sel_registerName("copyUrlButtonTapped"),
                    forControlEvents = UIControlEventTouchUpInside
                )
            }

            val refreshButton = UIBarButtonItem(
                image = UIImage.systemImageNamed("arrow.clockwise"),
                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                target = webView,
                action = sel_registerName("reload")
            )

            navigationItem.setLeftBarButtonItem(closeButton)
            navigationItem.titleView = urlButton
            navigationItem.setRightBarButtonItem(refreshButton)

            // 设置底部工具栏
            backButton = UIBarButtonItem(
                image = UIImage.systemImageNamed("chevron.backward"),
                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                target = webView,
                action = sel_registerName("goBack")
            )

            forwardButton = UIBarButtonItem(
                image = UIImage.systemImageNamed("chevron.forward"),
                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                target = webView,
                action = sel_registerName("goForward")
            )

            val shareButton = UIBarButtonItem(
                image = UIImage.systemImageNamed("square.and.arrow.up"),
                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                target = this,
                action = sel_registerName("shareButtonTapped")
            )

            val safariButton = UIBarButtonItem(
                image = UIImage.systemImageNamed("safari"),
                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                target = this,
                action = sel_registerName("openInSafariTapped")
            )

            val flexSpace = UIBarButtonItem(
                barButtonSystemItem = UIBarButtonSystemItem.UIBarButtonSystemItemFlexibleSpace,
                target = null,
                action = null
            )

            // 创建顶部间距的空白项
            val topSpaceItem = UIBarButtonItem(
                customView = UIView().apply {
                    setTranslatesAutoresizingMaskIntoConstraints(false)
                    heightAnchor.constraintEqualToConstant(8.0).setActive(true)
                }
            )
            
            toolBar.setItems(listOf(
                topSpaceItem,
                backButton, flexSpace,
                forwardButton, flexSpace,
                shareButton, flexSpace,
                safariButton
            ), animated = false)
            
            // 设置工具栏内边距
            toolBar.setLayoutMargins(UIEdgeInsetsMake(8.0, 0.0, 0.0, 0.0))

            // 加载 URL
            webView.loadRequest(NSURLRequest.requestWithURL(NSURL.URLWithString(url)!!))
        }

        // WKNavigationDelegate
        override fun webView(
            webView: WKWebView,
            didFinishNavigation: WKNavigation?
        ) {
            backButton.setEnabled(webView.canGoBack)
            forwardButton.setEnabled(webView.canGoForward)
            urlButton.setTitle(webView.URL?.host ?: url, UIControlStateNormal)
        }

        @ObjCAction
        fun closeButtonTapped() {
            dismissViewControllerAnimated(flag = true, completion = null)
            callback(WebPageState.Finished(url, "0", "User closed the page"))
        }

        @ObjCAction
        fun copyUrlButtonTapped() {
            UIPasteboard.generalPasteboard.string = webView.URL?.absoluteString ?: url
            
            // 触觉反馈
            val generator = UINotificationFeedbackGenerator()
            generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
            
            // 显示提示
            val alert = UIAlertController.alertControllerWithTitle(
                title = null,
                message = "已复制链接",
                preferredStyle = UIAlertControllerStyleAlert
            )
            presentViewController(alert, animated = true) {
                // 0.8秒后自动消失
                NSThread.sleepForTimeInterval(0.8)
                alert.dismissViewControllerAnimated(true, completion = null)
            }
        }

        @ObjCAction
        fun shareButtonTapped() {
            val items = listOf(webView.URL ?: NSURL.URLWithString(url)!!)
            val activityVC = UIActivityViewController(
                activityItems = items,
                applicationActivities = null
            )
            presentViewController(activityVC, animated = true, completion = null)
        }

        @ObjCAction
        fun openInSafariTapped() {
            val nsurl = webView.URL ?: NSURL.URLWithString(url)!!
            UIApplication.sharedApplication.openURL(
                url = nsurl,
                options = mapOf<Any?, Any?>(),
                completionHandler = null
            )
        }
    }

    val navigationController = UINavigationController(rootViewController = viewController)
    UIApplication.sharedApplication.keyWindow?.rootViewController?.
        presentViewController(navigationController, animated = true, completion = null)
}