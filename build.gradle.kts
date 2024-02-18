import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
	kotlin("plugin.jpa") version "1.9.21"
}

group = "testDiscordBot"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	// Kord Snapshots Repository (Optional):
	maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// 디스코드
	implementation("dev.kord:kord-core:0.12.0")
	implementation("io.ktor:ktor-client-websockets:2.3.7")
	implementation("io.ktor:ktor-client-json:2.3.7")
	implementation("io.ktor:ktor-client-serialization:2.3.7")

	// OpenAi API
	implementation("io.ktor:ktor-client-apache5:2.3.7")
	implementation("io.ktor:ktor-client-core:2.3.7")
	implementation("io.ktor:ktor-serialization-kotlinx-cbor:2.3.7")
	implementation("com.aallam.ktoken:ktoken:0.3.0")
	implementation("com.aallam.openai:openai-client:3.6.3")
	implementation("org.springframework.boot:spring-boot-starter-web")

	//json
	implementation("com.googlecode.json-simple:json-simple:1.1")


	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")


	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
