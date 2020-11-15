plugins {
    kotlin("jvm") version "1.4.10"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "com.jiangyc"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.jiangyc.notepad.MainApplicationKt")
}

javafx {
    version = "14"
    modules("javafx.controls", "javafx.fxml")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("no.tornado", "tornadofx", "1.7.20")

}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
        sourceCompatibility = "14"
        targetCompatibility = "11"
    }
}
