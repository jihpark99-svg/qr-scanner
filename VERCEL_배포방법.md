# 🚀 Vercel 배포 방법 (간단 가이드)

## ✅ 1단계: GitHub 푸시 완료!
현재 상태: **완료됨** ✅
- 저장소: `jihpark99-svg/qr-scanner`
- 모든 파일 푸시 완료

---

## 🌐 2단계: Vercel 배포하기

### 방법 A: Vercel 웹사이트 (가장 간단) 👈 **추천**

#### 1. Vercel 계정 생성
1. https://vercel.com 방문
2. **"Sign Up"** 클릭
3. **"Continue with GitHub"** 선택
4. GitHub 계정으로 로그인

#### 2. 프로젝트 배포
1. Vercel 대시보드에서 **"Add New..."** 버튼 클릭
2. **"Project"** 선택
3. **Import Git Repository** 섹션에서:
   - `jihpark99-svg/qr-scanner` 저장소 찾기
   - **"Import"** 클릭

#### 3. 프로젝트 설정 (중요!)
이 설정 화면에서:
- **Framework Preset**: `Other` 선택
- **Root Directory**: 비워두기 (기본값)
- **Build Command**: **비워두기** (정적 HTML이므로 빌드 불필요)
- **Output Directory**: 비워두기
- **Install Command**: 비워두기

#### 4. 배포!
- **"Deploy"** 버튼 클릭
- 1-2분 대기 ⏳
- 배포 완료! 🎉

#### 5. 배포 URL 확인
- 자동 생성된 URL (예: `qr-scanner-xxxxx.vercel.app`)
- 클릭해서 접속!

---

### 방법 B: Vercel CLI (고급 사용자용)

```bash
# 1. Vercel CLI 설치
npm install -g vercel

# 2. 로그인
vercel login

# 3. 배포
cd "d:\공부\QR Code"
vercel

# 4. 프로덕션 배포
vercel --prod
```

---

## ✅ 배포 후 확인사항

### 1. 카메라 권한
- ✅ HTTPS에서만 카메라 작동 (Vercel이 자동 제공)
- 브라우저에서 카메라 권한 허용 필요

### 2. 기능 테스트
- [ ] QR 스캔 기능
- [ ] QR 생성 기능
- [ ] 색상 변경
- [ ] 로고 추가
- [ ] 다운로드

---

## 🔄 업데이트 방법

코드 수정 후 자동 재배포:

```bash
git add .
git commit -m "Update features"
git push
```

Vercel이 **자동으로 감지하고 재배포**합니다! 🎉

---

## 🎨 추가 설정 (선택사항)

### 커스텀 도메인 추가
1. Vercel 프로젝트 → **Settings** → **Domains**
2. 도메인 입력
3. DNS 설정 안내에 따라 설정

---

## ⚡ 빠른 시작 (3단계로 끝)

1. https://vercel.com 방문 → GitHub 로그인
2. `qr-scanner` 프로젝트 Import
3. Deploy 클릭!

**완료! 전 세계에서 QR 스캐너 사용 가능! 🌍**

---

## 📱 배포 후 URL 예시

```
https://qr-scanner-jihpark99.vercel.app
```

이 URL을 친구들과 공유하세요! 🚀
