# 파이썬 QR 스캐너 사용 가이드 (Python QR Scanner Guide)

## 1. 라이브러리 설치 (Install Dependencies)
이 프로그램은 강력한 스캔 기능을 위해 `pyzbar`를, 모던한 UI를 위해 `customtkinter`를 사용합니다.

**Windows 사용자 주의사항:**
`pyzbar` 실행 시 오류가 발생한다면 **Visual C++ Redistributable** 설치가 필요할 수 있습니다.

터미널에서 아래 명령어를 실행하세요:
```bash
pip install -r requirements.txt
```

## 2. 프로그램 실행 (Run the Application)
```bash
python qr_scanner.py
```

## 주요 기능 (Features)
*   **모던 다크 UI**: 깔끔하고 반응이 빠른 다크 모드 인터페이스.
*   **고성능 스캔**: 스캔 로직이 별도 스레드에서 돌아가므로 화면이 멈추지 않습니다.
*   **강력한 인식률**: OpenCV 기본 감지기보다 더 빠르고 정확한 `pyzbar` 엔진을 사용합니다.
*   **카메라 선택**: 왼쪽 상단 메뉴에서 웹캠(Camera 0, 1...)을 선택/변경할 수 있습니다.
*   **편리한 동작**: 내용 자동 복사, 웹 링크 자동 열기 기능을 지원합니다.
*   **QR 생성**: 나만의 QR 코드를 만들고, 색상을 바꾸거나 로고를 넣을 수 있습니다.

## 문제 해결 (Troubleshooting)
*   **`ImportError: DLL load failed`**: Visual C++ 재배포 패키지가 없는 경우 발생합니다. 마이크로소프트 공식 홈페이지에서 "vcredist_x64.exe"를 다운로드하여 설치하세요.
*   **카메라가 나오지 않음**: 줌(Zoom)이나 다른 프로그램이 카메라를 사용 중인지 확인하세요.
