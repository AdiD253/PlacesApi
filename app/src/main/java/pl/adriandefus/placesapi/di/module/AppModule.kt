package pl.adriandefus.placesapi.di.module

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import pl.adriandefus.placesapi.di.utils.AppViewModelFactory
import pl.adriandefus.placesapi.domain.db.RealmService

@Module
class AppModule {

    @Provides
    fun provideViewModelFactory(adminAppViewModelFactory: AppViewModelFactory): ViewModelProvider.Factory {
        return adminAppViewModelFactory
    }

    @Provides
    fun provideDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideRealm(): Realm {
        return Realm.getDefaultInstance()
    }

    @Provides
    fun provideRealmService(realm: Realm): RealmService {
        return RealmService(realm)
    }
}