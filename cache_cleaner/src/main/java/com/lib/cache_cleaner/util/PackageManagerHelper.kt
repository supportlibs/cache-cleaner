package com.lib.cache_cleaner.util

import android.app.usage.StorageStats
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.*
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.storage.StorageManager
import kotlinx.coroutines.delay
import java.io.File
import java.lang.reflect.Method
import java.text.StringCharacterIterator

class PackageManagerHelper {

    companion object {

        @JvmStatic
        fun getInstalledApps(
            context: Context
        ): ArrayList<PackageInfo> {
            val list = context.packageManager.getInstalledPackages(0)
            val pkgInfoList = ArrayList<PackageInfo>()
            for (i in list.indices) {
                val packageInfo = list[i]
                val flags = packageInfo!!.applicationInfo.flags
                val isSystemApp = (flags and ApplicationInfo.FLAG_SYSTEM) != 0
                if (!isSystemApp && list[i].packageName != context.packageName)
                    pkgInfoList.add(packageInfo)
            }

            return pkgInfoList
        }

        @JvmStatic
        fun getApplicationIcon(context: Context, pkgInfo: PackageInfo): Drawable? {
            return context.packageManager.getApplicationIcon(pkgInfo.packageName)
        }

        @JvmStatic
        fun getApplicationResourceString(
            context: Context, pkgName: String,
            resourceName: String
        ): String? {
            context.packageManager?.let { pm ->
                try {
                    val res = pm.getResourcesForApplication(pkgName)
                    val resId = res.getIdentifier(resourceName, "string", pkgName)
                    if (resId != 0)
                        return res.getString(resId)
                } catch (e: PackageManager.NameNotFoundException) {
                }
            }

            return null
        }

        @JvmStatic
        fun getApplicationLabel(context: Context, pkgInfo: PackageInfo): String {
            var localizedLabel: String? = null
            context.packageManager?.let { pm ->
                try {
                    val res = pm.getResourcesForApplication(pkgInfo.applicationInfo)
                    val resId = pkgInfo.applicationInfo.labelRes
                    if (resId != 0)
                        try {
                            localizedLabel = res.getString(resId)
                        } catch (e: Resources.NotFoundException) {
                        }
                } catch (e: PackageManager.NameNotFoundException) {
                }
            }

            return localizedLabel
                ?: pkgInfo.applicationInfo.nonLocalizedLabel?.toString()
                ?: pkgInfo.packageName
        }

        @JvmStatic
        fun getStorageStats(context: Context, pkgInfo: PackageInfo): StorageStats? {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null

            try {
                val storageStatsManager =
                    context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                return storageStatsManager.queryStatsForPackage(
                    StorageManager.UUID_DEFAULT, pkgInfo.packageName,
                    android.os.Process.myUserHandle()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        @JvmStatic
        fun getCacheSizeDiff(old: StorageStats?, new: StorageStats?): Long {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return 0
            if (old == null || new == null) return 0

            try {
                return if (new.cacheBytes >= old.cacheBytes) 0
                else old.cacheBytes - new.cacheBytes
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0
        }


        suspend fun getPackageSizeInfoString(
            context: Context, packageManager: PackageManager, packageName: String
        ): Long {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageStatsManager: StorageStatsManager =
                    context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager

                try {
                    val applicationInfo: ApplicationInfo =
                        context.packageManager.getApplicationInfo(packageName, 0)

                    storageStatsManager.queryStatsForUid(
                        applicationInfo.storageUuid,
                        context.packageManager.getApplicationInfo(packageName, 0).uid
                    ).apply {
                        return (cacheBytes /*+ dataBytes *//*+ appBytes*/)
                    }
                } catch (e: Exception) {
                    return getFakePackageSizeInfoString(packageManager, packageName)
                }
            } else {
                return try {
                    getPackageSizeInfoBelow26Sdk(packageManager, packageName)
                } catch (e: Exception) {
                    getFakePackageSizeInfoString(packageManager, packageName)
                }
            }
        }

        private fun getFakePackageSizeInfoString(
            packageManager: PackageManager,
            packageName: String
        ): Long =
            File(
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                ).publicSourceDir
            ).length()

        private suspend fun getPackageSizeInfoBelow26Sdk(
            packageManager: PackageManager,
            packageName: String
        ): Long {
            var totalSize = 0L
            val getPackageSizeInfo: Method
            try {
                getPackageSizeInfo = packageManager.javaClass.getMethod(
                    "getPackageSizeInfo", String::class.java,
                    IPackageStatsObserver::class.java
                )

                getPackageSizeInfo.invoke(
                    packageManager, packageName,
                    object : IPackageStatsObserver.Stub() {
                        override fun onGetStatsCompleted(pStats: PackageStats, succeeded: Boolean) {
                            totalSize = /*pStats.dataSize*/ + pStats.cacheSize /*+ pStats.codeSize*/
                        }
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(200)
            return totalSize
        }

        fun humanReadableByteCountSI(bytes: Long): String {
            var mBytes = bytes

            if (-1000 < mBytes && mBytes < 1000) {
                return "$mBytes B"
            }
            val ci = StringCharacterIterator("kMGTPE")
            while (mBytes <= -999950 || mBytes >= 999950) {
                mBytes /= 1000
                ci.next()
            }
            return String.format("%.1f %cB", mBytes / 1000.0, ci.current())
        }

    }
}