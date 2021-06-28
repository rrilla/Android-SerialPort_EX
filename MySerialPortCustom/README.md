# Android-SerialPort_EX
https://github.com/mik3y/usb-serial-for-android#readme

1. 라이브러리 추가
 - root build.gradle:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
 - Add library to dependencies
 ```
 dependencies {
    implementation 'com.github.mik3y:usb-serial-for-android:3.4.0'
}
 ```
2. 장치 연결될 때 알림 보낼경우
 - xml/device_filter.xml추가
 - AndroidManifest.xml
 ```
 <activity
    android:name="..."
    ...>
    <intent-filter>
        <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    </intent-filter>
    <meta-data
        android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
        android:resource="@xml/device_filter" />
</activity>
 ```
