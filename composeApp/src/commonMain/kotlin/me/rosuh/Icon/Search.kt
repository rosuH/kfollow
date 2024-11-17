package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Search: ImageVector
    get() {
        if (_Search != null) {
            return _Search!!
        }
        _Search = ImageVector.Builder(
            name = "Search",
            defaultWidth = 17.203.dp,
            defaultHeight = 17.197.dp,
            viewportWidth = 17.203f,
            viewportHeight = 17.197f
        ).apply {
            path(fill = SolidColor(Color(0xFF6E6F76))) {
                moveTo(16.154f, 17.197f)
                lineTo(9.85f, 10.928f)
                curveTo(9.35f, 11.342f, 8.775f, 11.666f, 8.127f, 11.9f)
                curveTo(7.479f, 12.135f, 6.813f, 12.252f, 6.129f, 12.252f)
                curveTo(4.41f, 12.252f, 2.959f, 11.66f, 1.775f, 10.477f)
                curveTo(0.592f, 9.293f, 0f, 7.842f, 0f, 6.123f)
                curveTo(0f, 4.424f, 0.592f, 2.979f, 1.775f, 1.787f)
                curveTo(2.959f, 0.596f, 4.41f, 0f, 6.129f, 0f)
                curveTo(7.828f, 0f, 9.27f, 0.592f, 10.453f, 1.775f)
                curveTo(11.637f, 2.959f, 12.229f, 4.408f, 12.229f, 6.123f)
                curveTo(12.229f, 6.842f, 12.111f, 7.527f, 11.877f, 8.18f)
                curveTo(11.643f, 8.828f, 11.326f, 9.393f, 10.928f, 9.873f)
                lineTo(17.203f, 16.148f)
                lineTo(16.154f, 17.197f)
                close()
                moveTo(6.129f, 10.752f)
                curveTo(7.41f, 10.752f, 8.496f, 10.303f, 9.387f, 9.404f)
                curveTo(10.281f, 8.502f, 10.729f, 7.408f, 10.729f, 6.123f)
                curveTo(10.729f, 4.842f, 10.281f, 3.752f, 9.387f, 2.854f)
                curveTo(8.496f, 1.951f, 7.41f, 1.5f, 6.129f, 1.5f)
                curveTo(4.828f, 1.5f, 3.73f, 1.951f, 2.836f, 2.854f)
                curveTo(1.945f, 3.752f, 1.5f, 4.842f, 1.5f, 6.123f)
                curveTo(1.5f, 7.408f, 1.945f, 8.502f, 2.836f, 9.404f)
                curveTo(3.73f, 10.303f, 4.828f, 10.752f, 6.129f, 10.752f)
                close()
            }
        }.build()

        return _Search!!
    }

@Suppress("ObjectPropertyName")
private var _Search: ImageVector? = null
