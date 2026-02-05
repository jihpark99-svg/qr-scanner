# 📷 Smart QR Code Scanner Project

이 프로젝트는 **Python (데스크탑용)**과 **Kotlin (안드로이드용)** 두 가지 버전의 QR 코드 스캐너 구현체를 포함하고 있습니다. 현재 주력으로 개발된 버전은 **Python 버전**입니다.

---

## 🐍 1. Python Desktop Version (Main)

최신 라이브러리를 활용하여 개발된 고성능 윈도우 데스크탑용 QR 스캐너입니다.

### ✨ 주요 기능 (Key Features)
*   **고성능 인식**: `OpenCV` + `pyzbar` 조합으로 빠르고 정확한 QR 인식 지원.
*   **모던 UI**: `CustomTkinter` 기반의 세련된 다크 모드 인터페이스.
*   **하드웨어 제어**: 연결된 웹캠(Camera 0, 1, 2...)을 드롭다운 메뉴로 선택 및 전환 가능.
*   **스레드 최적화**: 스캔 로직을 별도 스레드로 분리하여 UI 끊김 현상 제거.
*   **사용자 편의**: 인식된 내용 자동 표시, 클립보드 복사, 웹 링크 자동 열기 지원.

### 🚀 설치 및 실행 방법
1.  **필수 라이브러리 설치**:
    ```bash
    pip install -r requirements.txt
    ```
2.  **프로그램 실행**:
    ```bash
    python qr_scanner.py
    ```

---

## 📱 2. Android Version (Reference)

Jetpack Compose와 Google ML Kit를 사용한 안드로이드 앱 소스 코드입니다.

### 📂 파일 정보
*   **`MainActivity.kt`**: 카메라 권한 처리, CameraX 프리뷰, ML Kit QR 분석기, BottomSheet UI가 모두 포함된 단일 파일 소스입니다.
*   **`INSTRUCTIONS.md`**: 안드로이드 프로젝트 설정(Gradle 의존성, Manifest 권한 등) 방법이 적혀 있습니다.

### 🛠️ 사용 방법
1.  Android Studio에서 'Empty Activity' 프로젝트 생성.
2.  `MainActivity.kt` 내용을 덮어쓰기.
3.  `INSTRUCTIONS.md`를 참고하여 `build.gradle.kts`와 `AndroidManifest.xml` 설정.
4.  빌드 및 실행.

---

## 📝 라이센스 (License)
이 프로젝트는 교육 및 참고 목적으로 작성되었습니다. 자유롭게 수정하여 사용하세요.
