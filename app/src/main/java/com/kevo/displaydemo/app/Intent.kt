package com.kevo.displaydemo.app

// TODO: This is a work in progress ...
object Intent {

    object Extra {

        // Secondary Accessory (Customer Facing) Display
        /**
         * Start and bind the Accessory Display service to our application.
         */
        const val ACCESSORY_DISPLAY_START = "accessory_display_start"
    }

    object Action {

        const val STOP_SERVICE = "stop_service"
    }

    object Builder {
    }
}
