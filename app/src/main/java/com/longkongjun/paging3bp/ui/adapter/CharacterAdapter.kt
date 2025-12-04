package com.longkongjun.paging3bp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.longkongjun.paging3bp.R
import com.longkongjun.paging3bp.databinding.ItemCharacterBinding
import com.longkongjun.paging3bp.domain.model.Character
import java.util.Locale

class CharacterAdapter : PagingDataAdapter<Character, CharacterAdapter.CharacterViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    inner class CharacterViewHolder(
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            binding.imageAvatar.load(character.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
            }
            binding.textName.text = character.name
            binding.textStatus.text = character.status.uppercase(Locale.getDefault())
            binding.textStatus.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    statusColor(character.status)
                )
            )
            binding.textMeta.text = binding.root.context.getString(
                R.string.character_meta_format,
                character.species,
                character.gender
            )
            binding.textOrigin.text = binding.root.context.getString(R.string.origin_label, character.origin)
            binding.textLocation.text = binding.root.context.getString(R.string.location_label, character.location)
        }

        private fun statusColor(status: String): Int {
            val lower = status.lowercase(Locale.getDefault())
            return when {
                lower.contains("alive") -> R.color.status_alive
                lower.contains("dead") -> R.color.status_dead
                else -> R.color.status_unknown
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean = oldItem == newItem
    }
}
