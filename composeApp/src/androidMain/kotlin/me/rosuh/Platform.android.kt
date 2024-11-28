package me.rosuh

import android.content.res.Configuration
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

/**
 * open custom tabs for url
 * String url = "https://developers.android.com";
 * CustomTabsIntent intent = new CustomTabsIntent.Builder()
 *         .build();
 * intent.launchUrl(MainActivity.this, Uri.parse(url));
 */
actual fun openWebPage(url: String, callback: (WebPageState) -> Unit) {
    MainActivity.mainActivity?.let {
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(it, url.toUri())
    }
}