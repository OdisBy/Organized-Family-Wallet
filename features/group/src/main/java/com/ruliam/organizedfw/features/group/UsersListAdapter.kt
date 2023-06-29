package com.ruliam.organizedfw.features.group

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ruliam.organizedfw.core.data.model.GroupUsersDomain
import com.ruliam.organizedfw.features.group.databinding.UsersCardViewBinding

/**
 * RecyclerView adapter for displaying a list of users.
 */
class UsersListAdapter : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    private val asyncListDiffer: AsyncListDiffer<GroupUsersDomain> = AsyncListDiffer(this, DiffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UsersCardViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun updateUsers(users: List<GroupUsersDomain?>){
        asyncListDiffer.submitList(users)
    }


    class ViewHolder(
        private val binding: UsersCardViewBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroupUsersDomain) {

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

        }
    }


    object DiffCallback : DiffUtil.ItemCallback<GroupUsersDomain>() {

        override fun areItemsTheSame(oldItem: GroupUsersDomain, newItem: GroupUsersDomain): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupUsersDomain, newItem: GroupUsersDomain): Boolean {
            return oldItem == newItem
        }
    }
}