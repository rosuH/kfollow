package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Play: ImageVector
    get() {
        if (_Play != null) {
            return _Play!!
        }
        _Play = ImageVector.Builder(
            name = "Play",
            defaultWidth = 100.dp,
            defaultHeight = 100.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(8f, 17.175f)
                verticalLineTo(6.825f)
                quadToRelative(0f, -0.425f, 0.3f, -0.713f)
                reflectiveQuadToRelative(0.7f, -0.287f)
                quadToRelative(0.125f, 0f, 0.263f, 0.037f)
                reflectiveQuadToRelative(0.262f, 0.113f)
                lineToRelative(8.15f, 5.175f)
                quadToRelative(0.225f, 0.15f, 0.338f, 0.375f)
                reflectiveQuadToRelative(0.112f, 0.475f)
                reflectiveQuadToRelative(-0.112f, 0.475f)
                reflectiveQuadToRelative(-0.338f, 0.375f)
                lineToRelative(-8.15f, 5.175f)
                quadToRelative(-0.125f, 0.075f, -0.262f, 0.113f)
                reflectiveQuadTo(9f, 18.175f)
                quadToRelative(-0.4f, 0f, -0.7f, -0.288f)
                reflectiveQuadToRelative(-0.3f, -0.712f)
            }
        }.build()

        return _Play!!
    }

@Suppress("ObjectPropertyName")
private var _Play: ImageVector? = null
