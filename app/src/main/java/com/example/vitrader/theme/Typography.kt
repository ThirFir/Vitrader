package com.example.vitrader.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.vitrader.R

val spoqa = FontFamily(
    Font(R.font.spoqa_han_sans_neo_bold, FontWeight.Bold),      // W700
    Font(R.font.spoqa_han_sans_neo_bold, FontWeight.W600),
    Font(R.font.spoqa_han_sans_neo_medium, FontWeight.Medium),  // W500
    Font(R.font.spoqa_han_sans_neo_regular, FontWeight.Normal), // W400
    Font(R.font.spoqa_han_sans_neo_light, FontWeight.Light),    // W300
    Font(R.font.spoqa_han_sans_neo_thin, FontWeight.Thin)       // W100
)

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular),
    Font(R.font.montserrat_medium, FontWeight.W500),
    Font(R.font.montserrat_semibold, FontWeight.W600)
)

val Domine = FontFamily(
    Font(R.font.domine_regular),
    Font(R.font.domine_bold, FontWeight.Bold)
)