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
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlin.result)
    implementation(project(":aformvalidator-annotation"))

    testImplementation(libs.bundles.unittest)
}
