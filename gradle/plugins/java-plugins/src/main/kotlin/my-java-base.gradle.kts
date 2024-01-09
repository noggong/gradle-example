plugins {
    id("java")
    id("base")
    id("com.diffplug.spotless")
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

