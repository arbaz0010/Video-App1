package org.example.app.core.di

import org.koin.dsl.module

/**
 * Cross-cutting singletons used by every feature live here:
 * settings, http client, logger, etc. Add bindings as you build.
 */
val coreModule = module {
    // single<HttpClient> { HttpClient { /* … */ } }
    // single<Settings> { /* multiplatform-settings */ }
}
