package pl.adriandefus.placesapi.di.module.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.adriandefus.placesapi.ui.fragment.favourites.FavouritesFragment
import pl.adriandefus.placesapi.ui.fragment.MapFragment
import pl.adriandefus.placesapi.ui.fragment.predictions.PredictionsFragment

@Module
abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    abstract fun bindPredictionsFragment(): PredictionsFragment

    @ContributesAndroidInjector
    abstract fun bindMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun bindFavouritesFragment(): FavouritesFragment
}