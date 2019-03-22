package pl.adriandefus.placesapi.util

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import pl.adriandefus.placesapi.di.utils.AppViewModelFactory

@Throws(IllegalArgumentException::class)
inline fun <reified T : ViewModel> AppViewModelFactory.getViewModel(activity: FragmentActivity?): T? {
    activity ?: return null
    return androidx.lifecycle.ViewModelProviders.of(activity, this).get(T::class.java)
}

infix fun <T> MutableLiveData<T>.post(value: T) {
    postValue(value)
}

fun Fragment.navigate(@IdRes actionId: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
    NavHostFragment.findNavController(this).navigate(actionId, args, navOptions)
}

infix fun View.visibility(visibility: Boolean?) {
    when (visibility) {
        true -> setVisibility(View.VISIBLE)
        false -> setVisibility(View.INVISIBLE)
        null -> setVisibility(View.GONE)
    }
}