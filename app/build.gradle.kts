import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.autoresconfig)
    alias(libs.plugins.refine)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.nav.safeargs.kotlin)
}

android {
    namespace = "org.frknkrc44.hma_oss"

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    packaging {
        dex.useLegacyPackaging = true
        resources {
            excludes += arrayOf(
                "/META-INF/*",
                "/META-INF/androidx/**",
                "/kotlin/**",
                "/okhttp3/**",
            )
        }
    }
}

kotlin {
    jvmToolchain(21)
}

autoResConfig {
    generateClass.set(true)
    generateRes.set(false)
    generatedClassFullName.set("icu.nullptr.hidemyapplist.util.LangList")
    generatedArrayFirstItem.set("SYSTEM")
}

dependencies {
    implementation(projects.common)
    runtimeOnly(projects.xposed)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.com.drakeet.about)
    implementation(libs.com.drakeet.multitype)
    implementation(libs.com.github.kirich1409.viewbindingpropertydelegate)
    implementation(libs.com.github.liujingxing.rxhttp)
    implementation(libs.com.github.liujingxing.rxhttp.converter.serialization)
    implementation(libs.com.github.topjohnwu.libsu.core)
    implementation(libs.com.squareup.okhttp3)
    implementation(libs.dev.rikka.hidden.compat)
    implementation(libs.me.zhanghai.android.appiconloader)
    compileOnly(libs.dev.rikka.hidden.stub)
    ksp(libs.com.github.liujingxing.rxhttp.compiler)

    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.material)
}

android.applicationVariants.all {
    outputs.all {
        (this as BaseVariantOutputImpl).apply {
            outputFileName = "${rootProject.name.replace(" ", "_")}-v${versionName}-${buildType.name}.apk"
        }
    }
}
