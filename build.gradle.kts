plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.library").version("7.4.0").apply(false)
    kotlin("multiplatform").version("1.8.0").apply(false)
    id("maven-publish")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
