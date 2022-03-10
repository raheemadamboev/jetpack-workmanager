# jetpack-workmanager
Simple application that downloads Eminem image from internet and applies green color filter as well as displaying it in the screen. Image is stored in the Imgur server. Image is downloaded with the help of Retrofit and it is executed in the background using Kotlin Coroutines in IO dispatcher. Also downloading image is not dependent on always opening the app because WorkManager is used to execute that work. Two worker is chained uniquely and second one (worker that applies color filter to downloaded image) waits for first one to finish as it needs to get data from first worker. It only executes the work if device is connected to internet connection. UI is built by Jetpack Compose and MVVM architectural pattern is applied. Color filter is applied using Canvas API. Dependencies are injected using Dagger Hilt. Also dependecies are injected to Worker using Hilt. Images are displayed to image composable using Coil.

**Jetpack WorkManager**

<img src="" />

<a href="https://github.com/raheemadamboev/jetpack-workmanager/blob/master/app-debug.apk">Download demo</a>

**Tech stack:**

- MVVM
- Jetpack Compose
- Hilt Dependency Injection
- Jetpack WorkManager
- Retrofit
- Kotlin Coroutines
- LiveData, State
