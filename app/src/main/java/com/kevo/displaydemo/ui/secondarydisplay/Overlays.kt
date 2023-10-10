package com.kevo.displaydemo.ui.secondarydisplay

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.kevo.displaydemo.R
import com.kevo.displaydemo.databinding.CustomerPresentationScreenBinding
import com.kevo.displaydemo.databinding.OverlayCustomerOrderTotalBinding
import com.kevo.displaydemo.databinding.OverlayLoyaltyPhoneBinding
import com.kevo.displaydemo.databinding.OverlayReceiptSelectionBinding
import com.kevo.displaydemo.util.SnackbarHelper

/**
 * Interactive components that overlay static background images in our [ViewPager2].
 */
class Overlays(binding: CustomerPresentationScreenBinding) {

    val customerOrderTotal: OverlayCustomerOrderTotalBinding =
            OverlayCustomerOrderTotalBinding.bind(binding.overlays)
    private val loyaltyPhone: OverlayLoyaltyPhoneBinding =
            OverlayLoyaltyPhoneBinding.bind(binding.overlays)
    private val receiptSelection: OverlayReceiptSelectionBinding =
            OverlayReceiptSelectionBinding.bind(binding.overlays)

    private val customerOrderTotalContainer: ViewGroup =
            binding.overlays.findViewById(R.id.customer_order_total)
    private val loyaltyPhoneContainer: ViewGroup =
            binding.overlays.findViewById(R.id.loyalty_phone)
    private val receiptSelectionContainer: ViewGroup =
            binding.overlays.findViewById(R.id.receipt_selection)

    fun hideAll() {
        customerOrderTotalContainer.isVisible = false
        loyaltyPhoneContainer.isVisible = false
        receiptSelectionContainer.isVisible = false
    }

    fun showCustomerOrderTotal() {
        loyaltyPhoneContainer.isVisible = false
        receiptSelectionContainer.isVisible = false
        customerOrderTotalContainer.isVisible = true
    }

    fun showLoyaltyPhone() {
        customerOrderTotalContainer.isVisible = false
        receiptSelectionContainer.isVisible = false
        loyaltyPhoneContainer.isVisible = true
    }

    fun showReceiptSelection() {
        customerOrderTotalContainer.isVisible = false
        loyaltyPhoneContainer.isVisible = false
        receiptSelectionContainer.isVisible = true
    }

    fun setupCustomerOrderTotal(touchListener: View.OnTouchListener) {
        customerOrderTotalContainer.setOnTouchListener(touchListener)

        customerOrderTotal.btnTotal.setOnClickListener {
            SnackbarHelper.showLong(it, "This is the Order Total")
        }
        customerOrderTotal.btnAnnouncement.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "Merchants can upload their own image or this can be an integration to Google Ads as a revenue stream"
            )
        }
        customerOrderTotal.btnCart.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "The cart would mirror the contents that the merchant is actively adding to the customer's order"
            )
        }
    }

    fun setupLoyaltyPhone(touchListener: View.OnTouchListener) {
        loyaltyPhoneContainer.setOnTouchListener(touchListener)

        loyaltyPhone.btnLoyaltyGraphic.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "Loyalty reward concepts could be prominently displayed"
            )
        }
        loyaltyPhone.btnKeypad.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "Customers can enter their phone number to join the loyalty program and redeem rewards"
            )
        }
    }

    fun setupReceiptSelection(touchListener: View.OnTouchListener) {
        receiptSelectionContainer.setOnTouchListener(touchListener)

        receiptSelection.btnPrint.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "Printing your receipt now ... (just kidding)"
            )
        }
        receiptSelection.btnEmail.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "The receipt has been emailed to you ... (just kidding)"
            )
        }
        receiptSelection.btnSms.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "The receipt will be texted to you soon ... (just kidding)"
            )
        }
        receiptSelection.btnQrCode.setOnClickListener {
            SnackbarHelper.showLong(
                it,
                "Scan the QR Code with your phone to view the receipt!"
            )
        }
    }
}
