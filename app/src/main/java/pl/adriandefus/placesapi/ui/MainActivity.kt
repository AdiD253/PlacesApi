package pl.adriandefus.placesapi.ui

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import pl.adriandefus.placesapi.R

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        const val PLACE_ID = "place_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
