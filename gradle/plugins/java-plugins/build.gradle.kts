// 클린 빌드 파일의 상단에는 항상 plugin 블록이 있어야 한다.
plugins {
    `kotlin-dsl` // =  id("kotlin-dsl") // kotlin-dsl 을 이용하여 gradle plugin을 작성하는 프로젝트임을 gradle 에게 알림
}

dependencies {
    // my-java-base 의 plugin com.diffplug.spotless 은 gradle core 에 없기에 의존성 추가해야한다.
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.8.0")
}
