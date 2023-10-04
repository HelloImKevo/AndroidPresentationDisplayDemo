package com.kevo.displaydemo.ui.secondarydisplay

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kevo.displaydemo.R
import com.kevo.displaydemo.databinding.CustomerPresentationScreenBinding
import com.kevo.displaydemo.ui.slideshow.SlideItem
import com.kevo.displaydemo.ui.slideshow.SlideItem.Key
import com.kevo.displaydemo.ui.slideshow.SlidingImageAdapter
import com.kevo.displaydemo.util.SnackbarHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

/**
 * @param themedContext Should be a View context that contains UI theme information.
 * The "Application" context may not contain the needed theme details to inflate the
 * [PresentationFragment].
 * @param themeResourceId For whatever reason, the Android architects did not provide
 * a convenient way to determine the Theme Resource ID, which is required by the native
 * [Presentation] class to function properly.
 */
class SimplePresentationFragment(
    themedContext: Context,
    themeResourceId: Int,
    display: Display,
) : PresentationFragment() {

    private var _binding: CustomerPresentationScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var slidingImageAdapter: SlidingImageAdapter
    private lateinit var viewPager: ViewPager2

    private val foregroundEvents: CoroutineScope = MainScope()
    private var timer: Timer? = null

    init {
        setDisplay(themedContext, themeResourceId, display)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomerPresentationScreenBinding.inflate(inflater, container, false).apply {
            btnRed.setOnClickListener {
                SnackbarHelper.showLong(it, "You clicked the Red button")
            }
            btnYellow.setOnClickListener {
                SnackbarHelper.showLong(it, "You clicked the Yellow button")
            }
            btnGreen.setOnClickListener {
                SnackbarHelper.showLong(it, "You clicked the Green button")
            }
        }

        setupViewPager()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        startOrResetTimer()
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewPager() = with(binding) {
        viewPager = this.infiniteViewPager

        val sliderImages = mutableListOf<SlideItem>()
        sliderImages.add(
            SlideItem(Key.CustomerOrderEntry, R.drawable.example_screen_customer_order_total)
        )
        sliderImages.add(
            SlideItem(Key.LoyaltyPhone, R.drawable.example_screen_loyalty_phone)
        )
        sliderImages.add(
            SlideItem(Key.ReceiptSelection, R.drawable.example_screen_receipt_selection)
        )
        sliderImages.add(
            SlideItem(Key.SampleAd4, R.drawable.sample_ad_4)
        )

        slidingImageAdapter = SlidingImageAdapter(sliderImages, viewPager)
        viewPager.adapter = slidingImageAdapter

        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val item: SlideItem? = slidingImageAdapter.getItemAt(position)
                // Useful for debugging
                // Log.v(TAG, "Now showing item: ${item?.key}, ${item?.imageResId}")
                when (item?.key) {
                    Key.CustomerOrderEntry -> btnContainer.isVisible = true

                    else -> btnContainer.isVisible = false
                }
            }
        })
    }

    fun setText(newText: String) {
        binding.secondScreenTextView.text = newText
    }

    private fun startOrResetTimer() {
        Log.d(TAG, "Resetting scheduled timer")

        timer?.cancel()
        timer = Timer().apply {
            this.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    foregroundEvents.launch { transitionToNextSlide() }
                }
            }, AUTOMATIC_SLIDE_INTERVAL, AUTOMATIC_SLIDE_INTERVAL)
        }
    }

    @MainThread
    private fun transitionToNextSlide() {
        Log.d(TAG, "Sliding to item ... ${viewPager.currentItem + 1}")
        viewPager.currentItem = viewPager.currentItem + 1
    }

    companion object {

        const val TAG = "SimplePresentFragment"
        private const val AUTOMATIC_SLIDE_INTERVAL = 3_000L
    }
}
