package me.rosuh

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
        var mainActivity: MainActivity? = null
        var callback: OAuthCallback? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            App()
        }
        
        // 处理从 OAuth 启动的情况
        intent?.let { handleIntent(it) }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.scheme == "follow") {
            val token = uri.getQueryParameter("token")
            if (token != null) {
                callback?.onSuccess(token)
            } else {
                callback?.onError(OAuthError.NoToken, "No token received")
            }
            callback = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity = null
        callback = null
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}