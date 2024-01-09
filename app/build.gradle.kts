// 클린 빌드 파일의 상단에는 항상 plugin 블록이 있어야 한다.
plugins {
    // java-library 와 유사한 점이 많음
    // 컴파일 해야하는 java 코드가 있거나 테스트를 수행하는 경우 둘다 java 테스트 프레임 워크를 이용
    id("my-application")
}

application {
    // main class gradle 에 알려줌
    mainClass.set("com.example.MyApplication")
}

dependencies {
    implementation(project(":data-model"))
    implementation(project(":business-logic"))
}