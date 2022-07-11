object AppDependencies {
    // Core
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.agp}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.core_ktx}"
    const val ksp = "com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}"

    // Compose
    const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.compose}"
    const val composeUI = "androidx.compose.ui:ui:${Versions.compose}"
    const val composeUITooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    const val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    const val composeLivedata = "androidx.compose.runtime:runtime-livedata:${Versions.compose}"
    const val composeViewmodel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
    val compose = listOf(
        composeRuntime,
        composeUI,
        composeUITooling,
        composeFoundation,
        composeMaterial,
        composeLivedata,
        composeViewmodel
    )
    const val composeActivity = "androidx.activity:activity-compose:1.5.0"
    const val composeNavigation = "androidx.navigation:navigation-compose:2.5.0"


    // Kotlin
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val kotlin = listOf(kotlinStdlib, kotlinReflect)

    // App components
    const val activity = "androidx.activity:activity-ktx:${Versions.activity}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    const val fragmentTesting = "androidx.fragment:fragment-testing:${Versions.fragment}"
    val appComponents = listOf(activity, fragment)

    // UI
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val legacySupport = "androidx.legacy:legacy-support-v4:${Versions.legacy_support}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val gridLayout = "androidx.gridlayout:gridlayout:${Versions.gridLayout}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    const val pager = "androidx.viewpager2:viewpager2:${Versions.pager}"

    const val paging = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
    const val pagingCompose = "androidx.paging:paging-compose:1.0.0-alpha14"

    const val material = "com.google.android.material:material:${Versions.material}"


    // Integration
    const val chromeBrowser = "androidx.browser:browser:${Versions.chromeBrowser}"
    const val coreGooglePlay = "com.google.android.play:core-ktx:${Versions.coreGooglePlay}"
    const val googleServices = "com.google.gms:google-services:${Versions.googleServices}"

    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val dataBinding = "com.android.databinding:compiler:${Versions.dataBinding}"
    const val holoColorPicker = "com.larswerkman:HoloColorPicker:${Versions.holo_color_picker}"
    const val jodaTime = "joda-time:joda-time:${Versions.joda_time}"
    const val json = "org.json:json:${Versions.json}"
    const val junit = "junit:junit:${Versions.junit}"
    const val securityCrypto = "androidx.security:security-crypto:${Versions.security_crypto}"
    const val preference = "androidx.preference:preference-ktx:${Versions.preference}"

    const val shimmer = "com.facebook.shimmer:shimmer:${Versions.shimmer}"
    const val startup = "androidx.startup:startup-runtime:${Versions.startup}"
    const val webkit = "androidx.webkit:webkit:${Versions.webkit}"

    // Glide
    const val glideRuntime = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltPlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val hiltWork = "androidx.hilt:hilt-work:${Versions.hilt_work}"
    const val hiltWorkCompiler = "androidx.hilt:hilt-compiler:${Versions.hilt_work}"

    // Firebase
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val firebaseGradle =
        "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseGradle}"
    val firebaseModules = listOf(
        "com.google.firebase:firebase-analytics-ktx",
        "com.google.firebase:firebase-storage-ktx",
        "com.google.firebase:firebase-crashlytics-ktx"
    )

    // Lifecycle
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val lifecycleJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val lifecycleViewmodel =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleLivedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    val lifecycle = listOf(lifecycleRuntime, lifecycleJava8, lifecycleViewmodel, lifecycleLivedata)

    // Navigation
    const val navigationFragment =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationSafeArgsPlugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    val navigation = listOf(navigationFragment, navigationUI)

    // Network
    const val retrofitRuntime = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitMock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"
    const val okhttpLoggingInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLoggingInterceptor}"
    val network = listOf(retrofitRuntime, retrofitGson, retrofitMock, okhttpLoggingInterceptor)

    // Room DB
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomTesting = "androidx.room:room-testing:${Versions.room}"

    // Work
    const val workRuntime = "androidx.work:work-runtime:${Versions.work}"
    const val workTesting = "androidx.work:work-testing:${Versions.work}"
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.work}"

    // Testing

}