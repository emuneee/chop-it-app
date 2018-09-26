package co.mainmethod.chop.app

import android.app.Application
import android.content.Context
import co.mainmethod.chop.BuildConfig
import co.mainmethod.chop.di.*
import timber.log.Timber

/**
 * Created by evan on 10/7/17.
 */
class ChopApp : Application() {

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .dataModule(DataModule())
                .analyticsModule(AnalyticsModule())
                .build()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // all remaining should come after this point
        AppUpdateHelper().checkForUpdate()
    }

    companion object {
        lateinit var component: AppComponent
    }

}

val Context.app
    get() = applicationContext as ChopApp