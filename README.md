# AndroidPresentationDisplayDemo
Demo app for the Android Presentation Display component.

## How to Grab a Screenshot from the Secondary Display
You can get the "Display ID" using:
```shell
adb shell dumpsys display
```
And then look for a display labeled like "HDMI Screen". The Display ID is the
numeric value of the `uniqueId` property in most cases:
```
DisplayDeviceInfo{"HDMI Screen": uniqueId="local:1", 1280 x 800, modeId 2, 
defaultModeId 2, supportedModes [{id=2, width=1280, height=800, fps=60.000004}], 
colorMode 0, supportedColorModes [0], HdrCapabilities android.view.Display$HdrCapabilities@40f16308, 
density 237, 237.0 x 237.0 dpi, appVsyncOff 1000000, presDeadline 16666666, 
touch EXTERNAL, rotation 0, type HDMI, address {port=1}, state ON, 
FLAG_SECURE, FLAG_SUPPORTS_PROTECTED_BUFFERS, FLAG_PRESENTATION}
```

Then you use the `-d` option in `adb shell screencap`:
```shell
adb shell screencap -d 1 /sdcard/screen-01.png && adb pull /sdcard/screen-01.png ~/Desktop/
```

The same concept should apply to `screenrecord`, but this is only available on
**Android 11+**. Android developer documentation:  
https://developer.android.com/tools/adb#screencap  
```shell
adb shell screenrecord --display-id 1 /sdcard/recording-01.mp4
adb pull /sdcard/recording-01.mp4 ~/Desktop/
```

Stack Overflow post:  
https://stackoverflow.com/questions/64180082/adb-screenrecord-secondary-display-from-listed-displays-in-dumpsys  

Link to `screenrecord` source code:  
https://android.googlesource.com/platform/frameworks/av/+/refs/heads/master/cmds/screenrecord/screenrecord.cpp  
