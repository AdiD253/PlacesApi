package pl.adriandefus.placesapi.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import pl.adriandefus.placesapi.PlacesApiApplication
import pl.adriandefus.placesapi.di.module.ActivityBindingModule
import pl.adriandefus.placesapi.di.module.AppModule
import pl.adriandefus.placesapi.di.module.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityBindingModule::class,
    AppModule::class,
    NetworkModule::class
])
interface AppComponent : AndroidInjector<PlacesApiApplication> {

    override fun inject(application: PlacesApiApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}