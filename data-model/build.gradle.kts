// 클린 빌드 파일의 상단에는 항상 plugin 블록이 있어야 한다.
plugins {
    id("java") // java core 기능, java-library 푸함된 plugin
    id("java-library") // java-library (gradle core 에서 제공하는 라이브러리) -> src/main/java 폴더를 java 소스용 폴더로 지정하고 이걸 사용하여 일반 폴더 구조대신 java 패키지 형식으로 바꾼다.
}

// java 버전 구성
// id(java")" 플러그인에 의해 확장된 gradle 블록
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}
