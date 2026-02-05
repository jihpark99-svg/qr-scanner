# 🚀 Vercel 배포 가이드

이 가이드는 QR 스캐너 & 생성기 웹 앱을 Vercel에 배포하는 방법을 단계별로 설명합니다.

## 📋 준비사항

- GitHub 계정
- Vercel 계정 (무료) - https://vercel.com

---

## 🔧 1단계: GitHub 저장소 준비

### 옵션 A: 기존 저장소 사용
이미 GitHub에 코드를 올렸다면 이 단계를 건너뛰세요.

### 옵션 B: 새 저장소 생성

1. **GitHub에서 새 저장소 생성**
   - https://github.com/new 방문
   - Repository name 입력 (예: `qr-scanner-web`)
   - Public 선택
   - "Create repository" 클릭

2. **로컬 프로젝트를 GitHub에 푸시**

```bash
# Git 초기화 (아직 안했다면)
git init

# 파일 추가
git add index.html vercel.json README.md

# 커밋
git commit -m "Add QR Scanner Web App"

# GitHub 저장소 연결 (YOUR_USERNAME을 실제 사용자명으로 변경)
git remote add origin https://github.com/YOUR_USERNAME/qr-scanner-web.git

# 푸시
git branch -M main
git push -u origin main
```

---

## 🌐 2단계: Vercel에 배포

### 방법 1: Vercel 웹사이트 사용 (추천)

1. **Vercel 로그인**
   - https://vercel.com 방문
   - "Sign Up" → GitHub로 로그인

2. **새 프로젝트 생성**
   - 대시보드에서 "Add New..." → "Project" 클릭
   - GitHub 저장소 목록에서 `qr-scanner-web` 선택
   - "Import" 클릭

3. **프로젝트 설정**
   - **Framework Preset**: "Other" 선택
   - **Root Directory**: 그대로 두기
   - **Build Command**: 비워두기 (정적 HTML이므로 빌드 불필요)
   - **Output Directory**: 비워두기
   - "Deploy" 클릭

4. **배포 완료!** 🎉
   - 1-2분 후 배포 완료
   - 자동 생성된 URL 확인 (예: `qr-scanner-web.vercel.app`)

### 방법 2: Vercel CLI 사용

```bash
# Vercel CLI 설치
npm install -g vercel

# 로그인
vercel login

# 배포
vercel

# 프로덕션 배포
vercel --prod
```

---

## ✅ 3단계: 확인 및 테스트

1. **배포된 사이트 접속**
   - Vercel이 제공한 URL 방문
   - 예: `https://your-project.vercel.app`

2. **기능 테스트**
   - ✅ 카메라 권한 허용 (HTTPS에서만 작동)
   - ✅ QR 코드 스캔 테스트
   - ✅ QR 코드 생성 테스트
   - ✅ 색상 변경 및 로고 추가 테스트

---

## 🔄 4단계: 업데이트 배포

코드를 수정한 후 자동 배포:

```bash
# 변경사항 커밋
git add .
git commit -m "Update features"

# GitHub에 푸시
git push

# Vercel이 자동으로 재배포합니다!
```

---

## 🎨 5단계: 커스텀 도메인 설정 (선택사항)

1. Vercel 프로젝트 설정으로 이동
2. "Domains" 탭 클릭
3. 도메인 입력 및 DNS 설정
4. 완료!

---

## ⚠️ 문제 해결

### 카메라가 작동하지 않는 경우
- **원인**: HTTP에서는 카메라 접근 불가
- **해결**: HTTPS URL 사용 (Vercel은 기본적으로 HTTPS 제공)

### CORS 오류
- **원인**: 외부 리소스 로딩 문제
- **해결**: CDN 라이브러리 사용 (이미 적용됨)

### 로고 업로드가 안 되는 경우
- **원인**: 파일 크기 제한
- **해결**: 이미지 크기를 5MB 이하로 줄이기

---

## 📱 모바일 최적화

- 반응형 디자인 적용됨
- 모바일 브라우저에서도 카메라 사용 가능
- PWA로 설치 가능 (추가 설정 필요)

---

## 🌟 배포 후 체크리스트

- [ ] 데스크톱 브라우저에서 테스트
- [ ] 모바일 브라우저에서 테스트
- [ ] QR 스캔 기능 확인
- [ ] QR 생성 기능 확인
- [ ] 로고 추가 기능 확인
- [ ] 다운로드 기능 확인
- [ ] README 업데이트 (배포 URL 추가)

---

## 🔗 유용한 링크

- Vercel 문서: https://vercel.com/docs
- Vercel 대시보드: https://vercel.com/dashboard
- GitHub 저장소: https://github.com

---

축하합니다! 🎉 이제 전 세계 어디서나 QR 스캐너를 사용할 수 있습니다!
