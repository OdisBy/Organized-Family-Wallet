package com.ruliam.organizedfw.features.group

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.ruliam.organizedfw.core.data.model.GroupUsersDomain
import com.ruliam.organizedfw.core.data.repository.AvatarRepository
import com.ruliam.organizedfw.features.group.databinding.UsersViewBinding

/**
 * RecyclerView adapter for displaying a list of users.
 */
class UsersListAdapter(
    private val avatarRepository: AvatarRepository
) : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: UsersListAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    class ViewHolder(
        private val binding: UsersViewBinding,
        private val avatarRepository: AvatarRepository
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroupUsersDomain) {
            val profileImage: Bitmap
            binding.profilePhotoImage.setImageBitmap(profileImage)
            binding.balanceText.text = item.balance
            binding.lastTransactionText.text = item.lastTransaction
        }
    }
}