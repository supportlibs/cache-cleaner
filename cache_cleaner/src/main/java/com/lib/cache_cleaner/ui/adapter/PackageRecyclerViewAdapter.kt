//package com.lib.cache_cleaner.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.lib.cache_cleaner.ui.data.ApplicationCacheModel
//import com.ib.smartcare.cache_cleaner.util.PackageManagerHelper
//import com.ib.smartcare.databinding.ItemAppCacheCleanerBinding
//
//class PackageRecyclerViewAdapter(
//    var applications: List<ApplicationCacheModel>,
//    val onItemChecked: ((application: ApplicationCacheModel, checked: Boolean) -> Unit)
//) : RecyclerView.Adapter<PackageRecyclerViewAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        return ViewHolder(
//            ItemAppCacheCleanerBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    fun submitList(list: List<ApplicationCacheModel>) {
//        applications = list
//        notifyDataSetChanged()
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = applications.filter { !it.ignore }[position]
//        if (item.ignore) {
//            holder.packageLayout.visibility = View.GONE
//            return
//        }
//        holder.packageLayout.visibility = View.VISIBLE
//        holder.packageNameView.text = item.label
//        holder.packageLabelView.setOnCheckedChangeListener(null)
//        holder.cacheSizeView.text = PackageManagerHelper.humanReadableByteCountSI(item.cacheSize)
//        holder.packageLabelView.isChecked = item.checked
//        holder.packageLabelView.setOnCheckedChangeListener { _, checked ->
//            item.checked = checked
//            onItemChecked(item, checked)
//        }
//        holder.packageLayout.setOnClickListener { holder.packageLabelView.performClick() }
//        holder.packageIconView.setImageDrawable(
//            PackageManagerHelper.getApplicationIcon(
//                holder.packageIconView.context,
//                item.pkgInfo
//            )
//        )
//    }
//
//    fun chooseAll(check: Boolean, callback:() -> Unit) {
//        applications.forEach { item ->
//            item.checked = check
//        }
//        notifyItemRangeChanged(0, itemCount)
//        callback.invoke()
//    }
//
//    override fun getItemCount(): Int = applications.filter { !it.ignore }.size
//
//    inner class ViewHolder(binding: ItemAppCacheCleanerBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        val packageLayout: ConstraintLayout = binding.packageLayout
//        val packageIconView: ImageView = binding.packageIcon
//        val packageLabelView: CheckBox = binding.packageLabel
//        val packageNameView: TextView = binding.packageName
//        val cacheSizeView: TextView = binding.cacheSize
//
//        override fun toString(): String {
//            return super.toString() + " '" + packageNameView.text + "'"
//        }
//    }
//
//}