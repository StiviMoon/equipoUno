package com.example.pb.ui.retos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pb.databinding.ItemRetoBinding
import com.example.pb.model.Reto

class RetosAdapter(
    private val onEdit:   (Reto) -> Unit,
    private val onDelete: (Reto) -> Unit
) : ListAdapter<Reto, RetosAdapter.RetoViewHolder>(RetoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val binding = ItemRetoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RetoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RetoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RetoViewHolder(private val binding: ItemRetoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reto: Reto) {

            binding.tvDescripcionReto.text = reto.descripcion


            binding.btnEditar.setOnClickListener {
                it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    onEdit(reto)
                }.start()
            }
            binding.btnEliminar.setOnClickListener {
                it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    onDelete(reto)
                }.start()
            }
        }
    }

    class RetoDiffCallback : DiffUtil.ItemCallback<Reto>() {
        override fun areItemsTheSame(a: Reto, b: Reto) = a.id == b.id
        override fun areContentsTheSame(a: Reto, b: Reto) = a == b
    }
}