package pl.adriandefus.placesapi.ui.fragment.predictions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_predictions.*
import pl.adriandefus.placesapi.R
import pl.adriandefus.placesapi.di.utils.AppViewModelFactory
import pl.adriandefus.placesapi.domain.model.PlaceInformation
import pl.adriandefus.placesapi.domain.model.base.DataStatus
import pl.adriandefus.placesapi.domain.model.base.Status
import pl.adriandefus.placesapi.ui.FavouritesViewModel
import pl.adriandefus.placesapi.ui.MainActivity.Companion.PLACE_ID
import pl.adriandefus.placesapi.ui.PlacesViewModel
import pl.adriandefus.placesapi.remote.handler.PlaceSearchZeroResultError
import pl.adriandefus.placesapi.util.getViewModel
import pl.adriandefus.placesapi.util.navigate
import pl.adriandefus.placesapi.util.visibility
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PredictionsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory

    private val placesViewModel: PlacesViewModel? by lazy {
        viewModelFactory.getViewModel<PlacesViewModel>(activity)
    }

    private val favouritesViewModel: FavouritesViewModel? by lazy {
        viewModelFactory.getViewModel<FavouritesViewModel>(activity)
    }

    private val placeQuerySubject = PublishSubject.create<String>()

    private val predictionsAdapter by lazy {
        PredictionsAdapter(predictionsClickCallback)
    }

    private val predictionsClickCallback = object : PredictionsClickCallback {
        override fun onPredictionSelected(placeId: String) {
            openMapWithPlace(placeId)
        }

        override fun onMarkAsFavouriteSelected(placeInformation: PlaceInformation) {
            if (placeInformation.isFavourite) {
                favouritesViewModel?.removePlaceInformationFromFavourites(placeInformation)
            } else {
                favouritesViewModel?.addPlaceInformationToFavourites(placeInformation)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_predictions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initViewModelObservers()
        initSearchInput()
        initPlaceQuerySubject()
        initClickListeners()
    }

    override fun onStop() {
        placesViewModel?.clearAutocompleteStatus()
        super.onStop()
    }

    private fun initRecycler() {
        placesRecycler.apply {
            adapter = predictionsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }
    }

    private fun initViewModelObservers() {
        placesViewModel?.placeAutoCompleteStatus?.observe(this, placeAutoCompleteStatusObserver)
        favouritesViewModel?.addPlaceInformationStatus?.observe(this, addPlaceInformationStatusObserver)
        favouritesViewModel?.removePlaceInformationStatus?.observe(this, removePlaceInformationStatusObserver)
        favouritesViewModel?.favouritePlacesSizeStatus?.observe(this, favouritePlacesSizeStatusObserver)

        favouritesViewModel?.observeFavouritePlaces()
    }

    private fun initSearchInput() {
        searchInput.addTextChangedListener(
            afterTextChanged = { editable ->
                editable?.let {
                    searchCancel visibility it.isNotEmpty()
                    placeQuerySubject.onNext(it.toString().trim())
                }
            }
        )
    }

    @SuppressLint("CheckResult")
    private fun initPlaceQuerySubject() {
        placeQuerySubject
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { it.isEmpty() || it.length > 2 }
            .subscribe {
                placesViewModel?.performAutocompleteForQuery(it)
            }
    }

    private fun initClickListeners() {
        favouritesContainer.setOnClickListener {
            openFavourites()
        }

        searchCancel.setOnClickListener {
            searchInput.setText("")
        }
    }

    private fun setQueryProgress(isVisible: Boolean?) {
        searchProgress visibility isVisible
    }

    private fun setQueryError(error: Throwable?) {
        if (error is PlaceSearchZeroResultError) {
            placesEmptyInformation visibility true
            predictionsAdapter.submitList(listOf())
        } else {
            Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMapWithPlace(placeId: String) {
        navigate(R.id.actionOpenMapFragmentFromPredictions, bundleOf(Pair(PLACE_ID, placeId)))
    }

    private fun openFavourites() {
        navigate(R.id.actionOpenFavouritesFragment)
    }

    private val placeAutoCompleteStatusObserver = Observer<DataStatus<List<PlaceInformation>>> {
        when (it.status) {
            Status.PROGRESS -> setQueryProgress(true)
            Status.ERROR -> {
                setQueryProgress(false)
                setQueryError(it.error)
            }
            Status.SUCCESS -> {
                setQueryProgress(false)
                placesEmptyInformation visibility null
                predictionsAdapter.submitList(it.data)
            }
            Status.DEFAULT -> setQueryProgress(false)
        }
    }

    private val addPlaceInformationStatusObserver = Observer<DataStatus<Unit>> {
        when (it.status) {
            Status.SUCCESS -> predictionsAdapter.notifyDataSetChanged()
            Status.ERROR ->
                Toast.makeText(
                    context,
                    getString(R.string.error_adding_place_to_favourites),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private val removePlaceInformationStatusObserver = Observer<DataStatus<Unit>> {
        when (it.status) {
            Status.SUCCESS -> predictionsAdapter.notifyDataSetChanged()
            Status.ERROR ->
                Toast.makeText(
                    context,
                    getString(R.string.error_removing_place_from_favourites),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private val favouritePlacesSizeStatusObserver = Observer<DataStatus<Int>> {
        if (it.status == Status.SUCCESS)
            favouritesAmountBadge.text = it.data?.toString()
    }
}