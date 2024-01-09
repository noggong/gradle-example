import com.example.gradle.Slf4jSimpleRule

plugins {
    id("java")
    id("base")
    id("com.diffplug.spotless")
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

