package pl.adriandefus.placesapi.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_map.*
import pl.adriandefus.placesapi.R
import pl.adriandefus.placesapi.di.utils.AppViewModelFactory
import pl.adriandefus.placesapi.domain.model.Place
import pl.adriandefus.placesapi.domain.model.base.DataStatus
import pl.adriandefus.placesapi.domain.model.base.Status
import pl.adriandefus.placesapi.ui.MainActivity.Companion.PLACE_ID
import pl.adriandefus.placesapi.ui.PlacesViewModel
import pl.adriandefus.placesapi.util.getViewModel
import pl.adriandefus.placesapi.util.visibility
import javax.inject.Inject

class MapFragment : DaggerFragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory

    private val placesViewModel: PlacesViewModel? by lazy {
        viewModelFactory.getViewModel<PlacesViewModel>(activity)
    }
    private lateinit var map: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMapComponents()
    }

    override fun onStop() {
        placesViewModel?.clearPlaceStatus()
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        arguments?.getString(PLACE_ID)?.let { placeId ->
            placesViewModel?.placeInfoStatus?.observe(this, placeStatusObserver)
            placesViewModel?.getPlaceForId(placeId)
        }
    }

    private fun initMapComponents() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun placeMarkerOnMap(position: LatLng, title: String? = null): Marker {
        val markerOptions = MarkerOptions().position(position).title(title)
        return map.addMarker(markerOptions)
    }

    private fun zoomToLocation(location: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(getBoundsFromCenterRadius(location, 250), 100))
    }

    private fun getBoundsFromCenterRadius(center: LatLng, radiusInMeters: Int): LatLngBounds {
        val distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0)
        val southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
        val northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
        return LatLngBounds(southwestCorner, northeastCorner)
    }

    private val placeStatusObserver = Observer<DataStatus<Place>> {
        placeLoadingProgress visibility null
        when (it.status) {
            Status.PROGRESS -> placeLoadingProgress visibility true
            Status.ERROR -> Toast.makeText(context, getString(R.string.error_obtaining_place_information), Toast.LENGTH_SHORT).show()
            Status.SUCCESS -> {
                it.data?.let { place ->
                    placeMarkerOnMap(place.location, place.name)
                    placeDescription.text = place.name
                    zoomToLocation(place.location)
                }
            }
            Status.DEFAULT -> map.clear()
        }
    }
}