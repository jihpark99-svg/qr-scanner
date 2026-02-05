# 📷 Smart QR Code Scanner Project

이 프로젝트는 **웹 애플리케이션 (메인)**, **Python (데스크탑용)**, **Kotlin (안드로이드용)** 세 가지 버전의 QR 코드 스캐너 구현체를 포함하고 있습니다.

---

## 🌐 0. Web Application Version (Main) ⭐

**브라우저에서 바로 실행 가능한 모던 QR 스캐너 & 생성기**

### ✨ 주요 기능 (Key Features)
- **📷 실시간 QR 스캔**: WebRTC를 통한 카메라 접근으로 실시간 QR 코드 인식 (배터리 최적화 적용)
- **✨ 커스텀 QR 생성**: 색상 변경, 로고 삽입, 고해상도 렌더링 지원
- **🏗️ 모듈형 구조**: CSS, JS가 분리되어 유지보수가 용이함
- **🛡️ 오프라인 지원**: 모든 라이브러리가 로컬에 내장되어 인터넷 없이도 동작
- **🔒 보안**: HTTPS/localhost 보안 컨텍스트 체크 및 에러 핸들링

### 📂 프로젝트 구조
```
/
├── css/             # 스타일시트 (style.css)
├── js/              # 로직 파일
│   ├── app.js       # 메인 엔트리 포인트
│   ├── scanner.js   # 스캐너 클래스
│   ├── generator.js # 생성기 클래스
│   └── lib/         # 로컬 라이브러리 (jsQR, qrcode)
└── index.html       # 메인 페이지
```

### 🚀 빠른 시작
**중요**: 브라우저 보안 정책으로 인해 `index.html`을 직접 열면 카메라가 작동하지 않습니다. 반드시 로컬 서버를 통해 실행하세요.

1. **로컬 테스트**:
   ```bash
   # 프로젝트 폴더에서 터미널 열기
   python -m http.server 8080
   # 브라우저에서 접속
   # 👉 http://localhost:8080/index.html
   ```

2. **Vercel 배포** (추천):
   - [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) 가이드 참조
   - GitHub 저장소와 Vercel 연동 시 자동 배포됩니다.

### 🌍 라이브 데모
> 배포 후 URL을 여기에 추가하세요

### 🌐 브라우저 호환성
- ✅ Chrome/Edge (권장)
- ✅ Firefox
- ✅ Safari (iOS 11.3+)
- ✅ 모바일 삼성 인터넷, 네이버 앱 등



---

## 🐍 1. Python Desktop Version (Reference)

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
