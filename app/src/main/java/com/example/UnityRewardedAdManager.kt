package com.example

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions

object UnityRewardedAdManager {
    private const val GAME_ID = "800368206"
    private const val REWARDED_PLACEMENT = "Rewarded_Android"
    private const val COOLDOWN_MS = 10_000L

    private var initialized = false
    private var loading = false
    private var ready = false
    private var showing = false
    private var lastCompletedAt = 0L

    fun initialize(context: Context) {
        if (initialized) return

        UnityAds.initialize(
            context.applicationContext,
            GAME_ID,
            false,
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {
                    initialized = true
                    loadRewarded()
                }

                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError?,
                    message: String?
                ) {
                    initialized = false
                }
            }
        )
    }

    fun loadRewarded() {
        if (!initialized || loading || ready || showing) return

        loading = true

        UnityAds.load(
            REWARDED_PLACEMENT,
            object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(placementId: String?) {
                    loading = false
                    ready = true
                }

                override fun onUnityAdsFailedToLoad(
                    placementId: String?,
                    error: UnityAds.UnityAdsLoadError?,
                    message: String?
                ) {
                    loading = false
                    ready = false
                }
            }
        )
    }

    fun showRewarded(
        activity: Activity,
        onReward: () -> Unit,
        onComplete: () -> Unit,
        onFailure: () -> Unit
    ) {
        val now = SystemClock.elapsedRealtime()

        if (showing) {
            onFailure()
            return
        }

        if (lastCompletedAt > 0L &&
            now - lastCompletedAt < COOLDOWN_MS
        ) {
            onFailure()
            return
        }

        if (!initialized) {
            initialize(activity)
            onFailure()
            return
        }
        if (!ready) {
            loadRewarded()
            onFailure()
            return
        }

        ready = false
        showing = true

        UnityAds.show(
            activity,
            REWARDED_PLACEMENT,
            UnityAdsShowOptions(),
            object : IUnityAdsShowListener {

                override fun onUnityAdsShowFailure(
                    placementId: String?,
                    error: UnityAds.UnityAdsShowError?,
                    message: String?
                ) {
                    showing = false
                    loadRewarded()
                    onFailure()
                }

                override fun onUnityAdsShowStart(placementId: String?) = Unit

                override fun onUnityAdsShowClick(placementId: String?) = Unit

                override fun onUnityAdsShowComplete(
                    placementId: String?,
                    state: UnityAds.UnityAdsShowCompletionState?
                ) {
                    showing = false

                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                        lastCompletedAt = SystemClock.elapsedRealtime()
                        onReward()
                        onComplete()
                    }

                    loadRewarded()
                }
            }
        )
    }
}
