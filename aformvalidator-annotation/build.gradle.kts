import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlin.result)
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                from(components.findByName("release") ?: components["java"])
//                groupId = project.group.toString()
//                artifactId = project.name
//                version = project.version.toString()
//
//                pom {
//                    name = project.name
//                    description = project.description
//                    url = "https://github.com/Brendon-Mendicino/aformvalidator"
//
//                    licenses {
//                        license {
//                            name = "MIT License"
//                            url = "https://opensource.org/licenses/MIT"
//                        }
//                    }
//
//                    developers {
//                        developer {
//                            id = "Brendon-Mendicino"
//                            name = "Brendon Mendicino"
//                            url = "https://github.com/Brendon-Mendicino"
//                            email = "brendonmendicino@yahoo.it"
//                        }
//                    }
//
//                    scm {
//                        url = "https://github.com/Brendon-Mendicino/aformvalidator"
//                        connection = "scm:git:https://github.com/Brendon-Mendicino/aformvalidator.git"
//                        developerConnection = "scm:git:ssh://git@github.com/Brendon-Mendicino/aformvalidator.git"
//                    }
//                }
//            }
//        }
//
//        repositories {
//            // GitHub Packages
//            maven {
//                name = "GitHub"
//                url = uri("https://maven.pkg.github.com/Brendon-Mendicino/aformvalidator")
//                credentials {
//                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
//                }
//            }
//
//            // Maven Central (Sonatype)
//            maven {
//                name = "Sonatype"
//                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//                credentials {
//                    username = project.findProperty("sonatypeUsername") as String?
//                    password = project.findProperty("sonatypePassword") as String?
//                }
//            }
//        }
//    }
//
//    signing {
//        sign(publishing.publications["release"])
//    }
//}

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
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
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
