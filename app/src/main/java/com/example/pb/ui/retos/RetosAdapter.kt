package com.example.pb.ui.retos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pb.databinding.ItemRetoBinding
import com.example.pb.model.Reto

class RetosAdapter(
    private val onDeleteClick: (Reto) -> Unit
) : ListAdapter<Reto, RetosAdapter.RetoViewHolder>(DiffCallback) {

    inner class RetoViewHolder(private val binding: ItemRetoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reto: Reto) {
            binding.tvRetoTexto.text = reto.texto
            binding.btnDelete.setOnClickListener { onDeleteClick(reto) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val binding = ItemRetoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RetoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RetoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reto>() {
        override fun areItemsTheSame(oldItem: Reto, newItem: Reto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Reto, newItem: Reto) = oldItem == newItem
    }
}
