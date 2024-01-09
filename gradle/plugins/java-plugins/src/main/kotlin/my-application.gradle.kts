plugins {
    id("application")
    id("my-java-base")
}

// jar 파일을 합축하는 tasks 만들기
tasks.register<Zip>("bundle") {
    group = "My group"
    description = "packages it all!"

    from(tasks.jar) // jar task 의 결과물을 받음
    from(configurations.runtimeClasspath)

    destinationDirectory.set(layout.buildDirectory.dir("distribution"))
}