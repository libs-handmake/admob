package common.hoangdz.admob.config.shared

object AdSharedSetting {
    val MAX_NATIVE_AD_THRESHOLD = "max_native_ad_threshold" to 2

    val MIN_GAP_WATER_FLOOR = "min_gap_water_floor" to 2_000L

    val MAX_GAP_WATER_FLOOR = "max_gap_water_floor" to 30_000L

    val IGNORED_GAP_THRESHOLD = "ignored_gap_threshold" to 0

    val USE_WATER_FLOW = "use_water_flow" to false

    val INTER_GAP = "inter_gap" to 60_000L

    val APP_OPEN_GAP = "app_open_gap" to 0L

    val FULL_SCREEN_GAP = "full_screen_gap" to 0L
}