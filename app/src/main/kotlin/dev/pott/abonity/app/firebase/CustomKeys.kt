package dev.pott.abonity.app.firebase

import android.content.Context
import android.os.Build
import androidx.navigation.NavBackStackEntry
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import java.util.Locale

private const val FIREBASE_CUSTOM_KEY_COUNTRY = "Country"
private const val FIREBASE_CUSTOM_KEY_LANGUAGE = "Language"
private const val FIREBASE_CUSTOM_KEY_PLAY_SERVICES_AVAILABLE = "Play Services"

private const val FIREBASE_CUSTOM_KEY_INSTALLATION_SOURCE = "Installation Source"

private const val INSTALLATION_SOURCE_UNSUPPORTED =
    "Information unsupported by current Android version"
private const val NOT_AVAILABLE = "N/A"

fun Context.setFirebaseDefaultCustomKeys() {
    Firebase.crashlytics.setCustomKeys {
        val locale = Locale.getDefault()
        key(FIREBASE_CUSTOM_KEY_COUNTRY, locale.country)
        key(FIREBASE_CUSTOM_KEY_LANGUAGE, locale.language)
        key(FIREBASE_CUSTOM_KEY_PLAY_SERVICES_AVAILABLE, isGooglePlayServicesAvailable())
        key(FIREBASE_CUSTOM_KEY_INSTALLATION_SOURCE, getInstallationSource())
    }
}

private fun Context.getInstallationSource(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        packageManager.getInstallSourceInfo(packageName).initiatingPackageName
            ?: NOT_AVAILABLE
    } else {
        INSTALLATION_SOURCE_UNSUPPORTED
    }

private fun Context.isGooglePlayServicesAvailable(): Boolean =
    GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS

private const val FIREBASE_CUSTOM_KEY_ROUTE = "Current Screen"

fun NavBackStackEntry?.setAsFirebaseCustomKey() {
    Firebase.crashlytics.setCustomKeys {
        key(
            FIREBASE_CUSTOM_KEY_ROUTE,
            this@setAsFirebaseCustomKey?.destination?.route ?: NOT_AVAILABLE,
        )
    }
}
