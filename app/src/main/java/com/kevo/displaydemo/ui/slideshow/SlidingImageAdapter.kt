package com.kevo.displaydemo.ui.slideshow

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kevo.displaydemo.R

class SlidingImageAdapter(
    private val items: MutableList<SlideItem>,
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<SlidingImageAdapter.SliderViewHolder>() {

    private val itemsCopy: List<SlideItem>

    init {
        assert(items.size >= 2) { "The Sliding Image Adapter requires at least 2 items" }
        itemsCopy = items.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slide_item_component, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(items[position])
        if (position == items.size - 2) {
            viewPager.post(extendCollectionRunnable)
        }
    }

    private val extendCollectionRunnable = Runnable {
        // TODO: Optimize this! (this will just balloon until we run out of memory)

        // Take the first element and append it to the end of the array, to achieve
        // the infinite scroll effect.
        // val item = items.removeFirst()
        // items.add(item)
        // notifyItemInserted(items.size)

        items.addAll(itemsCopy)
        notifyItemInserted(items.size)
    }

    fun getItemAt(position: Int): SlideItem? {
        return try {
            items[position]
        } catch (e: IndexOutOfBoundsException) {
            Log.w(TAG, "Invalid position: $position")
            null
        }
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setImage(item: SlideItem) {
            // Useful for debugging
            // Log.d(TAG, "Now showing item: ${item.key}, ${item.imageResId}")

            itemView.findViewById<ImageView>(R.id.image_slide)?.apply {
                setImageResource(item.imageResId)
            }
        }
    }

    companion object {

        const val TAG = "SlidingImageAdapter"
    }
}
