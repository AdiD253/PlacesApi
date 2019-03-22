package pl.adriandefus.placesapi

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.realm.Realm
import pl.adriandefus.placesapi.di.DaggerAppComponent

class PlacesApiApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder()
            .application(this)
            .build()

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}