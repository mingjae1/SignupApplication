# 🎓 수강신청 시스템 (Course Registration System)

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-blue.svg)](https://gradle.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![FlatLaf](https://img.shields.io/badge/FlatLaf-3.6.2-blueviolet.svg)](https://www.formdev.com/flatlaf/)
[![License](https://img.shields.io/badge/License-Educational-green.svg)](LICENSE)

> 명지대학교 수강신청 시스템을 모델로 한 Java Swing 데스크톱 애플리케이션

## 📖 소개

Java Swing과 MySQL을 활용한 **완전 기능 수강신청 시스템**입니다. MVC 패턴과 DAO 패턴을 적용하여 깔끔한 코드 구조와 높은 유지보수성을 제공합니다.

### 🌟 주요 특징

- ✨ **모던 UI/UX** - FlatLaf 테마로 깔끔하고 세련된 인터페이스
- 🏗️ **체계적 설계** - MVC와 DAO 패턴으로 관심사 분리
- 🔐 **보안 강화** - 비밀번호 정책, SQL 인젝션 방지, 세션 관리
- 📊 **품질 관리** - SonarCloud와 GitHub Actions 연동
- 🎨 **테마 전환** - 라이트/다크 모드 실시간 전환
- 📱 **반응형 UI** - 화면별 최적화된 창 크기

## 📑 목차

- [소개](#-소개)
- [빠른 시작](#-빠른-시작)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [설치 및 실행](#-설치-및-실행)
- [사용 방법](#-사용-방법)
- [테스트 계정](#-테스트-계정)
- [보안 기능](#-보안-기능)
- [문제 해결](#-문제-해결)
- [향후 계획](#-향후-계획)
- [기여하기](#-기여하기)

---

## ⚡ 빠른 시작

```bash
# 1. 저장소 클론
git clone https://github.com/mingjae1/SignupApplication.git
cd SignupApplication/javasignupproject

# 2. MySQL 데이터베이스 설정
mysql -u root -p
CREATE DATABASE lms_data;
USE lms_data;
source LMS_data.sql;

# 3. 데이터베이스 연결 설정
echo "db.password=YOUR_MYSQL_PASSWORD" > config.properties

# 4. 애플리케이션 실행
./gradlew run
```

---

## 🎯 주요 기능

### 👤 사용자 기능

#### 📝 회원가입
- 연쇄 콤보박스로 캠퍼스 → 단과대학 → 학과 선택
- 실시간 입력 검증 (이메일, 학번, 아이디, 비밀번호)
- 강력한 비밀번호 정책 (8-20자, 영문/숫자/특수문자 포함)
- 아이디 및 학번 중복 검사
- ReDoS 공격 방지

#### 🔐 로그인/로그아웃
- DB 기반 인증 시스템
- 역할 기반 접근 제어 (학생/관리자)
- Enter 키 지원
- 세션 관리 및 보안 처리

#### 👨‍🎓 내 정보
- 학적 사항 조회 (이름, 학번, 소속 등)
- HTML 포맷 팝업 표시

### 📚 수강 관리

#### 🔍 강좌 검색
- 캠퍼스별 자동 필터링
- 단과대학/학과별 다단계 필터링
- 키워드 검색 (강좌명)
- 실시간 테이블 업데이트

#### ✅ 수강신청
- 검색 결과에서 즉시 신청
- 최대 18학점 자동 검증
- 중복 신청 방지
- 총 학점 자동 계산
- 신청 취소 기능

#### 📌 미리담기
- 관심 강좌 임시 저장
- 수강신청으로 일괄 전환
- 독립적인 학점 계산
- 목록 관리 기능

#### 📅 시간표
- 월~금, 1~9교시 그리드 표시
- 강좌별 색상 구분
- 수강신청/미리담기 모드 전환
- PNG 이미지 저장 기능

### 🔧 관리자 기능

#### 📊 강의 관리
- 전체 강의 목록 조회
- 강의 추가/수정/삭제 (CRUD)
- 학과 목록 조회
- 입력 폼 자동 채우기

#### 👥 권한 관리
- 자동 역할 인식
- 관리자 메뉴 표시

### 🎨 UI/UX

#### 🧭 네비게이션
- 히스토리 기반 이전/다음 버튼
- 햄버거 메뉴 사이드바
- 새로고침 기능

#### 🌓 테마
- 라이트/다크 모드 전환
- FlatLaf 모던 디자인

#### 📱 반응형
- 화면별 최적화 크기
- 자동 중앙 정렬

---

## 💻 기술 스택

### 핵심 기술
| 분류 | 기술 | 버전 | 설명 |
|------|------|------|------|
| 언어 | Java | 17+ | 애플리케이션 개발 언어 |
| GUI | Java Swing | - | 데스크톱 UI 프레임워크 |
| 데이터베이스 | MySQL | 8.0+ | 관계형 데이터베이스 |
| 빌드 도구 | Gradle | 8.5 | 의존성 관리 및 빌드 |

### 주요 라이브러리
| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| MySQL Connector/J | 9.4.0 | MySQL 데이터베이스 연결 |
| FlatLaf | 3.6.2 | 모던 Look and Feel 테마 |
| BCrypt | 0.4 | 비밀번호 해싱 |
| JUnit 5 | 5.10.0 | 단위 테스트 |

### 아키텍처 패턴
- **MVC (Model-View-Controller)** - 프레젠테이션 로직 분리
- **DAO (Data Access Object)** - 데이터 접근 추상화
- **Template Method** - 공통 로직 재사용
- **Singleton** - 전역 상태 관리

---

## 📁 프로젝트 구조

```
javasignupproject/
├── src/
│   └── signup/
│       ├── RMain.java              # 애플리케이션 진입점
│       ├── model/                  # 데이터 모델
│       │   ├── MMain.java         # 전역 상태 관리
│       │   ├── MUser.java         # 사용자 DTO
│       │   ├── MLecture.java      # 강좌 DTO
│       │   └── ComboboxItem.java  # 콤보박스 아이템
│       ├── view/                   # UI 컴포넌트
│       │   ├── VMain.java         # 메인 프레임
│       │   ├── VLogin.java        # 로그인 화면
│       │   ├── VSignup.java       # 회원가입 화면
│       │   ├── VSearch.java       # 강좌 검색
│       │   ├── VRegister.java     # 수강신청 내역
│       │   ├── VPreRegister.java  # 미리담기 내역
│       │   ├── VSchedule.java     # 시간표
│       │   └── VAdmin.java        # 관리자 화면
│       ├── controller/             # 이벤트 처리
│       │   ├── CMain.java         # 메인 컨트롤러
│       │   ├── CLogin.java        # 로그인 컨트롤러
│       │   ├── CSignup.java       # 회원가입 컨트롤러
│       │   ├── CSearch.java       # 검색 컨트롤러
│       │   ├── CRegister.java     # 수강신청 컨트롤러
│       │   ├── CPreRegister.java  # 미리담기 컨트롤러
│       │   └── CAdmin.java        # 관리자 컨트롤러
│       ├── dao/                    # 데이터 접근 계층
│       │   ├── DAO.java           # DB 연결 기본 클래스
│       │   ├── UserDAO.java       # 사용자 데이터 접근
│       │   ├── LectureDAO.java    # 강좌 데이터 접근
│       │   └── SaveDAO.java       # 수강신청 데이터 접근
│       └── constants/              # 상수 정의
│           ├── ViewConstants.java
│           ├── PanelNames.java
│           └── StatusConstants.java
├── LMS_data.sql                    # 데이터베이스 스키마
├── build.gradle.kts                # Gradle 빌드 설정
└── config.properties              # DB 설정 (Git 제외)
```

---

## 🚀 설치 및 실행

### 사전 요구사항

- **JDK 17 이상** - [다운로드](https://adoptium.net/)
- **MySQL 8.0 이상** - [다운로드](https://dev.mysql.com/downloads/mysql/)
- **Gradle** (또는 Gradle Wrapper 사용)
- **IDE** (선택사항): Eclipse 또는 IntelliJ IDEA

### 설치 단계

#### 1. 저장소 클론

```bash
git clone https://github.com/mingjae1/SignupApplication.git
cd SignupApplication/javasignupproject
```

#### 2. 데이터베이스 설정

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성 및 데이터 임포트
CREATE DATABASE lms_data;
USE lms_data;
source LMS_data.sql;
exit;
```

#### 3. 데이터베이스 연결 설정

`javasignupproject` 디렉토리에 `config.properties` 파일을 생성하고 MySQL 비밀번호를 입력합니다:

```properties
db.password=YOUR_MYSQL_PASSWORD
```

> ⚠️ **보안 주의**: `config.properties`는 `.gitignore`에 포함되어 Git에 커밋되지 않습니다.

#### 4. 빌드 및 실행

**Gradle을 사용한 실행:**
```bash
./gradlew run           # Linux/Mac
gradlew.bat run         # Windows
```

**IDE에서 실행:**
1. Eclipse 또는 IntelliJ에서 `javasignupproject` 폴더를 Gradle 프로젝트로 임포트
2. `src/signup/RMain.java` 실행

---

## 📖 사용 방법

### 1. 로그인
- 테스트 계정으로 로그인하거나 신규 회원가입
- Enter 키로 빠른 로그인

### 2. 강좌 검색
- 좌측 사이드바에서 "강좌 검색" 선택
- 단과대학/학과 필터 또는 키워드 검색
- 검색 결과에서 원하는 강좌 선택

### 3. 수강신청
- 강좌 선택 후 "수강신청" 또는 "미리담기" 버튼 클릭
- "수강신청 내역"에서 신청 현황 확인
- 필요시 신청 취소

### 4. 시간표 확인
- 사이드바에서 "시간표" 선택
- 수강신청/미리담기 모드 선택
- 이미지로 저장 가능

### 5. 관리자 기능 (관리자 계정)
- 사이드바에서 "관리자" 메뉴 선택
- 강의 추가/수정/삭제

---

## 🧪 테스트 계정

### 일반 사용자
```
아이디: test
비밀번호: 1234
소속: 자연캠퍼스 > 공과대학 > 컴퓨터공학과
```

### 관리자 계정 생성 (선택사항)
MySQL에서 직접 추가:
```sql
INSERT INTO user (userid, name, code, email, campus_id, college_id, department_id, role) 
VALUES ('admin', '관리자', 99999999, 'admin@mju.ac.kr', 1, 11, 112, 'admin');

INSERT INTO login (userId, password) 
VALUES ('admin', 'Admin123!@#');
```

---

## 🔒 보안 기능

### 비밀번호 보안
- 강력한 비밀번호 정책 (8-20자, 영문/숫자/특수문자)
- 메모리 보안 (`char[]` 배열 즉시 초기화)
- ReDoS 공격 방지 (미리 컴파일된 패턴)
- BCrypt 해싱 지원

### SQL 인젝션 방지
- PreparedStatement 사용
- 파라미터 바인딩
- 입력 검증

### 세션 및 접근 제어
- 중앙 집중식 세션 관리
- 역할 기반 접근 제어 (RBAC)
- 로그아웃 시 완전한 세션 초기화

---

## 📊 데이터베이스 구조

### ERD 다이어그램
```
campus (캠퍼스)
  ↓ 1:N
college (단과대학)
  ↓ 1:N
department (학과)
  ↓ 1:N
lecture (강좌)

user (사용자)
  ├─ → campus (FK)
  ├─ → college (FK)
  ├─ → department (FK)
  └─ 1:1 → login (인증)

save (수강신청)
  ├─ → user (FK)
  └─ → lecture (FK)
```

### 주요 테이블
- **campus** - 캠퍼스 정보
- **college** - 단과대학 정보
- **department** - 학과 정보
- **lecture** - 강좌 정보
- **user** - 사용자 정보
- **login** - 인증 정보
- **save** - 수강신청/미리담기

---

## 🔧 문제 해결

### MySQL 연결 실패
**증상**: `Access denied for user 'root'@'localhost'`

**해결**:
```bash
# config.properties 파일에 올바른 비밀번호 입력
echo "db.password=YOUR_ACTUAL_PASSWORD" > config.properties
```

### 데이터베이스 없음
**증상**: `Unknown database 'lms_data'`

**해결**:
```sql
CREATE DATABASE lms_data;
USE lms_data;
source LMS_data.sql;
```

### JDK 버전 오류
**증상**: `Unsupported class file major version`

**해결**:
```bash
# JDK 17+ 설치 확인
java -version

# 환경변수 설정 (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-17

# 환경변수 설정 (Linux/Mac)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Gradle 빌드 실패
**증상**: `Could not resolve all dependencies`

**해결**:
```bash
./gradlew clean build --refresh-dependencies
```

### config.properties 누락
**증상**: `FileNotFoundException: config.properties`

**해결**:
```bash
cd javasignupproject
echo "db.password=your_password" > config.properties
```

---

## 🚀 향후 계획

### 계획된 기능
- [ ] 비밀번호 암호화 강화 (BCrypt 전면 적용)
- [ ] 수강 인원 제한 기능
- [ ] 수강 정정 기간 설정
- [ ] 강좌 평가 및 후기 시스템
- [ ] 졸업 요건 확인 기능
- [ ] 성적 조회 및 관리
- [ ] 이메일 인증 시스템
- [ ] 비밀번호 찾기 기능
- [ ] 다국어 지원 (i18n)
- [ ] 모바일 반응형 웹 버전

### 개선 사항
- [ ] 단위 테스트 커버리지 확대
- [ ] 코드 문서화 강화
- [ ] 성능 최적화
- [ ] 접근성 개선

---

## 👥 기여하기

### 버그 리포트
[GitHub Issues](https://github.com/mingjae1/SignupApplication/issues)에 다음 정보와 함께 제보해 주세요:
- 버그 설명 및 재현 방법
- 예상 동작 vs 실제 동작
- 환경 정보 (OS, Java 버전, MySQL 버전)

### 기능 제안
Issues에 `enhancement` 라벨로 새 기능을 제안해 주세요.

### Pull Request
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 코드 스타일
- Java 코딩 컨벤션 준수
- 의미 있는 변수명 및 메서드명
- 복잡한 로직에 주석 추가
- 새 기능에 대한 테스트 작성

---

## 📄 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

---

## 📧 문의

프로젝트 관련 문의사항은 [GitHub Issues](https://github.com/mingjae1/SignupApplication/issues)를 통해 남겨주세요.

---

## 🙏 감사의 말

이 프로젝트는 명지대학교 수강신청 시스템을 참고하여 제작되었습니다.

---

<div align="center">

**⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요! ⭐**

Made with ❤️ by [mingjae1](https://github.com/mingjae1)

</div>
