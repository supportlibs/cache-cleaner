package com.lib.cache_cleaner.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.lib.cache_cleaner.R
import com.lib.cache_cleaner.util.AccessibilityClearCacheManager
import com.lib.cache_cleaner.util.IIntentServiceCallback
import com.lib.cache_cleaner.util.LocalBroadcastManagerServiceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppCacheCleanerService : AccessibilityService(), IIntentServiceCallback {

    companion object {
        private val accessibilityClearCacheManager = AccessibilityClearCacheManager()
    }


    private lateinit var localBroadcastManager: LocalBroadcastManagerServiceHelper

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager = LocalBroadcastManagerServiceHelper(this, this)
        localBroadcastManager.register()
    }

    override fun onDestroy() {
        localBroadcastManager.onDestroy()

        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            accessibilityClearCacheManager.checkEvent(event)
    }

    override fun onInterrupt() {
    }

    override fun onStopAccessibilityService() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        disableSelf()
    }

    override fun onExtraSearchText(clearCacheTextList: Array<String>?,
                                   storageTextList: Array<String>?) {
        accessibilityClearCacheManager.apply {
            setArrayTextClearCacheButton(
                ArrayList<CharSequence>().apply {
                    clearCacheTextList?.forEach { add(it) }
                    add(getText(R.string.clear_cache_btn_text))
                }
            )

            setArrayTextStorageAndCacheMenu(
                ArrayList<CharSequence>().apply {
                    storageTextList?.forEach { add(it) }
                    add(getText(R.string.storage_settings_for_app))
                    add(getText(R.string.storage_label))
                }
            )
        }
    }

    override fun onClearCache(pkgList: ArrayList<String>?) {
        pkgList?.let{
            CoroutineScope(Dispatchers.IO).launch {
                accessibilityClearCacheManager.clearCacheApp(
                    pkgList,
                    localBroadcastManager::sendAppInfo,
                    localBroadcastManager::sendFinish)
            }
        } ?: localBroadcastManager.sendFinish(true)
    }

    override fun onCleanCacheFinish() {

    }

}