package co.mainmethod.chop.di

import co.mainmethod.chop.app.ChopApp
import co.mainmethod.chop.app.AppUpdateHelper
import co.mainmethod.chop.create.CreateActivity
import co.mainmethod.chop.task.TaskProcessorService
import dagger.Component
import javax.inject.Singleton

/**
 * Created by evan on 12/4/17.
 */
@Singleton
@Component(modules = [(AppModule::class), (DataModule::class), (AnalyticsModule::class)])
interface AppComponent {

    fun inject(app: ChopApp)
    fun inject(updateHelper: AppUpdateHelper)
    fun inject(activity: CreateActivity)
    fun inject(service: TaskProcessorService)

}