package com.gloushkov.dogceo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gloushkov.dogceo.databinding.ItemListImageBinding

/**
 * Created by Ognian Gloushkov on 16.09.23.
 */
class ImageListAdapter(private val list: List<String>): RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemListImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageViewHolder(binding)
    }
    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.image.load(list[position])
    }

    class ImageViewHolder(binding: ItemListImageBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
    }
}