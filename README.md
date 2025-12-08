# Java Swing 수강신청 프로그램 (Course Registration System)

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-blue.svg)](https://gradle.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Educational-green.svg)](LICENSE)

## 📖 프로젝트 소개

Java Swing과 MySQL을 사용하여 **MVC(Model-View-Controller)** 패턴 및 **DAO(Data Access Object)** 패턴을 기반으로 구현한 데스크톱 수강신청 프로그램입니다.

### 주요 특징
- ✨ **현대적인 UI/UX**: FlatLaf 테마를 적용하여 깔끔하고 모던한 인터페이스 제공
- 🏗️ **체계적인 아키텍처**: MVC 패턴과 DAO 패턴으로 관심사 분리 및 유지보수성 향상
- 🔐 **보안 강화**: 비밀번호 정책, SQL 인젝션 방지, 세션 관리 등 보안 기능 구현
- 📊 **코드 품질 관리**: SonarCloud와 GitHub Actions 연동으로 지속적인 품질 관리
- 🎨 **테마 지원**: 라이트/다크 모드 실시간 전환 지원
- 📱 **반응형 디자인**: 화면별 최적화된 창 크기 자동 조정

---

## 📑 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능-전체-구현-기능-목록)
  - [사용자 인증 및 계정 관리](#1-사용자-인증-및-계정-관리)
  - [강좌 검색 및 조회](#2-강좌-검색-및-조회-기능)
  - [수강신청 관리](#3-수강신청-관리)
  - [시간표 기능](#4-시간표-기능)
  - [관리자 기능](#5-관리자-기능)
  - [UI/UX 기능](#6-uiux-기능)
- [설치 및 실행 방법](#-설치-및-실행-방법)
- [테스트 계정](#-테스트-계정)
- [사용된 기술 스택](#-사용된-기술-스택)
- [프로젝트 아키텍처](#-프로젝트-아키텍처)
- [보안 기능](#-보안-기능)
- [향후 개선 가능 사항](#-향후-개선-가능-사항)

---

## 📸 스크린샷

> 💡 **참고**: 프로그램을 실행하면 다음과 같은 화면들을 확인할 수 있습니다.

### 로그인 화면
- 아이디/비밀번호 입력
- 회원가입 링크
- 엔터키로 빠른 로그인

### 강좌 검색 화면
- 캠퍼스/단과대학/학과별 필터링
- 키워드 검색
- 수강신청/미리담기 기능

### 시간표 화면
- 월~금 1~9교시 그리드
- 강좌별 색상 구분
- PNG 이미지로 저장 가능

---

## 🎯 주요 기능 (전체 구현 기능 목록)

### 1. 사용자 인증 및 계정 관리

#### 1.1 회원가입 (`VSignup` / `CSignup`)
* **연쇄 콤보박스 시스템:**
   * 캠퍼스 선택 → 단과대학 목록 자동 로드
   * 단과대학 선택 → 학과 목록 자동 로드
   * `LectureDAO`를 통한 DB 기반 동적 로딩
   * `ComboboxItem` 객체로 ID와 이름을 함께 관리
* **입력 필드 유효성 검사:**
   * 이름, 학번, 아이디, 이메일, 비밀번호, 소속 정보 필수 입력 검증
   * 이메일 형식 검증 (정규표현식)
   * 학번 형식 검증 (숫자 8자리)
   * 아이디 길이 제한 (3~15자)
   * 비밀번호 확인 일치 검사
* **비밀번호 보안 규칙:**
   * 길이 제한: 8~20자
   * 필수 포함 요소: 영문자, 숫자, 특수문자 각 1개 이상
   * 금지 요소: 한글, 공백
   * ReDoS(Regular Expression Denial of Service) 공격 방지를 위한 미리 컴파일된 패턴 사용
* **중복 검사:**
   * 아이디 중복 검사 (`UserDAO.isUserIdDuplicate`)
   * 학번 중복 검사 (`UserDAO.isStudentIdDuplicate`)
* **DB 정규화:**
   * `FOREIGN KEY`를 활용한 소속 정보 저장 (캠퍼스 ID, 단과대학 ID, 학과 ID)
   * 사용자 정보(`user`)와 인증 정보(`login`) 테이블 분리
* **비밀번호 메모리 보안:**
   * 처리 완료 후 `char[]` 배열 초기화로 메모리에서 비밀번호 제거

#### 1.2 로그인 (`VLogin` / `CLogin`)
* **DB 기반 인증:**
   * `UserDAO.validateUser`를 통한 아이디/비밀번호 검증
   * 세션 관리: `MMain.setCurrentUserId`로 로그인 상태 유지
* **역할 기반 접근 제어:**
   * 일반 사용자(학생) 모드
   * 관리자 모드 자동 인식 및 전환
* **엔터키 지원:**
   * 아이디/비밀번호 입력 필드에서 Enter 키로 로그인 가능
* **에러 처리:**
   * 로그인 실패 시 명확한 오류 메시지
   * DB 연결 오류 시 별도 안내
   * 로그 기록을 통한 디버깅 지원

#### 1.3 로그아웃 (`CMain`)
* **세션 초기화:**
   * 사용자 ID 제거
   * 사용자 이름 표시 초기화
   * 관리자 모드 해제
* **화면 초기화:**
   * 로그인 화면으로 복귀
   * 창 크기 복구 (420x320)
   * 네비게이션 히스토리 초기화

#### 1.4 내 정보 조회 (`CMain.handleMyInfo`)
* **학적 사항 팝업 표시:**
   * 이름, 학번, 아이디, 이메일
   * 소속 정보 (캠퍼스 / 단과대학 / 학과)
   * HTML 포맷으로 깔끔한 정보 표시

---

### 2. 강좌 검색 및 조회 기능

#### 2.1 강좌 검색 (`VSearch` / `CSearch`)
* **캠퍼스 자동 필터링:**
   * 로그인한 사용자의 소속 캠퍼스 강좌만 자동 표시
   * `UserDAO.getCampusIdByUserId`로 사용자 캠퍼스 ID 조회
* **다단계 필터링:**
   * 단과대학 선택 → 학과 목록 동적 로드
   * "전체" 옵션으로 광범위 검색 지원
* **키워드 검색:**
   * 강좌명 기반 검색
   * DB에서 `LIKE` 쿼리로 부분 일치 검색
* **검색 결과 테이블 표시:**
   * 강좌 코드, 강좌명, 교수명, 학점, 시간
   * `DefaultTableModel`을 사용한 동적 테이블 업데이트

#### 2.2 수강신청/미리담기 (`CSearch`)
* **테이블에서 강좌 선택 후 신청:**
   * "수강신청" 버튼: 즉시 수강신청 내역에 추가
   * "미리담기" 버튼: 미리담기 목록에 추가
* **학점 제한 검사:**
   * 최대 18학점 제한 자동 검증
   * 초과 시 명확한 오류 메시지
* **중복 방지:**
   * 이미 신청/담긴 강좌는 재신청 불가
   * DB의 `INSERT IGNORE` 사용
* **실시간 피드백:**
   * 성공, 학점 초과, 중복, DB 오류 등 상세한 결과 코드 반환

---

### 3. 수강신청 관리

#### 3.1 수강신청 내역 (`VRegister` / `CRegister`)
* **신청 목록 조회:**
   * 현재 로그인한 사용자의 수강신청 내역 표시
   * 강좌 코드, 강좌명, 교수명, 학점, 시간 표시
* **총 학점 계산 및 표시:**
   * 신청한 전체 강좌의 학점 합계 자동 계산
   * 화면 하단에 "총 학점: XX학점" 표시
* **신청 취소 기능:**
   * 테이블에서 강좌 선택 → "신청 취소" 버튼 클릭
   * 확인 다이얼로그 표시
   * `SaveDAO.removeLecture`로 DB에서 삭제
   * 삭제 후 자동 새로고침

#### 3.2 미리담기 내역 (`VPreRegister` / `CPreRegister`)
* **미리담기 목록 조회:**
   * 현재 로그인한 사용자의 미리담기 목록 표시
* **목록 삭제 기능:**
   * 선택한 강좌를 미리담기에서 제거
   * 수강신청 내역과 동일한 삭제 프로세스
* **수강신청 전환 기능:**
   * 미리담기 → 수강신청으로 직접 전환
   * 학점 검사 후 수강신청 테이블에 추가
   * 성공 시 미리담기 목록에서 자동 제거
   * DB 트랜잭션으로 데이터 일관성 보장
* **총 학점 계산 및 표시:**
   * 미리담기 강좌의 총 학점 표시

---

### 4. 시간표 기능

#### 4.1 시간표 조회 (`VSchedule` / `CSchedule`)
* **시각적 시간표 생성:**
   * 월~금, 1~9교시 그리드 형태로 표시
   * 강좌별 색상 구분
   * 강좌명, 교수명, 강의실 정보 표시
* **모드 선택 라디오 버튼:**
   * "수강신청" 시간표: 확정된 수강신청 내역 표시
   * "미리담기" 시간표: 미리담은 강좌 표시
* **동적 데이터 로딩:**
   * `SaveDAO.getLecturesByStatus`로 상태별 강좌 조회
   * 백그라운드 스레드에서 데이터 로드 (UI 블로킹 방지)
* **시간표 이미지 저장:**
   * "이미지 저장" 버튼으로 PNG 파일로 내보내기
   * `JFileChooser`를 통한 저장 위치 선택
   * `BufferedImage`로 시간표 패널 캡처
   * 기본 파일명: "내시간표.png"

---

### 5. 관리자 기능

#### 5.1 관리자 모드 접근 (`CLogin` / `CMain`)
* **자동 권한 인식:**
   * 로그인 시 사용자 역할(`role`) 확인
   * 관리자 계정 로그인 시 관리자 모드 활성화
   * 사이드바에 "관리자" 메뉴 자동 표시

#### 5.2 강의 관리 (`VAdmin` / `CAdmin`)
* **전체 강의 목록 조회:**
   * 모든 강좌 정보를 테이블로 표시
   * 강좌 코드, 강좌명, 교수명, 학점, 시간, 학과 ID
* **강의 추가:**
   * 입력 폼을 통한 새 강의 등록
   * 필수 입력: 강좌 코드(ID), 강좌명, 교수명, 학점, 시간, 학과 ID
   * 중복 ID 검사
   * `LectureDAO.insertLecture`로 DB 삽입
* **강의 수정:**
   * 테이블에서 강의 클릭 → 입력 폼에 자동 채우기
   * 정보 수정 후 "수정" 버튼 클릭
   * 강좌 코드(PK)는 수정 불가
   * `LectureDAO.updateLecture`로 DB 업데이트
* **강의 삭제:**
   * 테이블에서 강의 선택 → "삭제" 버튼
   * 확인 다이얼로그로 실수 방지
   * `LectureDAO.deleteLecture`로 DB 삭제
* **입력 폼 초기화:**
   * "초기화" 버튼으로 모든 입력 필드 비우기
* **학과 목록 조회:**
   * "학과 찾기" 버튼으로 전체 학과 목록 팝업 표시
   * `LectureDAO.getAllDepartments`로 조회
   * 학과 ID 참조를 위한 보조 기능

---

### 6. UI/UX 기능

#### 6.1 네비게이션 시스템 (`CMain`)
* **히스토리 기반 네비게이션:**
   * 이전/다음 버튼으로 화면 이동
   * `ArrayDeque`를 사용한 스택 기반 히스토리 관리
   * 버튼 활성화/비활성화 자동 제어
* **사이드바 메뉴:**
   * 햄버거 버튼으로 사이드바 토글
   * 강좌 검색, 수강신청 내역, 미리담기 내역 메뉴
   * 시간표, 내 정보, 관리자(권한 시), 테마 변경
* **새로고침 기능:**
   * 현재 화면의 데이터 재로드
   * 각 화면별 적절한 새로고침 로직 자동 실행

#### 6.2 테마 변경 (`CMain`)
* **라이트/다크 모드 전환:**
   * "테마 변경" 버튼으로 즉시 전환
   * FlatLaf의 `FlatLightLaf` / `FlatDarkLaf` 사용
   * `FlatLaf.updateUI()`로 전체 UI 즉시 반영
   * 설정 유지 없이 세션 동안만 적용

#### 6.3 반응형 창 크기
* **화면별 최적화된 크기:**
   * 로그인 화면: 420x320
   * 회원가입 화면: 800x600
   * 메인 화면(로그인 후): 1280x800
* **자동 중앙 정렬:**
   * 크기 변경 시 화면 중앙에 재배치

#### 6.4 사용자 정보 표시
* **헤더에 이름 표시:**
   * 로그인 후 상단에 사용자 이름 표시
   * `UserDAO.getUserInfo`로 조회
   * 로그아웃 시 자동 제거

---

### 7. 데이터 관리 및 보안

#### 7.1 DAO (Data Access Object) 패턴
* **UserDAO:**
   * 사용자 인증 (`validateUser`)
   * 사용자 정보 조회 (`getUserInfo`)
   * 사용자 등록 (`addUser`)
   * 중복 검사 (`isUserIdDuplicate`, `isStudentIdDuplicate`)
   * 캠퍼스 ID 조회 (`getCampusIdByUserId`)
* **LectureDAO:**
   * 강좌 검색 (`searchLectures`)
   * 강좌 CRUD (`getAllLectures`, `insertLecture`, `updateLecture`, `deleteLecture`)
   * 계층적 데이터 조회 (`getAllCampuses`, `getCollegesByCampus`, `getDepartmentsByCollege`)
   * 학과 목록 조회 (`getAllDepartments`)
* **SaveDAO:**
   * 수강신청/미리담기 추가 (`addLecture`)
   * 삭제 (`removeLecture`)
   * 상태별 강좌 목록 조회 (`getLecturesByStatus`)
   * 학점 계산 및 검증

#### 7.2 비즈니스 로직 검증
* **최대 학점 제한:**
   * `SaveDAO`에서 수강신청/미리담기 시 총 학점 검사
   * 상한선: 18학점
   * 초과 시 신청 차단
* **중복 방지:**
   * DB 레벨: `INSERT IGNORE` 사용
   * 복합 기본키 (`userid`, `lecture_id`, `status`)로 중복 원천 차단
* **트랜잭션 처리:**
   * 미리담기 → 수강신청 전환 시 원자성 보장
   * 실패 시 롤백으로 데이터 일관성 유지

#### 7.3 에러 처리 및 로깅
* **SQL 예외 처리:**
   * `SQLException` 캐치 및 사용자 친화적 메시지 표시
   * `Logger`를 통한 에러 로그 기록
* **입력 검증:**
   * 빈 값, 형식 오류 사전 차단
   * 명확한 오류 메시지로 사용자 가이드
* **결과 코드 시스템:**
   * 0: 성공
   * 1: 학점 초과
   * 2: 중복
   * -1: DB 오류

---

### 8. 데이터베이스 스키마

#### 8.1 테이블 구조
* **root (캠퍼스):**
   * id (PK), name, filename
   * 예: 자연캠퍼스, 인문캠퍼스
* **college (단과대학):**
   * id (PK), name, filename, root_id (FK)
   * 예: 공과대학, ICT융합대학
* **department (학과):**
   * id (PK), name, filename, college_id (FK)
   * 예: 컴퓨터공학과, 전기공학과
* **lecture (강좌):**
   * id (PK), name, professor, credit, time, department_id (FK)
* **user (사용자):**
   * userid (PK), name, code, email, campus_id (FK), college_id (FK), department_id (FK)
* **login (인증):**
   * userId (PK, FK), password
   * CASCADE 삭제/업데이트
* **save (수강신청/미리담기):**
   * userid (FK), lecture_id (FK), status
   * 복합 기본키 (userid, lecture_id, status)

#### 8.2 외래 키 관계
* 계층 구조: root → college → department → lecture
* 사용자 소속: user → root, college, department
* 수강 관계: save → user, lecture

---

### 9. 추가 기술 기능

#### 9.1 멀티스레딩
* **백그라운드 데이터 로딩:**
   * 회원가입 시 캠퍼스 목록 비동기 로드
   * 시간표 데이터 로딩 시 UI 블로킹 방지
   * `new Thread(() -> {...}).start()` 사용

#### 9.2 CardLayout 기반 화면 전환
* **메인 프레임 (`VMain`):**
   * 로그인/회원가입 패널 전환
   * 내부 콘텐츠 패널 전환 (검색, 신청, 미리담기)
* **부드러운 화면 전환:**
   * `contentPanel(String panelName)` 메서드로 통일된 인터페이스

#### 9.3 컴포넌트 재사용
* **CListController (추상 클래스):**
   * `CRegister`와 `CPreRegister`의 공통 로직 추출
   * 테이블 새로고침, 삭제 기능 공유
   * 템플릿 메서드 패턴 적용

#### 9.4 코드 품질 관리
* **SonarCloud 연동:**
   * GitHub Actions를 통한 자동 코드 분석
   * 코드 스멜, 버그, 보안 취약점 탐지
* **SonarLint:**
   * IDE에서 실시간 코드 품질 검사

---

## 📋 기능별 상세 동작 흐름

### 수강신청 프로세스
1. 로그인 → 2. 강좌 검색 → 3. 강좌 선택 → 4. 수강신청/미리담기 → 5. 내역 확인 → 6. 시간표 조회

### 관리자 강의 관리 프로세스
1. 관리자 로그인 → 2. 관리자 모드 진입 → 3. 강의 조회 → 4. 강의 추가/수정/삭제 → 5. 학과 목록 참조

---

## 🚀 설치 및 실행 방법

### 📋 사전 요구사항

시작하기 전에 다음 항목들이 설치되어 있는지 확인하세요:

- **Java Development Kit (JDK) 17 이상**
  - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 또는 [OpenJDK](https://adoptium.net/)
- **MySQL Server 8.0 이상**
  - [MySQL 다운로드](https://dev.mysql.com/downloads/mysql/)
- **MySQL Workbench** (선택사항, 권장)
  - [MySQL Workbench 다운로드](https://dev.mysql.com/downloads/workbench/)
- **IDE** (다음 중 하나)
  - Eclipse IDE with Buildship Gradle plugin
  - IntelliJ IDEA

---

### 1️⃣ 데이터베이스 설정

1. MySQL 서버가 실행 중인지 확인합니다.
   ```bash
   # MySQL 서비스 상태 확인 (Windows)
   net start | findstr MySQL
   
   # MySQL 서비스 상태 확인 (Linux/Mac)
   systemctl status mysql
   ```

2. MySQL에 접속하여 데이터베이스를 생성합니다.
   ```bash
   mysql -u root -p
   ```

3. MySQL Workbench 또는 MySQL CLI에서 다음 명령을 실행합니다.
   ```sql
   CREATE DATABASE IF NOT EXISTS lms_data;
   USE lms_data;
   ```

4. 프로젝트의 `javasignupproject/LMS_data.sql` 파일을 실행하여 테이블과 샘플 데이터를 생성합니다.
   ```bash
   # MySQL CLI에서 실행
   source /path/to/javasignupproject/LMS_data.sql;
   
   # 또는 MySQL Workbench에서 SQL 스크립트를 열어 실행
   ```

---

### 2️⃣ 데이터베이스 연결 설정

1. `javasignupproject` 디렉토리에 `config.properties` 파일을 생성합니다.
   ```bash
   cd javasignupproject
   touch config.properties  # Linux/Mac
   # Windows에서는 텍스트 편집기로 직접 생성
   ```

2. 파일에 MySQL 비밀번호를 입력합니다.
   ```properties
   db.password=YOUR_MYSQL_PASSWORD_HERE
   ```

   > ⚠️ **주의**: `config.properties` 파일은 `.gitignore`에 포함되어 있어 Git에 커밋되지 않습니다. 보안을 위해 절대 이 파일을 저장소에 포함시키지 마세요.

---

### 3️⃣ 프로젝트 가져오기 및 빌드

#### Eclipse IDE 사용 시

1. Eclipse Marketplace에서 **"Buildship Gradle Integration"** 플러그인을 설치합니다.
   - `Help > Eclipse Marketplace` 메뉴에서 "Buildship" 검색 후 설치

2. 프로젝트를 가져옵니다.
   - `File > Import...` 선택
   - `Gradle > Existing Gradle Project` 선택
   - `Project root directory`에서 `javasignupproject` 폴더 선택
   - `Finish` 클릭

3. Gradle이 자동으로 의존성(MySQL Connector, FlatLaf)을 다운로드합니다.

#### IntelliJ IDEA 사용 시

1. `File > Open` 선택
2. `javasignupproject` 폴더를 선택
3. IntelliJ가 자동으로 Gradle 프로젝트를 인식하고 의존성을 다운로드합니다.

#### 커맨드 라인에서 빌드

```bash
cd javasignupproject
./gradlew build  # Linux/Mac
gradlew.bat build  # Windows
```

---

### 4️⃣ 프로그램 실행

#### IDE에서 실행

1. `src/signup/RMain.java` 파일을 엽니다.
2. 파일을 마우스 오른쪽 클릭 > `Run As > Java Application` (Eclipse) 또는 `Run 'RMain.main()'` (IntelliJ) 선택
3. 애플리케이션이 실행되면 로그인 화면이 표시됩니다.

#### 커맨드 라인에서 실행

```bash
cd javasignupproject
./gradlew run  # Linux/Mac
gradlew.bat run  # Windows
```

---

## 🧪 테스트 계정

`LMS_data.sql` 파일 실행 시 자동으로 생성되는 테스트 계정입니다.

### 일반 사용자 계정
```
아이디: test
비밀번호: 1234
소속: 자연캠퍼스 > 공과대학 > 컴퓨터공학과
```

> 💡 **팁**: 관리자 계정을 생성하려면 MySQL에서 직접 사용자를 추가하고 `user` 테이블의 `role` 컬럼을 'admin'으로 설정하세요.

```sql
-- 관리자 계정 추가 예시
INSERT INTO user (userid, name, code, email, campus_id, college_id, department_id, role) VALUES
('admin', '관리자', 99999999, 'admin@mju.ac.kr', 1, 11, 112, 'admin');

INSERT INTO login (userId, password) VALUES
('admin', 'Admin123!@#');
```

---

## 💻 사용된 기술 스택

### 핵심 기술
| 분류 | 기술 | 버전 | 설명 |
|------|------|------|------|
| 언어 | Java | 17+ | 애플리케이션 개발 언어 |
| GUI | Java Swing | - | 데스크톱 사용자 인터페이스 |
| 데이터베이스 | MySQL Server | 8.0+ | 데이터 저장 및 관리 |
| 빌드 도구 | Gradle | 8.5 | 의존성 관리 및 빌드 자동화 |

### 라이브러리 및 도구
| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| MySQL Connector/J | 9.4.0 | MySQL 데이터베이스 연결 |
| FlatLaf | 3.6.2 | 모던한 Look and Feel 테마 |
| JUnit 5 | 5.10.0 | 단위 테스트 프레임워크 |
| JaCoCo | - | 코드 커버리지 측정 |

### 개발 도구
- **IDE**: Eclipse / IntelliJ IDEA
- **DB 관리**: MySQL Workbench
- **코드 품질**: SonarCloud, SonarLint
- **CI/CD**: GitHub Actions
- **버전 관리**: Git, GitHub

### 디자인 패턴
- **MVC (Model-View-Controller)**: 프레젠테이션 로직 분리
- **DAO (Data Access Object)**: 데이터 접근 추상화
- **Template Method**: 공통 로직 재사용 (CListController)
- **Strategy**: 상태별 처리 로직 분리
- **Singleton**: 전역 상태 관리 (MMain)

---

## 📐 프로젝트 아키텍처

### MVC 구조
```
RMain (조립자)
  ├─ Model (MMain, MUser, MLecture)
  ├─ View (VLogin, VSignup, VSearch, VRegister, VPreRegister, VSchedule, VAdmin, VMain)
  └─ Controller (CLogin, CSignup, CSearch, CRegister, CPreRegister, CSchedule, CAdmin, CMain)
```

### 패키지 구조
```
signup/
  ├─ RMain.java (진입점)
  ├─ model/ (DTO 및 상태 관리)
  │   ├─ MMain.java (전역 상태)
  │   ├─ MUser.java (사용자 DTO)
  │   ├─ MLecture.java (강좌 DTO)
  │   └─ ComboboxItem.java (콤보박스 아이템)
  ├─ view/ (GUI 컴포넌트)
  │   ├─ VMain.java (메인 프레임)
  │   ├─ VLogin.java (로그인 화면)
  │   ├─ VSignup.java (회원가입 화면)
  │   ├─ VSearch.java (강좌 검색)
  │   ├─ VRegister.java (수강신청 내역)
  │   ├─ VPreRegister.java (미리담기 내역)
  │   ├─ VSchedule.java (시간표)
  │   ├─ VAdmin.java (관리자 화면)
  │   └─ VDeptList.java (학과 목록 팝업)
  ├─ controller/ (이벤트 처리)
  │   ├─ CMain.java (메인 컨트롤러)
  │   ├─ CLogin.java (로그인)
  │   ├─ CSignup.java (회원가입)
  │   ├─ CSearch.java (강좌 검색)
  │   ├─ CRegister.java (수강신청)
  │   ├─ CPreRegister.java (미리담기)
  │   ├─ CSchedule.java (시간표)
  │   ├─ CAdmin.java (관리자)
  │   └─ CListController.java (추상 부모)
  ├─ dao/ (데이터 접근)
  │   ├─ DAO.java (DB 연결 기본 클래스)
  │   ├─ UserDAO.java
  │   ├─ LectureDAO.java
  │   └─ SaveDAO.java
  └─ constants/ (상수)
      ├─ AppConstants.java (애플리케이션 전역 상수)
      ├─ PanelNames.java (화면 이름)
      └─ StatusConstants.java (상태 코드)
```

---

## 🔒 보안 기능

* **비밀번호 보안:**
   * 메모리 내 비밀번호 즉시 제거 (`char[]` 배열 초기화)
   * 강력한 비밀번호 정책 강제 (영문/숫자/특수문자 조합)
   * ReDoS 공격 방지 (미리 컴파일된 정규식 패턴)
* **SQL 인젝션 방지:**
   * PreparedStatement 사용
   * 파라미터 바인딩으로 안전한 쿼리 실행
* **세션 관리:**
   * 로그인 상태를 MMain에서 중앙 관리
   * 로그아웃 시 완전한 세션 초기화
* **역할 기반 접근 제어 (RBAC):**
   * 관리자 기능은 권한 있는 사용자만 접근 가능
   * UI 레벨에서 메뉴 표시/숨김 제어

---

## 🎨 UI/UX 특징

* **FlatLaf 모던 테마:**
   * 다크 모드 / 라이트 모드 실시간 전환
   * 깔끔하고 현대적인 인터페이스
* **직관적인 네비게이션:**
   * 이전/다음 버튼으로 화면 이동
   * 사이드바 메뉴로 주요 기능 빠른 접근
* **반응형 창 크기:**
   * 화면별 최적화된 크기 자동 조정
* **실시간 피드백:**
   * 모든 작업에 대한 명확한 성공/실패 메시지
   * 에러 상황에 대한 친절한 안내
* **키보드 단축키:**
   * Enter 키로 로그인
   * ESC 키로 다이얼로그 닫기

---

## 🔄 데이터 흐름

### 수강신청 데이터 흐름
1. **사용자 입력** → VSearch (View)
2. **이벤트 발생** → CSearch (Controller)
3. **비즈니스 로직** → SaveDAO (DAO)
4. **DB 쿼리 실행** → MySQL
5. **결과 반환** → SaveDAO → CSearch
6. **UI 업데이트** → VSearch

### 로그인 데이터 흐름
1. **사용자 입력** → VLogin
2. **이벤트 발생** → CLogin
3. **인증 요청** → UserDAO
4. **DB 검증** → MySQL
5. **세션 생성** → MMain
6. **화면 전환** → VMain → VSearch

---

## 📊 데이터베이스 ERD 개념

```
root (캠퍼스)
  ↓ 1:N
college (단과대학)
  ↓ 1:N
department (학과)
  ↓ 1:N
lecture (강좌)

user (사용자)
  ├─ → root (FK)
  ├─ → college (FK)
  ├─ → department (FK)
  └─ 1:1 → login (인증정보)

save (수강신청/미리담기)
  ├─ → user (FK)
  └─ → lecture (FK)
```

---

## 🚧 개발 중 주요 해결 과제

1. **학점 초과 방지:**
   * DAO 레벨에서 트랜잭션 전 학점 계산 및 검증
   * 최대 18학점 제한 자동 검사

2. **중복 신청 방지:**
   * DB 복합 기본키 설정 (userid, lecture_id, status)
   * `INSERT IGNORE` 사용으로 중복 차단

3. **연쇄 콤보박스 구현:**
   * `ComboboxItem` 객체로 ID와 이름 동시 관리
   * ActionListener를 통한 동적 데이터 로딩

4. **시간표 시각화:**
   * 강좌 시간 문자열 파싱 (예: "월1,2 수3,4")
   * GridBagLayout으로 복잡한 시간표 레이아웃 구현

5. **테마 전환:**
   * FlatLaf의 동적 Look and Feel 변경
   * 전체 UI 컴포넌트 즉시 업데이트

---

## 🔧 문제 해결 (Troubleshooting)

### MySQL 연결 오류
```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```
**해결 방법**: `config.properties` 파일의 비밀번호가 정확한지 확인하세요.

### 데이터베이스가 없다는 오류
```
Unknown database 'lms_data'
```
**해결 방법**: MySQL에서 `CREATE DATABASE lms_data;` 명령을 실행하고 `LMS_data.sql` 파일을 다시 실행하세요.

### Gradle 빌드 오류
```
Could not resolve all dependencies
```
**해결 방법**: 
1. 인터넷 연결을 확인하세요.
2. `./gradlew clean build --refresh-dependencies` 명령을 실행하세요.

### JDK 버전 오류
```
Unsupported class file major version
```
**해결 방법**: JDK 17 이상을 설치하고 환경변수를 설정하세요.
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### config.properties 파일을 찾을 수 없음
```
FileNotFoundException: config.properties
```
**해결 방법**: `javasignupproject` 디렉토리에 `config.properties` 파일을 생성하고 `db.password=your_password`를 입력하세요.

---

## 📝 향후 개선 가능 사항

* 비밀번호 암호화 (해싱) 적용
* 수강 인원 제한 기능 추가
* 수강 정정 기간 설정 기능
* 강좌 평가 및 후기 시스템
* 졸업 요건 확인 기능
* 성적 조회 기능
* 이메일 인증 기능
* 비밀번호 찾기 기능
* 다국어 지원 (i18n)

---

## 📄 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

---

## 👥 기여

프로젝트 개선에 참여하고 싶으신가요? 다음 방법으로 기여할 수 있습니다:

### 버그 리포트
1. GitHub Issues에서 기존 이슈를 확인하세요.
2. 새로운 이슈를 생성하고 다음 정보를 포함하세요:
   - 버그 설명
   - 재현 방법
   - 예상 동작
   - 실제 동작
   - 환경 정보 (OS, Java 버전, MySQL 버전)

### 기능 제안
1. GitHub Issues에서 "enhancement" 라벨로 새 이슈를 생성하세요.
2. 제안하는 기능의 목적과 사용 사례를 설명하세요.

### 코드 기여
1. 저장소를 Fork하세요.
2. 새로운 브랜치를 생성하세요: `git checkout -b feature/your-feature-name`
3. 변경사항을 커밋하세요: `git commit -m 'Add some feature'`
4. 브랜치에 Push하세요: `git push origin feature/your-feature-name`
5. Pull Request를 생성하세요.

### 코드 스타일
- Java 코딩 컨벤션을 따르세요.
- 의미 있는 변수명과 메서드명을 사용하세요.
- 복잡한 로직에는 주석을 추가하세요.
- 새로운 기능에는 테스트를 추가하세요.

---

## 📞 문의

프로젝트 관련 문의사항은 GitHub Issues를 통해 남겨주세요.
