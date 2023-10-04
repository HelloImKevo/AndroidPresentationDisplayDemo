package com.kevo.displaydemo.ui.slideshow

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kevo.displaydemo.R

class SlidingImageAdapter(
    private val items: MutableList<SlideItem>,
    infiniteSlidePager: InfiniteSlidePager
) : RecyclerView.Adapter<SlidingImageAdapter.SliderViewHolder>() {

    private val itemsCopy: List<SlideItem>
    private val viewPager: ViewPager2 = infiniteSlidePager.viewPager

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
        Log.v(TAG, "onBindViewHolder :: position $position items.size ${items.size}")
        holder.setImage(items[position])
        if (position == items.size - 2) {
            viewPager.post(extendCollectionRunnable)
        }
    }

    private val extendCollectionRunnable = Runnable {
        // TODO: Optimize this! (this will just balloon until we run out of memory)
        Log.i(TAG, "Rearranging content of items collection ...")

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

    @MainThread
    fun transitionToNextSlide() {
        Log.d(TAG, "transitionToNextSlide :: Sliding to item ... ${viewPager.currentItem + 1}")
        viewPager.currentItem = viewPager.currentItem + 1
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
