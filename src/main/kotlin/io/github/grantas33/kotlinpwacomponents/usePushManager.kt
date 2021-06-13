package io.github.grantas33.kotlinpwacomponents

import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import react.useEffect
import react.useState

data class UsePushManager(
    val pushManagerState: PushManagerState,
    val subscribeUser : suspend (notSubscribedState: PushManagerState.NotSubscribed, handleSubscription: (suspend (subscription: PushSubscription) -> Unit)?) -> Unit,
    val unsubscribeUser: suspend (subscribedState: PushManagerState.Subscribed) -> Unit
)

fun usePushManager(serviceWorkerState: ServiceWorkerState, publicKey: String): UsePushManager {

    val (pushManagerState, setPushManagerState) = useState<PushManagerState>(PushManagerState.NotLoaded)

    suspend fun loadPushManagerState(pushManager: PushManager?) {
        if (pushManager != null) {
            setPushManagerState(PushManagerState.Loading)
            pushManager.getSubscription().await().let {
                setPushManagerState(
                    if (it != null) {
                        PushManagerState.Subscribed(pushManager = pushManager)
                    } else {
                        PushManagerState.NotSubscribed(pushManager = pushManager)
                    }
                )
            }
        } else {
            setPushManagerState(PushManagerState.NotSupported)
        }
    }

    useEffect(dependencies = listOf()) {
        scope.launch {
            if (serviceWorkerState is ServiceWorkerState.Registered && pushManagerState is PushManagerState.NotLoaded) {
                loadPushManagerState(serviceWorkerState.swRegistration.pushManager)
            }
        }
    }

    useEffect(dependencies = listOf(serviceWorkerState)) {
        scope.launch {
            if (serviceWorkerState is ServiceWorkerState.Registered && pushManagerState is PushManagerState.NotLoaded) {
                loadPushManagerState(serviceWorkerState.swRegistration.pushManager)
            }
        }
    }

    suspend fun subscribeUser(
        pushManager: PushManager,
        handleSubscription: (suspend (subscription: PushSubscription) -> Unit)?
    ) {
        val subscription = try {
            pushManager.subscribe(
                PushSubscriptionOptions(userVisibleOnly = true, applicationServerKey = publicKey)
            ).await()
        } catch (e: Exception) {
            console.warn("Subscription denied - ${e.message}")
            return
        }

        handleSubscription?.let { it(subscription) }
        setPushManagerState(PushManagerState.Subscribed(pushManager))

    }

    suspend fun unsubscribeUser(pushManager: PushManager) {
        val subscription = pushManager.getSubscription().await()
        if (subscription != null) {
            try {
                subscription.unsubscribe().await()
                setPushManagerState(PushManagerState.NotSubscribed(pushManager))
            } catch (e: Exception) {
                console.error("User unsubscription failed: ${e.message}")
            }
        }
    }

    return UsePushManager(
        pushManagerState = pushManagerState,
        subscribeUser = { notSubscribedState, handleSubscription ->
            subscribeUser(pushManager = notSubscribedState.pushManager, handleSubscription = handleSubscription)
        },
        unsubscribeUser = { subscribedState ->
            unsubscribeUser(pushManager = subscribedState.pushManager)
        }
    )
}