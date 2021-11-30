@file:Suppress("unused")

package pl.mrodkiewicz.imageeditor.ui

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.sp
import pl.mrodkiewicz.imageeditor.R


//workaround the problem of not respecting font weights
val latoThin = FontFamily(
    fonts = listOf(
            Font(
            R.font.lato_thin,
            FontWeight.W100,
            style = FontStyle.Normal
        )
    )
)
val latoLight = FontFamily(
    fonts = listOf(
            Font(
            R.font.lato_light,
            FontWeight.W300,
            style = FontStyle.Normal
        )
    )
)
val latoRegular = FontFamily(
    fonts = listOf(
        Font(
            R.font.lato_regular,
            FontWeight.W400,
            style = FontStyle.Normal
        )
    )
)
val latoBold = FontFamily(Font(R.font.lato_bold, FontWeight.W700, style = FontStyle.Normal))
val latoBlack =
        FontFamily(Font(R.font.lato_black, FontWeight.W900, style = FontStyle.Normal))
val roboto = FontFamily(Font(R.font.roboto_mono_light))

val actionBarTextStyle = TextStyle(
    fontFamily = latoLight,
    color = yellowPrimary,
    fontSize = 32.sp
)

// Set of Material typography styles to start with
val typography = Typography(
    body1 = TextStyle(
        fontFamily = latoBold,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        color = Color.White,
        fontFamily = latoRegular,
        fontSize = 22.sp
    ),
    subtitle1 = TextStyle(
        color = Color.White,
        fontFamily = latoLight,
        fontSize = 20.sp
    )
    /* Other default text styles to override
button = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.W500,
    fontSize = 14.sp
),
caption = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)
*/
)