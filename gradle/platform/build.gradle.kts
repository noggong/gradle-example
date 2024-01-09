plugins {
    // 플랫폼 방식의 버전 중앙 관리
    // 의존성 제약 조건과 의존성만 정의 한다 그 외에는 java-library 와 같음
    // jar 같은 것이 포함되어있지 않음
    id("java-platform")
}

// 외부에서 해당 플랫폼의 의존성 제약을 받고 싶을때 사용하는 그룹이름
group = "com.example"

javaPlatform.allowDependencies()
dependencies {
    // "com.fasterxml.jackson:jackson-bom:2.13.3" 의 platform 제약조건을 상속받을수 있다.
    api(platform("com.fasterxml.jackson:jackson-bom:2.13.3"))
}

// 플랫폼 방식의 버전 중앙 관리 (의존성 제약조건)
dependencies.constraints {
    // 의존성 추가처럼 보이지만 실제 추가가 아니고 단지 사용할 버전 추가임
    api("org.apache.commons:commons-lang3:3.12.0")
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.slf4j:slf4j-simple1.7.36")
}