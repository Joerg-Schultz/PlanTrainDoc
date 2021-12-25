package de.tierwohlteam.android.plantraindoc_v1.others

object Constants {
    const val PTD_DB_NAME = "plantraindocv1_db"

    const val SHARED_PREFERENCES_NAME = "sharedPrefs"
    const val KEY_USER_ID = "KEY_USER_ID"
    const val DEFAULT_USER_NAME = "PTD AppUser"
    const val DEFAULT_USER_EMAIL = "PTD.appuser@mail.de"
    const val DEFAULT_USER_PASSWORD = "1234"

    const val KEY_HAS_ACCOUNT = "hasAccount"
    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_LOGGED_IN_NAME = "KEY_LOGGED_IN_NAME"
    const val KEY_PASSWORD = "KEY_PASSWORD"

    const val KEY_USE_WEB_SERVER = "useWebServer"
    const val KEY_USE_CLICKER = "useClicker"
    const val KEY_USE_SPEECH = "useSpeechforHelper"
    const val KEY_USE_LIGHT_GATE = "useLightGate"
    const val KEY_USE_AUTO_CLICK = "useAutoClick" //only enabled with lightGate
    const val KEY_USE_FEEDER = "useFeeder"

    const val FIRST_USAGE = "FIRST_USAGE"
    const val FIRST_GOAL = "FIRST_GOAL"

    const val VIBRATION_SHORT = 200L
    const val VIBRATION_LONG = 500L

    const val BASE_URL = "http://192.168.178.29:8081"
    val IGNORE_AUTH_URLS = listOf("/register", "/login")

    const val LAST_SYNC_DATE = "LAST_SYNC_DATE"
}