//package com.lib.cache_cleaner.ui.activity
//
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.View.GONE
//import android.view.View.VISIBLE
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.ib.smartcare.R
//import com.lib.cache_cleaner.const.Constant
//import com.lib.cache_cleaner.ui.adapter.DividerItemDecoratorWithFooter
//import com.lib.cache_cleaner.ui.adapter.PackageRecyclerViewAdapter
//import com.lib.cache_cleaner.ui.data.ApplicationCacheModel
//import com.lib.cache_cleaner.ui.dialog.PermissionDialogBuilder
//import com.ib.smartcare.cache_cleaner.util.*
//import com.ib.smartcare.databinding.ActivityCacheCleanerBinding
//import com.ib.smartcare.ui.common.MainActivity
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.activity_cache_cleaner.*
//
//@AndroidEntryPoint
//class CacheCleanerActivity : AppCompatActivity(), IIntentActivityCallback {
//
//
//    private lateinit var binding: ActivityCacheCleanerBinding
//    private lateinit var localBroadcastManager: LocalBroadcastManagerActivityHelper
//
//    private val viewModel: CacheCleanerViewModel by viewModels()
//
//    private fun onItemChecked(application: ApplicationCacheModel, checked: Boolean) {
//        if (checked)
//            viewModel.selectItem(application)
//        else
//            viewModel.unSelectItem(application)
//        viewModel.calculateSelectedCache()
//    }
//
//    private val adapter =
//        PackageRecyclerViewAdapter(listOf()) { application: ApplicationCacheModel, checked: Boolean ->
//            onItemChecked(application, checked)
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityCacheCleanerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        localBroadcastManager = LocalBroadcastManagerActivityHelper(this, this)
//        localBroadcastManager.register()
//
//        setupViews()
//        setupObservers()
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        checkAndShowPermissionDialogs()
//        viewModel.updatePackageList { viewModel.checkAllItems() }
//        Log.e("TAG", "onResume: ${viewModel.applicationsList.value.toString()}")
//
//    }
//
//    private fun setupObservers() {
//        viewModel.applicationsList.observe(this) { applicationsList ->
//            with(binding) {
//                if (applicationsList.isNotEmpty()) progress.visibility = GONE
//                adapter.submitList(applicationsList)
//
//                buttonClean.visibility = VISIBLE
//                chooseAllLayout.visibility = VISIBLE
//            }
//            choose_all_check_box.isChecked = applicationsList.all { it.checked }
//            viewModel.calculateSelectedCache()
//        }
//
//        viewModel.selectedMemory.observe(this) { selectedMemory ->
//            if (selectedMemory == 0L) {
//                button_clean.text = getString(R.string.clean)
//                button_clean.background =
//                    getDrawable(R.drawable.ic_optimize_btn_inactive_without_back)
//            } else {
//                button_clean.text = getString(
//                    R.string.clean_with_mem,
//                    PackageManagerHelper.humanReadableByteCountSI(selectedMemory)
//                )
//                button_clean.background = getDrawable(R.drawable.ic_optimize_btn_without_back)
//            }
////            choose_all_check_box.isChecked = selectedMemory == viewModel.applicationsList.value?.sumOf { it.cacheSize }
//        }
//    }
//
//    private fun setupViews() {
//        with(binding) {
//            list.layoutManager = LinearLayoutManager(this@CacheCleanerActivity)
//            list.adapter = adapter
//            list.addItemDecoration(
//                DividerItemDecoratorWithFooter(
//                    ContextCompat.getDrawable(
//                        this@CacheCleanerActivity,
//                        R.drawable.divider_drawable
//                    ), 45
//                )
//            )
//
//            buttonClean.setOnClickListener {
//                if (!checkAndShowPermissionDialogs()) return@setOnClickListener
//                startCleanCache(viewModel.getCheckedItemsPackageNames())
//            }
//
//            ivDrawerIcon.setOnClickListener { onBackPressed() }
//
//            chooseAllCheckBox.setOnClickListener {
//                adapter.chooseAll(chooseAllCheckBox.isChecked) { viewModel.calculateSelectedCache() }
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        startActivity(Intent(this@CacheCleanerActivity, MainActivity::class.java))
//    }
//
//    private fun startCleanCache(pkgList: MutableList<String>) {
//        addExtraSearchText()
//
//        pkgList.apply {
//            if (isEmpty()) {
//                Toast.makeText(
//                    this@CacheCleanerActivity,
//                    "Choose applications to clean",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return
//            }
//            if (contains(packageName)) {
//                remove(packageName)
//            }
//        }
//
//        localBroadcastManager.sendPackageList(pkgList as ArrayList<String>)
//    }
//
//    private fun addExtraSearchText() {
//        val intent = Intent(Constant.Intent.ExtraSearchText.ACTION)
//
//        ExtraSearchTextHelper.getTextForClearCache(this).let { list ->
//            if (list.isNotEmpty())
//                intent.putExtra(Constant.Intent.ExtraSearchText.NAME_CLEAR_CACHE_TEXT_LIST, list)
//        }
//
//        ExtraSearchTextHelper.getTextForStorage(this).let { list ->
//            if (list.isNotEmpty())
//                intent.putExtra(Constant.Intent.ExtraSearchText.NAME_STORAGE_TEXT_LIST, list)
//        }
//
//        intent.extras?.let {
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//        }
//    }
//
//    override fun onCleanCacheFinish(interrupted: Boolean) {
//        ActivityHelper.returnBackToActivity(this, this.intent)
//        viewModel.uncheckAllItems()
//        viewModel.clearSelectedMemory()
//        binding.chooseAllCheckBox.isChecked = false
//
//    }
//
//    override fun onStopAccessibilityServiceFeedback() {
////        updateStartStopServiceButton()
//    }
//
//    private fun checkAndShowPermissionDialogs(): Boolean {
//        val hasAccessibilityPermission = PermissionChecker.checkAccessibilityPermission(this)
//        if (!hasAccessibilityPermission) {
//            PermissionDialogBuilder.buildAccessibilityPermissionDialog(this)
//            return false
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val hasUsageStatsPermission = PermissionChecker.checkUsageStatsPermission(this)
//            if (!hasUsageStatsPermission) {
//                PermissionDialogBuilder.buildUsageStatsPermissionDialog(this)
//                return false
//            }
//        }
//        return PermissionChecker.checkAllRequiredPermissions(this)
//    }
//
//
//}