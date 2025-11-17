plugins {
    `java-library`
    alias(libs.plugins.jetbrains.kotlin.jvm)
    `publish-conventions`
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
    explicitApiWarning()
}

dependencies {
    implementation(libs.kotlin.stdlib)
}
