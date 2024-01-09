import com.example.gradle.Slf4jSimpleRule

plugins {
    id("java")
    id("base")
    id("com.diffplug.spotless")
}

sourceSets.create("integrationTest")

tasks.register<Test>("integrationTest") {
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    useJUnitPlatform()
}

dependencies.components {
    withModule<Slf4jSimpleRule>("org.slf4j:slf4j-simple")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named<JavaCompile>("compileJava") {
}

tasks.compileJava {
//    options.encoding = "UTF-8"
}

tasks.compileTestJava {
//    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform {
        excludeTags("slow")
    }

    maxParallelForks = 4 // 병렬테스트 프로세스 갯수

    maxHeapSize = "1g" // 프로세스별 힙 메모리
}

// Test 는 gradle core type
tasks.register<Test>("testSlow") {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform {
        includeTags("slow")
    }
}

tasks.check {
    dependsOn(tasks.named("testSlow"))
}
