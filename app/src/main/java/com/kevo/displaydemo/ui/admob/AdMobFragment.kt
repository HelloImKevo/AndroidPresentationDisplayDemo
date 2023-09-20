package com.kevo.displaydemo.ui.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kevo.displaydemo.R
import com.kevo.displaydemo.databinding.FragmentAdmobBinding
import java.util.Locale

// Remove the line below after defining your own ad unit ID.
private const val TOAST_TEXT = "Test ads are being shown. " +
        "To show live ads, replace the ad unit ID in res/values/strings.xml " +
        "with your own ad unit ID."
private const val START_LEVEL = 1

class AdMobFragment : Fragment() {

    private var _binding: FragmentAdmobBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var currentLevel: Int = 0
    private var interstitialAd: InterstitialAd? = null
    private lateinit var nextLevelButton: Button
    private lateinit var levelTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: Do something with it
        val viewModel =
            ViewModelProvider(this).get(AdMobViewModel::class.java)

        _binding = FragmentAdmobBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // AdMob initialization
        MobileAds.initialize(
            requireActivity()
        ) { }
        // Load the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        loadInterstitialAd()

        // Create the next level button, which tries to show an interstitial when clicked.
        nextLevelButton = binding.nextLevelButton
        nextLevelButton.isEnabled = false
        nextLevelButton.setOnClickListener { showInterstitial() }

        levelTextView = binding.level
        // Create the text view to show the level number.
        currentLevel = START_LEVEL

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(requireActivity(), TOAST_TEXT, Toast.LENGTH_LONG).show()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireActivity(),
            getString(R.string.interstitial_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    // The interstitialAd reference will be null until
                    // an ad is loaded.
                    interstitialAd = ad
                    nextLevelButton.isEnabled = true
                    Toast.makeText(requireActivity(), "onAdLoaded()", Toast.LENGTH_SHORT)
                        .show()
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            interstitialAd = null
                            Log.d(TAG, "The ad was dismissed.")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            interstitialAd = null
                            Log.d(TAG, "The ad failed to show.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            Log.d(TAG, "The ad was shown.")
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.message)
                    interstitialAd = null
                    nextLevelButton.isEnabled = true
                    val error: String = String.format(
                        Locale.ENGLISH,
                        "domain: %s, code: %d, message: %s",
                        loadAdError.domain,
                        loadAdError.code,
                        loadAdError.message
                    )
                    Toast.makeText(
                        requireActivity(),
                        "onAdFailedToLoad() with error: $error", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showInterstitial() {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (interstitialAd != null) {
            interstitialAd!!.show(requireActivity())
        } else {
            Toast.makeText(requireActivity(), "Ad did not load", Toast.LENGTH_SHORT).show()
            goToNextLevel()
        }
    }

    private fun goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
        levelTextView.text = "Level " + (++currentLevel)
        if (interstitialAd == null) {
            loadInterstitialAd()
        }
    }

    companion object {
        private const val TAG = "AdMobFragment"
    }
}
