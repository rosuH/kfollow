package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Home: ImageVector
    get() {
        if (_Home != null) {
            return _Home!!
        }
        _Home = ImageVector.Builder(
            name = "Home",
            defaultWidth = 15.dp,
            defaultHeight = 16.904.dp,
            viewportWidth = 15f,
            viewportHeight = 16.904f
        ).apply {
            path(fill = SolidColor(Color(0xFF6E6F76))) {
                moveTo(0f, 16.904f)
                lineTo(0f, 5.654f)
                lineTo(7.5f, 0f)
                lineTo(15f, 5.654f)
                lineTo(15f, 16.904f)
                lineTo(9.398f, 16.904f)
                lineTo(9.398f, 10.201f)
                lineTo(5.602f, 10.201f)
                lineTo(5.602f, 16.904f)
                lineTo(0f, 16.904f)
                close()
            }
        }.build()

        return _Home!!
    }

@Suppress("ObjectPropertyName")
private var _Home: ImageVector? = null
