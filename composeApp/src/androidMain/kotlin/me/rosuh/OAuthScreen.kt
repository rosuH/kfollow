package me.rosuh

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OAuthScreen(
    provider: String, callback: OAuthCallback, onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf("https://app.follow.is/login?provider=$provider") }
    val host by remember {
        derivedStateOf { Uri.parse(url).host ?: "Unknown" }
    }
    var isLoading by remember { mutableStateOf(true) }

    BackHandler {
        onDismiss()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = host) }, navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }, actions = {
            IconButton(onClick = { url = url }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 加载指示器
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // WebView
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
                WebViewCache.getWebView(context = context).apply {
                    applyConfig()
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?, request: WebResourceRequest?
                        ): Boolean {
                            val uri = request?.url ?: return false
                            return when {
                                uri.scheme == "folo" -> {
                                    val token = uri.getQueryParameter("token")
                                    if (token != null) {
                                        callback.onSuccess(token)
                                    } else {
                                        callback.onError(
                                            OAuthError.NoToken, "No token received"
                                        )
                                    }
                                    onDismiss()
                                    true
                                }

                                else -> false
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }
                    }
                }
            }, update = { webView ->
                webView.loadUrl(url)
            })
        }

    }

}