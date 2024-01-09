import com.example.gradle.JarCount

plugins {
    id("application")
    id("my-java-base")
}
// 애플리케이션을 구성하는 Jar 파일 수를 세고 그 결과를 파일에 쓰는 task
// @see (gradle/plugins/java-plugins/src/main/java/com.example.gradle.JarCount)
// jarCount 의 메소드들에서 get 을 자동으로 빼고 구현체가 자동 생성 된다.
tasks.register<JarCount>("countJars") {
    group = "My Group"
    description = "Count Jar count"

    allJars.from(tasks.jar)
    allJars.from(configurations.runtimeClasspath)

    countFile.set(layout.buildDirectory.file("gen/count.txt"))
}

// jar 파일을 합축하는 tasks 만들기
tasks.register<Zip>("bundle") {
    group = "My Group"
    description = "packages it all!"

    from(tasks.jar) // jar task 의 결과물을 받음
    from(configurations.runtimeClasspath)

    destinationDirectory.set(layout.buildDirectory.dir("distribution"))
}

// 라이프 사이클 에 task 추가
tasks.build {
    dependsOn(tasks.named("bundle"))
}

// 라이프 사이클 task 생성
tasks.register("buildAll") {
    description = "Build even more!"

    dependsOn(tasks.build)
    dependsOn(tasks.named("countJars"))
}