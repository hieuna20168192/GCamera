plugins {
    id("com.ghtk.gchat.lib")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.androidTest)

    // TFLite
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.3.1")

//    implementation("com.google.mlkit:image-labeling-custom:17.0.1")
}
