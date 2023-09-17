# dog-ceo-android
Android library wrapper of the Dog CEO API.

This is a demo project written in 7 days for a job interview task.

## Task

The main objective of the wrapper was to showcase the use of an Android library module, exposing methods to get a random image, get next and previous images and multiple images.

## Architectural Overview and usage

The app uses the MVVM design pattern. The View and View Model are part of the demo application, the whole Model is part of the SDK. 
The SDK (Model) exposes it's methods as Kotlin Flows that the ViewModel should observe and propagate the results to the View layer.
The SDK is responsible for caching the images which are saved as files in the cache directory.
Testing of the lib is done via instrumentation tests in order to be able to use Android Context. It's done via the JUnit framework and io.mockk

## Known defects and future improvements

1. The library keeps the loaded Bitmaps in a runtime list. This creates a huge memory management issue when a lot of images are loaded. The list has to be replaced by an LRU in-memory cache and everything else to be loaded from disk.
2. The loading of the images in the list screen is done via an external library (Glide) and is done on the View layer directly. This must be refactored to be done in the SDK itself and observed in the View via the View Model.
3. Failed loading (due to no connectivity or an API error) is never retried.
4. Test coverage needs to be improved.