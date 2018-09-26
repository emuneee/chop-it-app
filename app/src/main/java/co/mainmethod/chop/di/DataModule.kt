package co.mainmethod.chop.di

import android.content.Context
import co.mainmethod.chop.data.AppData
import co.mainmethod.chop.data.SharedAppData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by evan on 12/4/17.
 */
@Module
class  DataModule {

    @Provides
    @Singleton
    fun providerAppData(context: Context): AppData = SharedAppData(context)

}