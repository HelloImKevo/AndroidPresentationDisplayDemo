package com.kevo.displaydemo.ui.carousel

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import com.kevo.displaydemo.databinding.FragmentCarouselBinding
import com.kevo.displaydemo.ui.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class CarouselFragment : BaseFragment() {

    private var _binding: FragmentCarouselBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: CarouselViewModel
    private lateinit var carousel: Carousel

    private val foregroundEvents: CoroutineScope = MainScope()
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CarouselViewModel::class.java)

        _binding = FragmentCarouselBinding.inflate(inflater, container, false)

        setupCarousel()

        binding.btnShowFullScreenImage.setOnClickListener {
            showFullScreenImage()
        }

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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCarousel() = with(binding) {
        this@CarouselFragment.carousel = this.carousel

        carousel.setAdapter(object : Carousel.Adapter {
            override fun count(): Int {
                return viewModel.images.size
            }

            override fun populate(view: View, index: Int) {
                if (view is ImageView) {
                    view.setImageResource(viewModel.images[index])
                } else {
                    Log.w(TAG, "Type '${view::class.java}' is currently not supported ¯\\_(ツ)_/¯")
                }
            }

            override fun onNewItem(index: Int) {}
        })

        this.imageView0.setOnTouchListener(touchListener)
        this.imageView1.setOnTouchListener(touchListener)
        this.imageView2.setOnTouchListener(touchListener)
        this.imageView3.setOnTouchListener(touchListener)
        this.imageView4.setOnTouchListener(touchListener)
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
                    foregroundEvents.launch { transitionToNextCarouselImage() }
                }
            }, CAROUSEL_AUTOMATIC_SLIDE_INTERVAL, CAROUSEL_AUTOMATIC_SLIDE_INTERVAL)
        }
    }

    @MainThread
    private fun transitionToNextCarouselImage() {
        if (carousel.currentIndex >= carousel.count - 1) {
            carousel.transitionToIndex(0, ANIMATION_DURATION)
        } else {
            carousel.transitionToIndex(carousel.currentIndex + 1, ANIMATION_DURATION)
        }
    }

    companion object {

        private const val TAG = "CarouselFragment"
        private const val CAROUSEL_AUTOMATIC_SLIDE_INTERVAL = 5_000L
        private const val ANIMATION_DURATION: Int = 1_000
    }
}
