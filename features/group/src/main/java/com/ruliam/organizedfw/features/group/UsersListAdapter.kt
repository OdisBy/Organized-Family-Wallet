package com.ruliam.organizedfw.features.group

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.features.group.databinding.UsersCardViewBinding

/**
 * RecyclerView adapter for displaying a list of users.
 */
class UsersListAdapter(private val viewModel: GroupPageViewModel) : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    private val asyncListDiffer: AsyncListDiffer<GroupUserDomain> = AsyncListDiffer(this, DiffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UsersCardViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, parent.context, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun updateUsers(users: List<GroupUserDomain?>){
        asyncListDiffer.submitList(users)
    }

    class ViewHolder(
        private val binding: UsersCardViewBinding,
        private val context: Context,
        private val viewModel: GroupPageViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupUserDomain) {
            // Set profile image
            Glide.with(context)
                .asBitmap()
                .load(item.profilePhoto)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        binding.profilePhotoImage.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

            binding.username.text = item.username

            binding.root.setOnClickListener {
                Log.d(TAG, "Click binding adapter userId ${item.id}")
                viewModel.onUserClick(item)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<GroupUserDomain>() {
        override fun areItemsTheSame(oldItem: GroupUserDomain, newItem: GroupUserDomain): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: GroupUserDomain, newItem: GroupUserDomain): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val TAG = "UsersListAdapter"
    }
}