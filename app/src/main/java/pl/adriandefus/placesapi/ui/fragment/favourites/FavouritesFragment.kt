package pl.adriandefus.placesapi.ui.fragment.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_favourites.*
import pl.adriandefus.placesapi.R
import pl.adriandefus.placesapi.di.utils.AppViewModelFactory
import pl.adriandefus.placesapi.domain.model.PlaceInformation
import pl.adriandefus.placesapi.domain.model.base.DataStatus
import pl.adriandefus.placesapi.domain.model.base.Status
import pl.adriandefus.placesapi.ui.FavouritesViewModel
import pl.adriandefus.placesapi.ui.MainActivity.Companion.PLACE_ID
import pl.adriandefus.placesapi.util.getViewModel
import pl.adriandefus.placesapi.util.navigate
import pl.adriandefus.placesapi.util.visibility
import javax.inject.Inject

class FavouritesFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory

    private val favouritesViewModel: FavouritesViewModel? by lazy {
        viewModelFactory.getViewModel<FavouritesViewModel>(activity)
    }

    private val favouriteClickCallback = object : FavouriteClickCallback {
        override fun onFavouriteSelected(placeId: String) {
            openMapWithPlace(placeId)
        }

        override fun onRemoveItemSelected(placeInformation: PlaceInformation) {
            favouritesViewModel?.removePlaceInformationFromFavourites(placeInformation)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_favourites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initViewModelObservers()
    }

    private fun initRecycler() {
        favouriteItemsRecycler.apply {
            adapter = FavouritesAdapter(
                favouritesViewModel?.getFavouritePlaces(),
                favouriteClickCallback
            )
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }
    }

    private fun initViewModelObservers() {
        favouritesViewModel?.favouritePlacesSizeStatus?.observe(this, favouritePlacesSizeStatusObserver)
        favouritesViewModel?.observeFavouritePlaces()
    }

    private fun openMapWithPlace(placeId: String) {
        navigate(R.id.actionOpenMapFragmentFromFavourites, bundleOf(Pair(PLACE_ID, placeId)))
    }

    private val favouritePlacesSizeStatusObserver = Observer<DataStatus<Int>> {
        if (it.status == Status.SUCCESS) {
            favouriteItemsEmptyInformation visibility
                    if (it.data == 0) true
                    else null
        }
    }
}