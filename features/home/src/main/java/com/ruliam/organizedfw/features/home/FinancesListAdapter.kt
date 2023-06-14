package com.ruliam.organizedfw.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ruliam.organizedfw.features.home.databinding.HeaderListDesignBinding
import com.ruliam.organizedfw.features.home.databinding.ItemFinanceBinding
import com.ruliam.organizedfw.features.home.model.FinanceItemType
import com.ruliam.organizedfw.features.home.model.HeaderItemType
import com.ruliam.organizedfw.features.home.model.ListItemType

/**
 * RecyclerView adapter for displaying a list of finances.
 *
 * The UI is based on the [ListItemType] ([ItemFinanceBinding] [HeaderListDesignBinding]).
 * We use the [FinanceItemType] as model of finances binding, also we use [HeaderListDesignBinding] to display the date of finances.
 */
class FinancesListAdapter(
    private val viewModel: HomeFragmentViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val asyncListDiffer: AsyncListDiffer<ListItemType> = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ListItemType.TYPE_HEADER ->
                HeaderViewHolder(HeaderListDesignBinding.inflate(layoutInflater, parent, false))
            else ->
                FinanceViewHolder(ItemFinanceBinding.inflate(layoutInflater, parent, false), viewModel)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ListItemType.TYPE_HEADER -> (holder as HeaderViewHolder).bind(
                item = asyncListDiffer.currentList[position] as HeaderItemType
            )
            ListItemType.TYPE_FINANCE -> (holder as FinanceViewHolder).bind(
                item = asyncListDiffer.currentList[position] as FinanceItemType
            )
            else -> return
        }
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun updateFinances(items: List<ListItemType>) {
        asyncListDiffer.submitList(items)
    }

    class FinanceViewHolder(
        private val binding: ItemFinanceBinding,
        private val viewModel: HomeFragmentViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FinanceItemType) {
            binding.nameOfFinance.text = item.financeItem.name
            itemView.setOnClickListener {
                binding.nameOfFinance.isSelected = !binding.nameOfFinance.isSelected
            }
            binding.amount.text = viewModel.amountUtils.formatBalance(item.financeItem.amount)
//            binding.typeOfFinance.text = item.financeItem.description
            binding.ownerOfFinance.text = item.financeItem.username
        }
    }

    class HeaderViewHolder(private val binding: HeaderListDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HeaderItemType) {
            binding.headerText.text = item.title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return asyncListDiffer.currentList[position].typeOfItem
    }

    object DiffCallback : DiffUtil.ItemCallback<ListItemType>() {
        override fun areItemsTheSame(oldItem: ListItemType, newItem: ListItemType): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: ListItemType, newItem: ListItemType): Boolean {
            return false
        }
    }
}
