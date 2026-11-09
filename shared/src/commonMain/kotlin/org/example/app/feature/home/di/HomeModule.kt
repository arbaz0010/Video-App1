package org.example.app.feature.home.di

import org.example.app.feature.home.presentation.HomeStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val homeModule = module {
    factoryOf(::HomeStore)
}
