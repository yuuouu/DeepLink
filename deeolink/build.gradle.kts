plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "io.github.yuuouu.deeolink"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}

val artifactIdValue = project.property("POM_ARTIFACT_ID") as String
val versionName = project.property("VERSION_NAME") as String
val groupIdValue = project.property("GROUP") as String

group = groupIdValue
version = versionName

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["release"])

            groupId = groupIdValue
            artifactId = artifactIdValue
            version = versionName

            pom {
                name.set(project.property("POM_NAME") as String)
                description.set(project.property("POM_DESCRIPTION") as String)
                url.set(project.property("POM_URL") as String)

                licenses {
                    license {
                        name.set(project.property("POM_LICENSE_NAME") as String)
                        url.set(project.property("POM_LICENSE_URL") as String)
                        distribution.set(project.property("POM_LICENSE_DIST") as String)
                    }
                }

                scm {
                    url.set(project.property("POM_SCM_URL") as String)
                    connection.set(project.property("POM_SCM_CONNECTION") as String)
                    developerConnection.set(project.property("POM_SCM_DEV_CONNECTION") as String)
                }

                developers {
                    developer {
                        id.set(project.property("POM_DEVELOPER_ID") as String)
                        name.set(project.property("POM_DEVELOPER_NAME") as String)
                        url.set(project.property("POM_DEVELOPER_URL") as String)
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatype"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (versionName.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = findProperty("mavenCentralUsername") as String?
                password = findProperty("mavenCentralPassword") as String?
            }
        }
    }
}

signing {
    val signingKeyId = findProperty("SIGNING_KEY_ID") as String?
    val signingKey = findProperty("SIGNING_KEY") as String?
    val signingPassword = findProperty("SIGNING_PASSWORD") as String?

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
