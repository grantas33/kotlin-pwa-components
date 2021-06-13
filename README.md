[![](https://jitpack.io/v/grantas33/kotlin-pwa-components.svg)](https://jitpack.io/#grantas33/kotlin-pwa-components)
# Module kotlin-pwa-components

This module includes two utility hook functions and can be used in progressive web applications which:
* Use Kotlin.js
* Provide a service worker script file for the browser
* Use [Kotlin React wrapper](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-react) library

[Kotlin PWA starter kit](https://github.com/grantas33/Kotlin-PWA-starter-kit) includes a sample of the hooks in action:

```
val serviceWorkerState = useServiceWorker()
val (pushManagerState, subscribeUser, unsubscribeUser) = usePushManager(
    serviceWorkerState = serviceWorkerState,
    publicKey = "BLceSSynHW5gDWDz-SK5mmQgUSAOzs_yXMPtDO0AmNsRjUllTZsdmDU4_gKvTr_q1hA8ZX19xLbGe28Bkyvwm3E"
)
```
