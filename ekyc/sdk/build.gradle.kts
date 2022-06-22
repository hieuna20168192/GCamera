plugins {
    id("com.ghtk.lib")
}

android {
    val disableSet = setOf(
        "SupportAnnotationUsage",
        "LogNotTimber",
        "ObsoleteSdkInt",
        "UnknownIssueId"
    )
    lint {
        disable.addAll(disableSet)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.androidTest)
    implementation(libs.model) { isChanging = true }
    implementation(libs.base) { isChanging = true }
    implementation(libs.authenCore) { isChanging = true }
    implementation(libs.internalconfig) { isChanging = true }
    implementation(libs.logger) { isChanging = true }
    implementation(libs.repository) { isChanging = true }
    implementation(libs.material)
    implementation(libs.mlkitFaceDetection)
    implementation(libs.mlkitCamera)
    implementation(libs.moshi)
}
