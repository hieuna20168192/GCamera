plugins {
    id("com.ghtk.lib")
}

android {
    val disableSet = setOf(
        "ResourceName",
        "ScopedStorage",
        "CanvasSize",
        "DefaultLocale",
        "ManifestOrder",
        "UseCompatLoadingForDrawables",
        "LogNotTimber",
        "DrawAllocation",
        "ObsoleteLayoutParam",
        "ObsoleteSdkInt",
        "Overdraw",
        "UnusedResources",
        "IconLocation",
        "IconMissingDensityFolder",
        "ContentDescription",
        "SetTextI18n",
        "HardcodedText",
        "RelativeOverlap",
        "RtlHardcoded",
        "RtlEnabled",
        "UnknownIssueId",
        "RtlSymmetry"
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
    implementation(libs.kycSdk) { isChanging = true }
    implementation(libs.utils) { isChanging = true }
    implementation(libs.logger) { isChanging = true }
    implementation(libs.repository) { isChanging = true }
    implementation(libs.internalconfig) { isChanging = true }
    implementation(libs.material)
    implementation(libs.cameraview)
    implementation(libs.mlkitCamera)
    implementation(libs.playServicesMlkitFaceDetect)
    implementation(libs.gson)
    implementation(libs.moshi)
}
