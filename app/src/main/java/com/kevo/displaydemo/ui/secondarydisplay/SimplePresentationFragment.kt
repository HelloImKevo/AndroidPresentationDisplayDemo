package com.kevo.displaydemo.ui.secondarydisplay

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kevo.displaydemo.R
import com.kevo.displaydemo.databinding.CustomerPresentationScreenBinding
import com.kevo.displaydemo.ui.slideshow.InfiniteSlidePager
import com.kevo.displaydemo.ui.slideshow.SlideItem
import com.kevo.displaydemo.ui.slideshow.SlideItem.Key
import com.kevo.displaydemo.ui.slideshow.SlidingImageAdapter
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
    private var overlays: Overlays? = null

    private lateinit var slidingImageAdapter: SlidingImageAdapter
    private lateinit var infiniteSlidePager: InfiniteSlidePager

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
        _binding = CustomerPresentationScreenBinding.inflate(inflater, container, false)
        overlays = Overlays(binding).also {
            it.setupCustomerOrderTotal(touchListener)
            it.setupLoyaltyPhone(touchListener)
            it.setupReceiptSelection(touchListener)
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
        overlays = null
    }

    private fun setupViewPager() = with(binding) {
        val viewPager = this.infiniteViewPager.also {
            infiniteSlidePager = InfiniteSlidePager(it)
        }

        val sliderImages = mutableListOf<SlideItem>()
        sliderImages.add(
            SlideItem(Key.CustomerOrderEntry2, R.drawable.example_screen_customer_order_total_2)
        )
        sliderImages.add(
            SlideItem(Key.LoyaltyPhone, R.drawable.example_screen_loyalty_phone)
        )
        sliderImages.add(
            SlideItem(Key.CustomerOrderEntry3, R.drawable.example_screen_customer_order_total_3)
        )
        sliderImages.add(
            SlideItem(Key.VisaTapPay, R.drawable.example_screen_visa_tap_pay)
        )
        sliderImages.add(
            SlideItem(Key.CustomerOrderEntry4, R.drawable.example_screen_customer_order_total_4)
        )
        sliderImages.add(
            SlideItem(Key.ReceiptSelection, R.drawable.example_screen_receipt_selection)
        )
        sliderImages.add(
            SlideItem(Key.BankAd, R.drawable.example_screen_bank_ad)
        )

        // Add a copy of the last item to the beginning for our infinite scrolling setup
        sliderImages.add(0, sliderImages.last())
        // Add a copy of the first item to the end of the list
        sliderImages.add(sliderImages.size, sliderImages[1])

        slidingImageAdapter = SlidingImageAdapter(sliderImages, infiniteSlidePager)
        viewPager.adapter = slidingImageAdapter

        // Start the ViewPager2 at the second item in the list to compensate for our infinite
        // scroll setup
        viewPager.currentItem = 1
        this@SimplePresentationFragment.overlays?.showCustomerOrderTotal()

        // Setup infinite scrolling for the ViewPager2
        // Extract the recycler and layout manager used internally
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val itemCount = viewPager.adapter?.itemCount ?: 0
        // Every time a scroll occurs run our logic to determine if we need to loop around
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemVisible
                        = layoutManager.findFirstVisibleItemPosition()
                val lastItemVisible
                        = layoutManager.findLastVisibleItemPosition()
                if (firstItemVisible == (itemCount - 1) && dx > 0) {
                    // Once we get to the fake first page that was placed at the end of our list set
                    // the actual position to the real first page
                    recyclerView.scrollToPosition(1)
                } else if (lastItemVisible == 0 && dx < 0) {
                    // Do the same thing for the other direction
                    recyclerView.scrollToPosition(itemCount - 2)
                }
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Reset timer so it doesn't interrupt user swipes
                startOrResetTimer()

                val item: SlideItem? = slidingImageAdapter.getItemAt(position)
                // Useful for debugging
                // Log.v(TAG, "Now showing item: ${item?.key}, ${item?.imageResId}")
                when (item?.key) {
                    Key.CustomerOrderEntry2,
                    Key.CustomerOrderEntry3,
                    Key.CustomerOrderEntry4 ->
                        this@SimplePresentationFragment.overlays?.showCustomerOrderTotal()

                    Key.LoyaltyPhone ->
                        this@SimplePresentationFragment.overlays?.showLoyaltyPhone()

                    Key.ReceiptSelection ->
                        this@SimplePresentationFragment.overlays?.showReceiptSelection()

                    else -> this@SimplePresentationFragment.overlays?.hideAll()
                }
            }
        })
    }

    fun setText(newText: String) {
        overlays?.customerOrderTotal?.secondScreenTextView?.text = newText
    }

    private val touchListener = View.OnTouchListener { v: View?, _: MotionEvent ->
        startOrResetTimer()
        v?.performClick()
        false
    }

    private fun startOrResetTimer() {
        Log.d(TAG, "Resetting scheduled timer")

        timer?.cancel()
        timer = Timer().apply {
            this.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    foregroundEvents.launch { slidingImageAdapter.transitionToNextSlide() }
                }
            }, AUTOMATIC_SLIDE_INTERVAL, AUTOMATIC_SLIDE_INTERVAL)
        }
    }

    companion object {

        const val TAG = "SimplePresentFragment"
        private const val AUTOMATIC_SLIDE_INTERVAL = 20_000L
    }
}
