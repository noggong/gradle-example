plugins {
    id("java")
    id("com.diffplug.spotless")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}
