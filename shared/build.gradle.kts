import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.apollographql.apollo3").version("3.7.4")
    `maven-publish`
}

version = file("../VERSION").readText()

apply {
    plugin("maven-publish")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    val xcFramework = XCFramework()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            xcFramework.add(this)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.apollographql.apollo3:apollo-runtime:3.7.4")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.apollographql.apollo3:apollo-runtime:3.7.4")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.mobileappconsultant.newsfeedmmsdk"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}

configure<com.apollographql.apollo3.gradle.api.ApolloExtension> {

    sourceSets {
        schemaFile.set(file("src/commonMain/graphql/schema.json"))
    }
}

apollo {
    generateKotlinModels.set(true)
    packageName.set("com.mobileappconsultant.newsfeedmmsdk.graphql")

    introspection {
        schemaFile.set(file("src/commonMain/graphql/schema.json"))
        endpointUrl.set("https://newsfeedapi.frontendlabs.co.uk/query")
    }
}

val githubProperties = Properties()
githubProperties.load(project.rootProject.file("github.properties").inputStream())

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/mobileappconsultant/newsfeedkmmsdk")
            credentials {
                username = githubProperties["gpr.user"] as String? ?: System.getenv("GPR_USER")
                password = githubProperties["gpr.key"] as String? ?: System.getenv("GPR_KEY")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            groupId = "com.mobileappconsultant.newsfeedkmmsdk"
            artifactId = "sdk"
            version = file("../VERSION").readText()
            artifact("$buildDir/outputs/aar/shared-release.aar")

            pom.withXml {
                // for dependencies and exclusions
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.allDependencies.withType(ModuleDependency::class.java) {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", group)
                    dependencyNode.appendNode("artifactId", name)
                    dependencyNode.appendNode("version", version)

                    // for exclusions
                    if (excludeRules.size > 0) {
                        val exclusions = dependencyNode.appendNode("exclusions")
                        excludeRules.forEach { ex ->
                            val exclusion = exclusions.appendNode("exclusion")
                            exclusion.appendNode("groupId", ex.group)
                            exclusion.appendNode("artifactId", ex.module)
                        }
                    }
                }
            }
        }
    }
}