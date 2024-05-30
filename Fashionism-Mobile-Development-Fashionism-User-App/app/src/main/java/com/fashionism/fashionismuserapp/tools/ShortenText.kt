package com.fashionism.fashionismuserapp.tools

fun shortenText(text: String, maxLength: Int = 20): String {
    if (text.length <= maxLength) {
        return text
    }

    val shortenedText = text.substring(0, maxLength)
    return "$shortenedText..."
}