//package com.lib.cache_cleaner.ui.activity
//
//import android.content.Context
//import android.content.pm.PackageInfo
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.lib.cache_cleaner.ui.data.ApplicationCacheModel
//import com.ib.smartcare.cache_cleaner.util.ApplicationCacheModelMapper
//import com.ib.smartcare.cache_cleaner.util.PackageManagerHelper
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class CacheCleanerViewModel @Inject constructor(
//    @ApplicationContext private val context: Context,
//    private val mapper: ApplicationCacheModelMapper
//) : ViewModel() {
//
//    private var packageInfoList = arrayListOf<PackageInfo>()
//
//    private val _applicationsList = MutableLiveData<List<ApplicationCacheModel>>()
//    val applicationsList: LiveData<List<ApplicationCacheModel>>
//        get() = _applicationsList
//
//    private val _selectedMemory = MutableLiveData<Long>(0)
//    val selectedMemory: LiveData<Long>
//        get() = _selectedMemory
//
//    private val _reloadCount = MutableLiveData(0)
//
//    init {
//        updatePackageList { checkAllItems() }
//    }
//
//    fun updatePackageList(callback: () -> Unit) {
//        packageInfoList = PackageManagerHelper.getInstalledApps(context)
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _applicationsList.postValue(
//                mapper.mapPackageInfoList(packageInfoList, context)
//                    .filter { it.cacheSize > 2000 * 1000 }//2Mb
//                    .sortedByDescending { it.cacheSize })
//
//            if (_reloadCount.value!! <= 1) callback.invoke()
//
//            _reloadCount.postValue(_reloadCount.value?.plus(1))
//        }
//    }
//
//    fun getCheckedItemsPackageNames() = _applicationsList.value
//        ?.filter { !it.ignore && it.checked }
//        ?.map { it.name }
//        ?.toMutableList()
//        ?: mutableListOf()
//
//    fun calculateSelectedCache() =
//        _selectedMemory.postValue(_applicationsList.value
//            ?.filter { it.checked }
//            ?.sumOf { it.cacheSize })
//
//    fun selectItem(applicationCacheModel: ApplicationCacheModel) {
//        val list = applicationsList.value!!
//        list.find { it == applicationCacheModel }?.checked = true
//        _applicationsList.postValue(list)
//    }
//
//    fun unSelectItem(applicationCacheModel: ApplicationCacheModel) {
//        val list = applicationsList.value!!
//        list.find { it == applicationCacheModel }?.checked = false
//        _applicationsList.postValue(list)
//    }
//
//    fun clearSelectedMemory() {
//        _selectedMemory.postValue(0L)
//    }
//
//    fun uncheckAllItems() {
//        viewModelScope.launch {
//            val list = applicationsList.value?.map {
//                ApplicationCacheModel(
//                    it.pkgInfo,
//                    it.name,
//                    it.label,
//                    it.locale,
//                    it.stats,
//                    it.hideStats,
//                    checked = false,
//                    it.ignore,
//                    it.cacheSize
//                )
//            } ?: mapper.mapPackageInfoList(packageInfoList, context)
//                .filter { it.cacheSize > 2000 * 1000 }
//                .sortedByDescending { it.cacheSize }
//
//            _applicationsList.postValue(list)
//        }
//    }
//
//    fun checkAllItems() {
//        viewModelScope.launch {
//            val list = applicationsList.value?.map {
//                ApplicationCacheModel(
//                    it.pkgInfo,
//                    it.name,
//                    it.label,
//                    it.locale,
//                    it.stats,
//                    it.hideStats,
//                    checked = true,
//                    it.ignore,
//                    it.cacheSize
//                )
//            } ?: mapper.mapPackageInfoList(packageInfoList, context)
//                .filter { it.cacheSize > 2000 * 1000 }
//                .sortedByDescending { it.cacheSize }
//
//            _applicationsList.postValue(list)
//        }
//    }
//}