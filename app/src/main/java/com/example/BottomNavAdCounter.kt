package com.example

object BottomNavAdCounter {
    private val counts = mutableMapOf<String, Int>()

    fun registerClick(
        tabKey: String,
        onRewardedAd: () -> Unit
    ) {
        val next = (counts[tabKey] ?: 0) + 1

        if (next >= 4) {
            counts[tabKey] = 0
            onRewardedAd()
        } else {
            counts[tabKey] = next
        }
    }

    fun reset() {
        counts.clear()
    }
}
