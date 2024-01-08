// 클린 빌드 파일의 상단에는 항상 plugin 블록이 있어야 한다.
plugins {
    // java-library 와 유사한 점이 많음
    // 컴파일 해야하는 java 코드가 있거나 테스트를 수행하는 경우 둘다 java 테스트 프레임 워크를 이용
    id("application")
}

// java 버전 구성
// id(java")" 플러그인에 의해 확장된 gradle 블록
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}