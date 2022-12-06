import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin:
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // OpenTelemetry:
    implementation(platform("io.opentelemetry:opentelemetry-bom:1.19.0"))
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-extension-kotlin")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.19.2-alpha")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("example.MainKt")
    applicationDefaultJvmArgs = listOf(
//        "-javaagent:./opentelemetry-javaagent.jar",
        "-javaagent:./opentelemetry-javaagent-1.21.0-20221206.221310-88.jar",
        "-Dotel.service.name=coroutines-example",
        "-Dotel.traces.exporter=jaeger",
        "-Dotel.exporter.jaeger.endpoint=http://localhost:14250",
        "-Dotel.metrics.exporter=none",
//        "-Dotel.instrumentation.kotlinx-coroutines.enabled=false",
    )
}