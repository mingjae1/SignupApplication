# \# Java Swing 수강신청 프로그램 (MVC/DAO)

# 

# Java Swing과 MySQL을 사용하여 MVC(Model-View-Controller) 패턴 및 DAO(Data Access Object) 패턴을 기반으로 구현한 데스크톱 수강신청 프로그램입니다.

# 

# Gradle을 통해 라이브러리 의존성을 관리하며, FlatLaf 스킨을 적용하여 UI를 개선했습니다. 또한, SonarCloud와 GitHub Actions를 연동하여 코드 품질을 지속적으로 관리합니다.

# 

# ---

# 

# \## 🎯 주요 기능

# 

# \* \*\*계정 관리:\*\*

# &nbsp;   \* \*\*회원가입:\*\* `FOREIGN KEY`(외래 키)를 활용한 DB 정규화 (소속 ID 저장).

# &nbsp;   \* \*\*연쇄 콤보박스:\*\* 캠퍼스 선택 $\\rightarrow$ 단과대학 목록 로드 $\\rightarrow$ 학과 목록 로드.

# &nbsp;   \* \*\*유효성 검사:\*\* ID/학번 중복 검사, 비밀번호 보안 규칙(ReDoS 방지 Regex) 적용.

# &nbsp;   \* \*\*로그인/로그아웃:\*\* `UserDAO`를 통한 DB 인증 및 세션 관리.

# \* \*\*강좌 검색 (`VSearch`):\*\*

# &nbsp;   \* 로그인한 사용자의 \*\*소속 캠퍼스\*\* 강좌만 자동 필터링.

# &nbsp;   \* 단과대학, 학과별 상세 필터링.

# &nbsp;   \* 키워드 검색 (띄어쓰기 무시 기능 포함).

# \* \*\*수강신청 관리:\*\*

# &nbsp;   \* \*\*`VRegister` (신청 내역):\*\* "신청 취소" 기능 및 총 학점 표시.

# &nbsp;   \* \*\*`VPreRegister` (미리담기):\*\* "목록 삭제" 및 "수강신청" (DB 트랜잭션) 기능.

# \* \*\*핵심 비즈니스 로직 (DAO 처리):\*\*

# &nbsp;   \* \*\*최대 학점 제한:\*\* `SaveDAO`에서 수강신청/미리담기 시 총 학점을 검사 (18학점).

# &nbsp;   \* \*\*중복 방지:\*\* `INSERT IGNORE`를 사용한 중복 신청 및 미리담기 방지.

# 

# ---

# 

# \## 🚀 설치 및 실행 방법 (How-To)

# 

# 이 프로젝트는 \*\*Gradle\*\*로 빌드되며, \*\*MySQL\*\* 데이터베이스가 필요합니다.

# 

# \### 1단계: 데이터베이스 (DB) 설정

# 

# 1\.  로컬 PC에 MySQL 서버 (v8.0.20 권장)가 설치 및 실행 중이어야 합니다.

# 2\.  MySQL Workbench (또는 다른 DB 툴)에서 \*\*`lms\_data`\*\*라는 이름의 새 스키마(데이터베이스)를 생성합니다.

# 3\.  `javasignupproject` 폴더 내의 \*\*`LMS\_data.sql`\*\* 파일 전체 스크립트를 `lms\_data` 스키마에서 실행합니다.

# &nbsp;   \* \*(이 과정은 모든 테이블과 기본 테스트 계정 1개를 생성합니다.)\*

# 

# \### 2단계: 환경 설정 (`config.properties`)

# 

# 1\.  \*\*`javasignupproject`\*\* 폴더 (즉, `src` 폴더와 같은 위치)에 \*\*`config.properties`\*\*라는 이름의 새 파일을 \*\*직접 생성\*\*해야 합니다. (이 파일은 `.gitignore` 처리되어 있습니다.)

# 2\.  파일 안에 본인의 MySQL 비밀번호를 입력합니다.

# 

# &nbsp;   ```properties

# &nbsp;   db.password=YOUR\_MYSQL\_PASSWORD\_HERE

# &nbsp;   ```

# 

# \### 3단계: Eclipse IDE로 가져오기 (Gradle)

# 

# 1\.  (필수) Eclipse Marketplace에서 \*\*"Buildship Gradle Integration"\*\* 플러그인을 설치합니다.

# 2\.  `File > Import...` (가져오기)를 선택합니다.

# 3\.  `Gradle > \*\*Existing Gradle Project\*\*` (기존 Gradle 프로젝트)를 선택합니다.

# 4\.  `Project root directory`에서 이 저장소의 \*\*`javasignupproject`\*\* 폴더를 선택합니다.

# 5\.  `Finish`를 누르면 Gradle이 `build.gradle.kts` 파일을 읽어 모든 라이브러리(MySQL, FlatLaf)를 자동으로 다운로드합니다.

# 

# \### 4단계: 프로그램 실행

# 

# 1\.  `src/signup/RMain.java` 파일을 엽니다.

# 2\.  `RMain.java` 파일을 마우스 오른쪽 클릭 > `Run As > Java Application`으로 실행합니다.

# 

# ---

# 

# \## 🧪 기본 테스트 계정

# 

# `LMS\_data.sql` 파일 실행 시, 다음과 같은 테스트 계정이 자동으로 생성됩니다.

# 

# \* \*\*ID:\*\* `test`

# \* \*\*PW:\*\* `1234`

# \* \*\*(소속:\*\* 자연캠퍼스, 공과대학, 컴퓨터공학과)

# 

# ---

# 

# \## 💻 사용된 기술 스택

# 

# \* \*\*언어:\*\* Java (JDK 21)

# \* \*\*IDE:\*\* Eclipse 2025-06 (4.36.0)

# \* \*\*데이터베이스:\*\*

# &nbsp;   \* MySQL Server 8.0.20

# &nbsp;   \* MySQL Workbench 8.0.20

# \* \*\*빌드 도구:\*\* Gradle (Kotlin DSL)

# \* \*\*라이브러리 (Dependencies):\*\*

# &nbsp;   \* MySQL Connector/J (9.4.0)

# &nbsp;   \* FlatLaf (Look and Feel UI)

# \* \*\*코드 품질:\*\* SonarCloud (GitHub Actions 연동) / SonarLint

