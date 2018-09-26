package co.mainmethod.chop.di

import android.content.Context
import co.mainmethod.chop.analytics.AnalyticsTracker
import co.mainmethod.chop.analytics.FirebaseAnalyticsTracker
import co.mainmethod.chop.data.AppData
import co.mainmethod.chop.data.SharedAppData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by evan on 12/4/17.
 */
@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun providerAnalyticsTracker(context: Context): AnalyticsTracker = FirebaseAnalyticsTracker(context)

}