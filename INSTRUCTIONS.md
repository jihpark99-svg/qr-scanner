# QR Code Scanner Setup Instructions

Since I couldn't find the Android project structure files automatically, please manually apply the following settings to your project.

## 1. Add Dependencies (`build.gradle.kts` : Module level)

Add the following libraries to the `dependencies` block in your module's `build.gradle.kts` file:

```kotlin
dependencies {
    // ... other dependencies

    // CameraX
    val camerax_version = "1.3.1"
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // Icons (if you use extended icons like 'ContentCopy')
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
}
```

## 2. Add Permissions (`AndroidManifest.xml`)

Add the camera permission inside the `<manifest>` tag, above the `<application>` tag:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android" ...>

    <!-- Add this line -->
    <uses-permission android:name="android.permission.CAMERA" />

    <application ...>
        ...
    </application>
</manifest>
```

## 3. MainActivity.kt

I have created a `MainActivity.kt` file in this directory with the complete, working code. You can copy its content to your project's `MainActivity.kt`.
