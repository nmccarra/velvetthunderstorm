plugins {
	java
	alias(libs.plugins.spring.framework.boot)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.open.api.generator)
}

group = "com.nmccarra"
version = "0.0.1-SNAPSHOT"
description = "Velvet Thunderstorm"


java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

openApiGenerate {
	generatorName = "spring"
	inputSpec = "$rootDir/src/main/resources/static/openapi.yaml"
	apiPackage = "com.nmccarra.velvetthunderstorm.api"
	modelPackage = "com.nmccarra.velvetthunderstorm.model"
	configOptions = mapOf(
		"useTags" to "true",
		"useSpringBoot3" to "true",
		"interfaceOnly" to "true",
		"dateLibrary" to "java8",
		"useBeanValidation" to "true",
		"serializationLibrary" to "jackson",
		"useJakartaEe" to "true"
	)
}

sourceSets {
	main {
		java {
			srcDir("$buildDir/generate-resources/main/src/main/java")
		}
	}
}

dependencies {
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.doc.open.api.starter.webmvc.ui)
	implementation(libs.snake.yaml)
	implementation(libs.jackson.databbind.nullable)
	implementation(libs.jakarta.validation.api)
	implementation(libs.swagger.core)
	implementation(libs.aws.dynamodb)
	implementation(libs.aws.dynamodb.enhanced)

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation(libs.spring.boot.starter.test)

	testImplementation(platform(libs.junit.bom))

	// Core JUnit 5 dependencies
	testImplementation(libs.junit.jupiter.api)
	testImplementation(libs.junit.jupiter.params) // for parameterized tests
	testRuntimeOnly(libs.junit.jupiter.engine) // test engine

	testImplementation(libs.assertj.core) // Better assertions
	testImplementation(libs.mockito.junit.jupiter) // Mocking

	testImplementation("org.testcontainers:testcontainers:1.19.3")
	testImplementation("org.testcontainers:junit-jupiter:1.19.3")
	testImplementation("org.testcontainers:localstack:1.19.3")
	testImplementation("org.awaitility:awaitility:4.2.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.compileJava {
	dependsOn(tasks.openApiGenerate)
}
