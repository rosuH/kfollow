package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Subscription: ImageVector
    get() {
        if (_Subscription != null) {
            return _Subscription!!
        }
        _Subscription = ImageVector.Builder(
            name = "Subscription",
            defaultWidth = 18.996.dp,
            defaultHeight = 18.773.dp,
            viewportWidth = 18.996f,
            viewportHeight = 18.773f
        ).apply {
            path(fill = SolidColor(Color(0xFF6E6F76))) {
                moveTo(1.799f, 18.773f)
                curveTo(1.299f, 18.773f, 0.873f, 18.6f, 0.521f, 18.252f)
                curveTo(0.174f, 17.9f, 0f, 17.475f, 0f, 16.975f)
                lineTo(0f, 7.576f)
                curveTo(0f, 7.076f, 0.174f, 6.652f, 0.521f, 6.305f)
                curveTo(0.873f, 5.953f, 1.299f, 5.777f, 1.799f, 5.777f)
                lineTo(17.197f, 5.777f)
                curveTo(17.697f, 5.777f, 18.121f, 5.953f, 18.469f, 6.305f)
                curveTo(18.82f, 6.652f, 18.996f, 7.076f, 18.996f, 7.576f)
                lineTo(18.996f, 16.975f)
                curveTo(18.996f, 17.475f, 18.82f, 17.9f, 18.469f, 18.252f)
                curveTo(18.121f, 18.6f, 17.697f, 18.773f, 17.197f, 18.773f)
                lineTo(1.799f, 18.773f)
                close()
                moveTo(7.746f, 15.826f)
                lineTo(13.049f, 12.275f)
                lineTo(7.746f, 8.725f)
                lineTo(7.746f, 15.826f)
                close()
                moveTo(1.746f, 4.4f)
                lineTo(1.746f, 2.9f)
                lineTo(17.25f, 2.9f)
                lineTo(17.25f, 4.4f)
                lineTo(1.746f, 4.4f)
                close()
                moveTo(4.746f, 1.5f)
                lineTo(4.746f, 0f)
                lineTo(14.25f, 0f)
                lineTo(14.25f, 1.5f)
                lineTo(4.746f, 1.5f)
                close()
            }
        }.build()

        return _Subscription!!
    }

@Suppress("ObjectPropertyName")
private var _Subscription: ImageVector? = null
