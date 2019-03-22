package pl.adriandefus.placesapi.di.module.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import pl.adriandefus.placesapi.ui.PlacesViewModel
import pl.adriandefus.placesapi.di.annotation.ViewModelKey
import pl.adriandefus.placesapi.ui.FavouritesViewModel

@Module
abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlacesViewModel::class)
    abstract fun bindPlacesViewModel(placesViewModel: PlacesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesViewModel::class)
    abstract fun bindFavouritesViewModel(favouritesViewModel: FavouritesViewModel): ViewModel
}