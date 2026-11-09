package org.example.app.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import app.composeui.generated.resources.Inter_Bold
import app.composeui.generated.resources.Inter_Medium
import app.composeui.generated.resources.Inter_Regular
import app.composeui.generated.resources.Inter_SemiBold
import app.composeui.generated.resources.Res

@Composable
fun InterFontFamily(): FontFamily = FontFamily(
    Font(Res.font.Inter_Regular, FontWeight.Normal),
    Font(Res.font.Inter_Medium, FontWeight.Medium),
    Font(Res.font.Inter_SemiBold, FontWeight.SemiBold),
    Font(Res.font.Inter_Bold, FontWeight.Bold),
)

@Composable
fun AppTypography(): Typography {
    val inter = InterFontFamily()
    return Typography(
        headlineLarge = TextStyle(fontFamily = inter, fontSize = 28.sp, fontWeight = FontWeight.Bold),
        headlineMedium = TextStyle(fontFamily = inter, fontSize = 22.sp, fontWeight = FontWeight.Bold),
        headlineSmall = TextStyle(fontFamily = inter, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
        titleLarge = TextStyle(fontFamily = inter, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = TextStyle(fontFamily = inter, fontSize = 15.sp, fontWeight = FontWeight.Medium),
        titleSmall = TextStyle(fontFamily = inter, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        bodyLarge = TextStyle(fontFamily = inter, fontSize = 15.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
        bodyMedium = TextStyle(fontFamily = inter, fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
        bodySmall = TextStyle(fontFamily = inter, fontSize = 13.sp, fontWeight = FontWeight.Normal),
        labelLarge = TextStyle(fontFamily = inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
        labelMedium = TextStyle(fontFamily = inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
        labelSmall = TextStyle(fontFamily = inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
    )
}
