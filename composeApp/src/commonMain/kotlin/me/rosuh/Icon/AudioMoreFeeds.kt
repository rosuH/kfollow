package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AudioMoreFeeds: ImageVector
    get() {
        if (_AudioMoreFeeds != null) {
            return _AudioMoreFeeds!!
        }
        _AudioMoreFeeds = ImageVector.Builder(
            name = "AudioMoreFeeds",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 12.5f)
                curveTo(20.276f, 12.5f, 20.5f, 12.276f, 20.5f, 12f)
                curveTo(20.5f, 11.724f, 20.276f, 11.5f, 20f, 11.5f)
                curveTo(19.724f, 11.5f, 19.5f, 11.724f, 19.5f, 12f)
                curveTo(19.5f, 12.276f, 19.724f, 12.5f, 20f, 12.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 12.5f)
                curveTo(12.276f, 12.5f, 12.5f, 12.276f, 12.5f, 12f)
                curveTo(12.5f, 11.724f, 12.276f, 11.5f, 12f, 11.5f)
                curveTo(11.724f, 11.5f, 11.5f, 11.724f, 11.5f, 12f)
                curveTo(11.5f, 12.276f, 11.724f, 12.5f, 12f, 12.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 12.5f)
                curveTo(4.276f, 12.5f, 4.5f, 12.276f, 4.5f, 12f)
                curveTo(4.5f, 11.724f, 4.276f, 11.5f, 4f, 11.5f)
                curveTo(3.724f, 11.5f, 3.5f, 11.724f, 3.5f, 12f)
                curveTo(3.5f, 12.276f, 3.724f, 12.5f, 4f, 12.5f)
                close()
            }
        }.build()

        return _AudioMoreFeeds!!
    }

@Suppress("ObjectPropertyName")
private var _AudioMoreFeeds: ImageVector? = null
