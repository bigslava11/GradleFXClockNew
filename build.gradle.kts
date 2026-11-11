plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // Example external library
    implementation("com.google.guava:guava:33.3.1-jre")

    // JUnit testing
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

javafx {
    version = "24.0.2"

    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.web",
        "javafx.swing"
    )
}

application {
    mainClass = "org.example.FXClock"
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}
