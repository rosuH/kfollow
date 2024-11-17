package me.rosuh.data.api

import io.ktor.resources.*
import kotlinx.serialization.Serializable


@Resource("/subscriptions")
@Serializable
class Subscriptions(
    val view: Int? = null
)

/**
 * 当用户选择不同的内容类型时，会通过 view 参数传递对应的数字标识：
 *
 * 0: 文章
 * 1: 社交媒体
 * 2: 图片
 * 3: 视频
 * 4: 音频
 * 5: 通知
 */
sealed class SubscriptionType {
    abstract val view: Int
    abstract val title: String

    data object Article : SubscriptionType() {
        override val view: Int = 0
        override val title: String = "文章"
    }

    data object SocialMedia : SubscriptionType() {
        override val view: Int = 1
        override val title: String = "社交媒体"
    }

    data object Image : SubscriptionType() {
        override val view: Int = 2
        override val title: String = "图片"
    }

    data object Video : SubscriptionType() {
        override val view: Int = 3
        override val title: String = "视频"
    }

    data object Audio : SubscriptionType() {
        override val view: Int = 4
        override val title: String = "音频"
    }

    data object Notification : SubscriptionType() {
        override val view: Int = 5
        override val title: String = "通知"
    }
}

val Int.subscriptionType: SubscriptionType
    get() {
        return when (this) {
            0 -> SubscriptionType.Article
            1 -> SubscriptionType.SocialMedia
            2 -> SubscriptionType.Image
            3 -> SubscriptionType.Video
            4 -> SubscriptionType.Audio
            5 -> SubscriptionType.Notification
            else -> SubscriptionType.Article
        }
    }

val subscriptionTypeList = listOf(
    SubscriptionType.Article,
    SubscriptionType.SocialMedia,
    SubscriptionType.Image,
    SubscriptionType.Video,
    SubscriptionType.Audio,
    SubscriptionType.Notification
)

val subscriptionTypeListTitle = subscriptionTypeList.map { it.title }