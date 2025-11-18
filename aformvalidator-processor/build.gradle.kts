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
    implementation(libs.bundles.kotlinpoet)
    implementation(project(":aformvalidator-core"))

    testImplementation(libs.bundles.unittest)
//    // --- JUnit 5 (Jupiter) ---
//    testImplementation(platform("org.junit:junit-bom:6.0.0")) // aligns all JUnit modules
//    testImplementation("org.junit.jupiter:junit-jupiter")
//
//    // Optional: Kotlin test helpers (assertions, etc.)
//    testImplementation(kotlin("test")) // wraps JUnit assertions nicely
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.compile.testing)
    testImplementation(libs.kotlin.compile.testing.ksp)
}
