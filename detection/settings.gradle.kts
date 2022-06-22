val projectArtifactId: String by settings
rootProject.name = projectArtifactId

pluginManagement {
    fun Settings.getEnv(key: String): String? {
        return providers.environmentVariable(key).orNull
    }

    val pluginsVersion: String by settings
    val isRelease = getEnv("RELEASE")
    val repo = getEnv("GHTK_REPO")
    val repoDev = getEnv("GHTK_REPO_DEV")
    val artifactoryUrl = getEnv("ARTIFACTORY_URL")
    val artifactoryUsername = getEnv("ARTIFACTORY_USERNAME")
    val artifactoryPassword = getEnv("ARTIFACTORY_PASSWORD")
    val repoUrl = if (isRelease == "true") "$artifactoryUrl/$repo" else "$artifactoryUrl/$repoDev"

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        mavenLocal()
        maven {
            setUrl(repoUrl)
            isAllowInsecureProtocol = true
            credentials {
                username = artifactoryUsername
                password = artifactoryPassword
            }
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.ghtk.gchat.setting") {
                useModule("com.ghtk:plugins:$pluginsVersion")
            }
        }
    }
}

plugins {
    id("com.ghtk.gchat.setting")
}
