package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Clock: ImageVector
    get() {
        if (_Clock != null) {
            return _Clock!!
        }
        _Clock = ImageVector.Builder(
            name = "Clock",
            defaultWidth = 100.dp,
            defaultHeight = 100.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(12f, 20f)
                curveToRelative(4.4f, 0f, 8f, -3.6f, 8f, -8f)
                reflectiveCurveToRelative(-3.6f, -8f, -8f, -8f)
                reflectiveCurveToRelative(-8f, 3.6f, -8f, 8f)
                reflectiveCurveToRelative(3.6f, 8f, 8f, 8f)
                moveToRelative(0f, -18f)
                curveToRelative(5.5f, 0f, 10f, 4.5f, 10f, 10f)
                reflectiveCurveToRelative(-4.5f, 10f, -10f, 10f)
                reflectiveCurveTo(2f, 17.5f, 2f, 12f)
                reflectiveCurveTo(6.5f, 2f, 12f, 2f)
                moveToRelative(5f, 9.5f)
                verticalLineTo(13f)
                horizontalLineToRelative(-6f)
                verticalLineTo(7f)
                horizontalLineToRelative(1.5f)
                verticalLineToRelative(4.5f)
                close()
            }
        }.build()

        return _Clock!!
    }

@Suppress("ObjectPropertyName")
private var _Clock: ImageVector? = null
