package co.mainmethod.chop.di

import android.content.Context
import co.mainmethod.chop.app.ChopApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * The application module, in this simple example, will only return the instance of the application itself.
 * Created by evan on 12/4/17.
 */
@Module
class AppModule(val app: ChopApp) {

    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideContext(): Context = app

}
