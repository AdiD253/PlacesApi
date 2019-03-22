package pl.adriandefus.placesapi.di.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory @Inject constructor(
    private var creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator: Provider<out ViewModel>? =
            creators[modelClass]
                ?: getCreator(modelClass)
                ?: throw IllegalArgumentException("unknown class $modelClass")

        try {
            return creator!!.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun<T> getCreator(modelClass: Class<T>): Provider<out ViewModel>? =
        creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value

}