plugins {
    java
    eclipse
    id("org.sonarqube") version "7.0.1.6134"
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
    }
}

// Java 21 버전을 사용한다고 설정
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// SonarCloud 설정 (프로젝트 키와 조직 이름)
sonar {
  properties {
    property("sonar.projectKey", "mingjae1_SignupApplication")
    property("sonar.organization", "mingjae1")
  }
}

// .jar 파일들을 다운로드할 저장소 (Maven Central)
repositories {
    mavenCentral()
}

// 4. [핵심] 님의 프로젝트가 사용할 .jar 파일 목록 (의존성)
dependencies {
    // 1. MySQL 커넥터
    implementation("com.mysql:mysql-connector-j:9.4.0")

    // 2. FlatLaf (방금 추가한 Look and Feel 스킨)
    implementation("com.formdev:flatlaf:3.6.2")

    // 3. JUnit (테스트 및 SonarCloud 커버리지용)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// JUnit 5 테스트를 실행하도록 설정
tasks.withType<Test> {
    useJUnitPlatform()
}
