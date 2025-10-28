package es.mundodolphins.app.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * ConnectivityObserver now allows injecting a ConnectivityManager and optionally
 * a NetworkRequest (useful for testing). It keeps a secondary constructor that accepts
 * a Context for production usage.
 */
class ConnectivityObserver(
    private val connectivityManager: ConnectivityManager,
    private val providedRequest: NetworkRequest? = null,
) {
    constructor(context: Context) : this(
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
        null,
    )

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.postValue(true)
            }

            override fun onLost(network: Network) {
                _isConnected.postValue(false)
            }
        }

    init {
        val networkRequest =
            providedRequest ?: NetworkRequest
                .Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}
