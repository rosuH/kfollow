package me.rosuh.Icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Setting: ImageVector
    get() {
        if (_Setting != null) {
            return _Setting!!
        }
        _Setting = ImageVector.Builder(
            name = "Setting",
            defaultWidth = 18.797.dp,
            defaultHeight = 18.996.dp,
            viewportWidth = 18.797f,
            viewportHeight = 18.996f
        ).apply {
            path(fill = SolidColor(Color(0xFF6E6F76))) {
                moveTo(7.096f, 18.996f)
                lineTo(6.697f, 15.949f)
                curveTo(6.432f, 15.863f, 6.16f, 15.738f, 5.883f, 15.574f)
                curveTo(5.609f, 15.406f, 5.355f, 15.231f, 5.121f, 15.047f)
                lineTo(2.297f, 16.248f)
                lineTo(0f, 12.246f)
                lineTo(2.449f, 10.4f)
                curveTo(2.414f, 10.248f, 2.393f, 10.098f, 2.385f, 9.949f)
                curveTo(2.377f, 9.797f, 2.373f, 9.646f, 2.373f, 9.498f)
                curveTo(2.373f, 9.365f, 2.377f, 9.225f, 2.385f, 9.076f)
                curveTo(2.393f, 8.924f, 2.414f, 8.764f, 2.449f, 8.596f)
                lineTo(0f, 6.75f)
                lineTo(2.297f, 2.771f)
                lineTo(5.121f, 3.949f)
                curveTo(5.355f, 3.766f, 5.609f, 3.596f, 5.883f, 3.439f)
                curveTo(6.16f, 3.279f, 6.432f, 3.148f, 6.697f, 3.047f)
                lineTo(7.096f, 0f)
                lineTo(11.701f, 0f)
                lineTo(12.1f, 3.047f)
                curveTo(12.4f, 3.164f, 12.67f, 3.295f, 12.908f, 3.439f)
                curveTo(13.15f, 3.58f, 13.396f, 3.75f, 13.646f, 3.949f)
                lineTo(16.5f, 2.771f)
                lineTo(18.797f, 6.75f)
                lineTo(16.324f, 8.625f)
                curveTo(16.355f, 8.789f, 16.371f, 8.939f, 16.371f, 9.076f)
                lineTo(16.371f, 9.498f)
                curveTo(16.371f, 9.631f, 16.367f, 9.77f, 16.359f, 9.914f)
                curveTo(16.352f, 10.055f, 16.332f, 10.217f, 16.301f, 10.4f)
                lineTo(18.75f, 12.246f)
                lineTo(16.447f, 16.248f)
                lineTo(13.646f, 15.047f)
                curveTo(13.396f, 15.246f, 13.143f, 15.422f, 12.885f, 15.574f)
                curveTo(12.627f, 15.723f, 12.365f, 15.848f, 12.1f, 15.949f)
                lineTo(11.701f, 18.996f)
                lineTo(7.096f, 18.996f)
                close()
                moveTo(9.398f, 12.498f)
                curveTo(10.231f, 12.498f, 10.938f, 12.207f, 11.519f, 11.625f)
                curveTo(12.106f, 11.039f, 12.398f, 10.33f, 12.398f, 9.498f)
                curveTo(12.398f, 8.666f, 12.106f, 7.959f, 11.519f, 7.377f)
                curveTo(10.938f, 6.791f, 10.231f, 6.498f, 9.398f, 6.498f)
                curveTo(8.566f, 6.498f, 7.857f, 6.791f, 7.271f, 7.377f)
                curveTo(6.689f, 7.959f, 6.398f, 8.666f, 6.398f, 9.498f)
                curveTo(6.398f, 10.33f, 6.689f, 11.039f, 7.271f, 11.625f)
                curveTo(7.857f, 12.207f, 8.566f, 12.498f, 9.398f, 12.498f)
                close()
            }
        }.build()

        return _Setting!!
    }

@Suppress("ObjectPropertyName")
private var _Setting: ImageVector? = null
