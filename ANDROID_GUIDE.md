# 안드로이드 QR 스캐너 설정 가이드 (Android QR Scanner Setup Guide)

이 안드로이드 앱의 모든 기능을 실행하려면, **모듈 수준**의 `build.gradle.kts` 파일(보통 `app/build.gradle.kts`)에 아래 의존성(Dependencies)을 추가해야 합니다.

## 1. 의존성 추가 (Add Dependencies)

기존 CameraX 및 ML Kit 라이브러리에 더해, QR 코드 생성을 위한 `zxing-core`를 추가하세요.

```kotlin
dependencies {
    // ... 기존 의존성 ...

    // CameraX (카메라 기능)
    val camerax_version = "1.3.1"
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    // ML Kit Barcode Scanning (QR 코드 인식)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // ZXing (QR 코드 생성용)
    implementation("com.google.zxing:core:3.5.2")

    // Icons (확장 아이콘)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    
    // Coil (선택 사항: 이미지를 쉽게 로드하려면 사용 가능, 현재 코드는 기본 Bitmap 사용)
    // implementation("io.coil-kt:coil-compose:2.5.0") 
}
```

## 2. 프로젝트 동기화 (Sync Project)
Android Studio 상단에 나타나는 **"Sync Now"** 버튼을 클릭하여 새로운 라이브러리를 다운로드하고 적용하세요.
