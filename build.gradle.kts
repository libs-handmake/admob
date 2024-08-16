plugins {
    alias(libs.plugins.android.library)
    id(libs.plugins.hoangdv.library.get().pluginId)
    id(libs.plugins.hoangdv.core.get().pluginId)
    id(libs.plugins.hoangdv.firebase.core.get().pluginId)
    id(libs.plugins.hoangdv.jetpack.compose.get().pluginId)
}

android {
    namespace = "common.hoangdz.admob"
}

dependencies {
    implementation(project(":base:android-common"))
    implementation(libs.play.services.ads)
    implementation(libs.compose.shimmer)
    implementation(libs.user.messaging.platform)
}

