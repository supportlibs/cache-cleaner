package com.lib.cache_cleaner.ui.data

import android.app.usage.StorageStats
import android.content.pm.PackageInfo
import java.util.*

data class ApplicationCacheModel(val pkgInfo: PackageInfo, val name: String,
                                 var label: String, var locale: Locale,
                                 var stats: StorageStats?, var hideStats: Boolean,
                                 var checked: Boolean, var ignore: Boolean, var cacheSize:Long = 0) {
    override fun toString(): String = "$name{$checked}"
}
