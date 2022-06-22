plugins {
    id("com.ghtk.gchat.lib")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.androidTest)

    // TFLite
    api("org.tensorflow:tensorflow-lite-task-vision:0.3.1")
}
