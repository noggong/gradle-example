// 클린 빌드 파일의 상단에는 항상 plugin 블록이 있어야 한다.
plugins {
    id("my-java-library") // java-library (gradle core 에서 제공하는 라이브러리) -> src/main/java 폴더를 java 소스용 폴더로 지정하고 이걸 사용하여 일반 폴더 구조대신 java 패키지 형식으로 바꾼다.
}

dependencies {
    implementation(platform("com.example:platform"))
    implementation(project(":data-model"))
    implementation(libs.slf4j.api)
    implementation(libs.commons.lang)

    // test source set 에 의존성 추가
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // 위에서 정의한 source set (integrationTest) 전용 의존성 추가할 수 있다
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-api")
    integrationTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

//    api("org.apache.commons:commons-lang3") // 추가한 종속성이 컴파일 주에 전이적으로 표시된다는 의미 : business-logic 을 의존하는 플젝이 있다면 해당 프로젝트 의존성에도 표기된다.
//    runtimeOnly("group:name") // <-- 런타임에만 존재
    // compileOnly("group:name") // <- 컴파일 단계에서만 존재
}

//configurations {
//    compileClasspath // <- Compile time "view" (aka Variant)
//    runtimeClasspath // <- "Runtime "view" (aka Variant)
//    compileClasspath.extendsFrom(implementation, compileOnly, ...)
//    compileClasspath.extendsFrom(implementation, runtimeOnly, ...)
//}

//dependencies.constraints {
    // 의존성 추가처럼 보이지만 실제 추가가 아니고 단지 사용할 버전 추가임
//    implementation("org.apache.commons:commons-lang3:3.12.0")
//}