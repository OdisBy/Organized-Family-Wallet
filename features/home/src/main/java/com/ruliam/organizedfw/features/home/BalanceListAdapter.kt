package com.ruliam.organizedfw.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ruliam.organizedfw.features.home.databinding.BalanceCardViewBinding
import com.ruliam.organizedfw.features.home.model.BalanceCardItem

class BalanceListAdapter(
)  : RecyclerView.Adapter<BalanceListAdapter.ViewHolder>() {


    private val asyncListDiffer: AsyncListDiffer<BalanceCardItem> = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BalanceCardViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun updateCard(items: List<BalanceCardItem>) {
        asyncListDiffer.submitList(items)
    }

    class ViewHolder(
        private val binding: BalanceCardViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BalanceCardItem) {
            binding.profilePhotoImage.setImageBitmap(item.avatar)
            binding.balanceText.text = item.balance
            binding.lastTransactionText.text = item.lastTransaction
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<BalanceCardItem>() {

        override fun areItemsTheSame(oldItem: BalanceCardItem, newItem: BalanceCardItem): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: BalanceCardItem, newItem: BalanceCardItem): Boolean {
            return oldItem == newItem
        }
    }
}
