rootProject.name = "gcamera"

pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}


dependencyResolutionManagement {

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

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl(repoUrl)
            isAllowInsecureProtocol = true
            credentials {
                username = artifactoryUsername
                password = artifactoryPassword
            }
        }
        jcenter()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.10"
}

fun Settings.getEnv(key: String): String? {
    return providers.environmentVariable(key).orNull
}

val isCI = getEnv("CI")
val isUseComposite = getEnv("USE_COMPOSITE")
val isRelease = getEnv("RELEASE")
val artifactoryUsername = getEnv("ARTIFACTORY_USERNAME")
val artifactoryPassword = getEnv("ARTIFACTORY_PASSWORD")
val artifactoryUrl = getEnv("ARTIFACTORY_URL")
val cacheRepo = getEnv("GHTK_CACHE")
val cacheUrl = "$artifactoryUrl/$cacheRepo/"

println("CI Build?: $isCI")
println("Use composite?: $isUseComposite")
println("Is Release?: $isRelease")

gradleEnterprise {
    buildScan {
        // Connecting to scans.gradle.com by agreeing to the terms of service
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(isCI == "true")
    }
}

buildCache {
    local {
        isEnabled = true
        removeUnusedEntriesAfterDays = 15
    }

    remote<HttpBuildCache> {
        isEnabled = true
        url = uri(cacheUrl)
        isAllowInsecureProtocol = true
        credentials {
            username = artifactoryUsername
            password = artifactoryPassword
        }
        isUseExpectContinue = true
        isPush = isCI == "true"
    }
}

if (isUseComposite == "true") {

    include("app")

    includeBuild("ekyc/eui") {
        dependencySubstitution {
            substitute(module("com.ghtk.internal.ekyc:eui")).using(project(":"))
        }
    }

    includeBuild("detection") {
        dependencySubstitution {
            substitute(module("com.ghtk.internal:detection")).using(project(":"))
        }
    }

    includeBuild("ekyc/sdk") {
        dependencySubstitution {
            substitute(module("com.ghtk.internal.ekyc:sdk")).using(project(":"))
        }
    }
}
