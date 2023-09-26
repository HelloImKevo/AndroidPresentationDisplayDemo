package com.kevo.displaydemo.util

import android.os.Build

object DeviceHelper {

    /**
     * Examples:
     * PAX/A920
     * LANDI/APOS A8OVS
     * LANDI/DX8000
     * Elo Touch Solutions/Elo-PP-15
     * Elo Touch Solutions/15in-I-Series-4-USBC-V
     */
    @JvmStatic
    fun generateDeviceFamilyIdentifier() = "${Build.MANUFACTURER}/${Build.MODEL}"

    @JvmStatic
    fun isEloZ30(): Boolean = generateDeviceFamilyIdentifier().equals(
        "Elo Touch Solutions/15in-I-Series-4-USBC-V",
        ignoreCase = true
    )
}
