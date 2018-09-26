package co.mainmethod.chop.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

/**
 * Created by evan on 2/3/18.
 */
class FirebaseAnalyticsTracker(context: Context) : AnalyticsTracker {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(event: AnalyticsTracker.Event, param: Pair<AnalyticsTracker.Param, String>?) {
        val params = Bundle()

        if (param != null) {
            params.putString(param.first.value, param.second)
        }
        logEvent(event, params)
    }

    override fun logEvent(event: AnalyticsTracker.Event, params: Bundle) {
        Timber.d("logEvent ${event.label}, $params")
        firebaseAnalytics.logEvent(event.label, params)
    }
}