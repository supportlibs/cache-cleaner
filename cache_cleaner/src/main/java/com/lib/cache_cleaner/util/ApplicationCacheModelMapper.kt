package com.lib.cache_cleaner.util

import android.content.Context
import android.content.pm.PackageInfo
import com.lib.cache_cleaner.ui.data.ApplicationCacheModel

class ApplicationCacheModelMapper {

    suspend fun mapPackageInfoList(packageInfoList: List<PackageInfo>, context: Context): List<ApplicationCacheModel> {
        return mutableListOf<ApplicationCacheModel>().apply {
            packageInfoList.forEach {
                add(
                    ApplicationCacheModel(
                        pkgInfo = it,
                        name = it.packageName,
                        label = PackageManagerHelper.getApplicationLabel(context,it),
                        locale = LocaleHelper.getCurrentLocale(context),
                        stats = PackageManagerHelper.getStorageStats(context, it),
                        hideStats = false,
                        checked = false,
                        ignore = false,
                        cacheSize = PackageManagerHelper.getPackageSizeInfoString(context, context.packageManager, it.packageName)
                    )
                )
            }
        }
    }


}
