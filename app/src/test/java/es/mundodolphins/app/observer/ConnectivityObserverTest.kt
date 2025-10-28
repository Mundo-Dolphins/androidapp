package es.mundodolphins.app.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(RobolectricTestRunner::class)
class ConnectivityObserverTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Helper to observe LiveData<Boolean> values synchronously in tests.
    private fun LiveData<Boolean>.getOrAwaitValueBoolean(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): Boolean {
        val data = arrayOfNulls<Boolean>(1)
        val latch = CountDownLatch(1)
        val observer = androidx.lifecycle.Observer<Boolean> { t ->
            data[0] = t
            latch.countDown()
        }

        try {
            this.observeForever(observer)

            // If a value is already present it will release immediately
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was not set within the timeout")
            }

            return data[0] as Boolean
        } finally {
            this.removeObserver(observer)
        }
    }

    @Test
    fun `onAvailable should post true to isConnected`() {
        val context = mockk<Context>(relaxed = true)
        val connectivityManager = mockk<ConnectivityManager>(relaxed = true)

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        // Provide a fake NetworkRequest to avoid using NetworkRequest.Builder in tests (Robolectric environment)
        val fakeRequest = mockk<android.net.NetworkRequest>(relaxed = true)
        every { connectivityManager.registerNetworkCallback(any<android.net.NetworkRequest>(), capture(callbackSlot)) } returns Unit

        val observer = ConnectivityObserver(connectivityManager, fakeRequest)

        // Simulate network available
        val network = mockk<Network>()
        callbackSlot.captured.onAvailable(network)

        // Run posted tasks on main looper so postValue takes effect
        shadowOf(Looper.getMainLooper()).idle()

        val value = observer.isConnected.getOrAwaitValueBoolean()
        assertThat(value).isTrue()

        verify { connectivityManager.registerNetworkCallback(any<android.net.NetworkRequest>(), any<ConnectivityManager.NetworkCallback>()) }
    }

    @Test
    fun `onLost should post false to isConnected`() {
        val context = mockk<Context>(relaxed = true)
        val connectivityManager = mockk<ConnectivityManager>(relaxed = true)

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        val fakeRequest = mockk<android.net.NetworkRequest>(relaxed = true)
        every { connectivityManager.registerNetworkCallback(any<android.net.NetworkRequest>(), capture(callbackSlot)) } returns Unit

        val observer = ConnectivityObserver(connectivityManager, fakeRequest)

        // Simulate network lost
        val network = mockk<Network>()
        callbackSlot.captured.onLost(network)

        // Run posted tasks on main looper so postValue takes effect
        shadowOf(Looper.getMainLooper()).idle()

        val value = observer.isConnected.getOrAwaitValueBoolean()
        assertThat(value).isFalse()

        verify { connectivityManager.registerNetworkCallback(any<android.net.NetworkRequest>(), any<ConnectivityManager.NetworkCallback>()) }
    }
}
