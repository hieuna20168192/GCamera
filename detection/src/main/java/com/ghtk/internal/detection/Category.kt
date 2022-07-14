package com.ghtk.internal.detection

data class Category(
    val label: String = "",
    val score: Int = 0
) {
    val noWear: Boolean
        get() = label.equals("normal", ignoreCase = true)
}
