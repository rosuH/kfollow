package me.rosuh

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.rosuh.data.OAuthCallback

class OAuthActivity : ComponentActivity() {
    companion object {
        var callback: OAuthCallback? = null
    }

    private var tokenState: Pair<String, Boolean> = "" to false

    private val innerCallback = object : OAuthCallback {
        override fun onSuccess(token: String) {
            if (token == tokenState.first && tokenState.second) {
                return
            }
            tokenState = token to true
            callback?.onSuccess(token)
            finish()
        }

        override fun onError(errorType: me.rosuh.data.OAuthError, message: String) {
            callback?.onError(errorType, message)
            finish()
        }

        override fun onCancel() {
            callback?.onCancel()
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = intent.getStringExtra("provider") ?: run {
            finish()
            return
        }
        setContent {
            OAuthScreen(
                provider = provider,
                callback = callback ?: return@setContent,
                onDismiss = { finish() }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }
}