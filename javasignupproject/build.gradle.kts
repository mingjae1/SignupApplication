plugins {
    java
    application
    eclipse
    jacoco
    id("org.sonarqube") version "7.0.1.6134"
}

application {
    mainClass.set("signup.RMain")
}

sourceSets {
    main {
        java {
            srcDirs("src")
            exclude("test/**", "config.properties")
        }
    }
    test {
        java {
            srcDirs("src/test/java")
        }
    }
}

// Java 버전을 시스템에 맞게 설정 (Java 17 이상)
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// SonarCloud 설정 (프로젝트 키와 조직 이름)
sonar {
  properties {
    property("sonar.projectKey", "mingjae1_SignupApplication")
    property("sonar.organization", "mingjae1")
    property("sonar.sources", "src")
    property("sonar.tests", "src/test/java")
    property("sonar.java.binaries", "build/classes/java/main")
    property("sonar.java.test.binaries", "build/classes/java/test")
    property("sonar.exclusions", "**/test/**,**/config.properties")
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
    finalizedBy(tasks.jacocoTestReport) // test 실행 후 자동으로 리포트 생성
}

// JaCoCo 테스트 리포트 생성 설정
tasks.jacocoTestReport {
    dependsOn(tasks.test) // test 실행 후 리포트 생성
    reports {
        xml.required.set(true) // SonarQube가 XML 포맷을 읽음
        html.required.set(true)
    }
}
