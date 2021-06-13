package io.github.grantas33.kotlinpwacomponents

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import react.useEffect
import react.useState

/**
 * A hook that handles the registration of the service worker
 *
 * @param[serviceWorkerScriptUrl] Path to the service worker script file from the public folder
 */
fun useServiceWorker(serviceWorkerScriptUrl: String = "/serviceWorker.js"): ServiceWorkerState {

    val (serviceWorkerState, setServiceWorkerState) = useState<ServiceWorkerState>(ServiceWorkerState.Loading)

    suspend fun loadServiceWorkerState() {
        try {
            val swRegistration = window.navigator.serviceWorker.register(serviceWorkerScriptUrl).await()
            setServiceWorkerState(ServiceWorkerState.Registered(swRegistration = swRegistration))
        } catch (e: Exception) {
            setServiceWorkerState(ServiceWorkerState.Failed(exception = e))
        }
    }

    useEffect(dependencies = listOf()) {
        scope.launch {
            loadServiceWorkerState()
        }
    }

    return serviceWorkerState
}