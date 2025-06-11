import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlin.result)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString(),
    )

    pom {
        name = project.name
        description = project.description
        url = "https://github.com/Brendon-Mendicino/aformvalidator"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "Brendon-Mendicino"
                name = "Brendon Mendicino"
                url = "https://github.com/Brendon-Mendicino"
                email = "brendonmendicino@yahoo.it"
            }
        }

        scm {
            url = "https://github.com/Brendon-Mendicino/aformvalidator"
            connection = "scm:git:https://github.com/Brendon-Mendicino/aformvalidator.git"
            developerConnection =
                "scm:git:ssh://git@github.com/Brendon-Mendicino/aformvalidator.git"
        }
    }
}

publishing {
    repositories {
        // GitHub Packages
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/Brendon-Mendicino/aformvalidator")
            // username and password (a personal Github access token) should be specified as
            // `githubPackagesUsername` and `githubPackagesPassword` Gradle properties or alternatively
            // as `ORG_GRADLE_PROJECT_githubPackagesUsername` and `ORG_GRADLE_PROJECT_githubPackagesPassword`
            // environment variables
            credentials(PasswordCredentials::class)
        }
    }
}