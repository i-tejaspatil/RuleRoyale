package com.ruleroyale
interface Platform {
    val name: String
}
expect fun getPlatform(): Platform
