import org.jreleaser.model.Active

plugins {
    `maven-publish`

    // Remember that unlike regular Gradle projects, convention plugins in buildSrc do not automatically resolve
    // external plugins. We must declare them as dependencies in buildSrc/build.gradle.kts.
    id("org.jreleaser")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            description = project.description

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
    }

    repositories {
        // Local repository
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}


jreleaser {
    gitRootSearch = true
    signing {
        active = Active.ALWAYS
        armored = true
    }
    deploy {
        maven {
            // Maven Central
            // doc: https://jreleaser.org/guide/latest/reference/deploy/maven/maven-central.html
            mavenCentral {
                create("sonatype") {
                    active = Active.ALWAYS
                    applyMavenCentralRules = true
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")

                    retryDelay = 60
                    maxRetries = 120
                }
            }

            // GitHub Packages
            // doc: https://jreleaser.org/guide/latest/reference/deploy/maven/github.html
            github {
                create("packages") {
                    active = Active.ALWAYS
                    applyMavenCentralRules = true
                    url = "https://maven.pkg.github.com/Brendon-Mendicino/aformvalidator"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}
