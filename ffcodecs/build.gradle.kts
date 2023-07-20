import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "com.com.github.anilbeesetti.nextlib.ffcodecs"

    compileSdk = 33

    defaultConfig {

        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }

        ndk {
            abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }

        ndkVersion = "25.2.9519653"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        /// Set JVM target to 17
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

// Gradle task to setup ffmpeg
val ffmpegSetup by tasks.registering(Exec::class) {
    workingDir = file("../ffmpeg")
    // export ndk path and run bash script
    environment("ANDROID_NDK_HOME", android.ndkDirectory.absolutePath)
    commandLine("bash", "setup.sh")
}

tasks.preBuild.dependsOn(ffmpegSetup)

dependencies {
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.google.errorprone.annotations)
    implementation(libs.androidx.annotation)
    compileOnly(libs.checker.qual)
    compileOnly(libs.kotlin.annotations.jvm)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = "com.github.anilbeesetti"
                artifactId = "nextlib"
                version = "1.0"

                from(components["release"])
            }
        }
    }
}