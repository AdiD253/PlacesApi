package pl.adriandefus.placesapi.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.adriandefus.placesapi.di.module.main.MainActivityModule
import pl.adriandefus.placesapi.di.module.main.MainFragmentBindingModule
import pl.adriandefus.placesapi.ui.MainActivity

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [
        MainActivityModule::class,
        MainFragmentBindingModule::class
    ])
    abstract fun bindMainActivity(): MainActivity
}