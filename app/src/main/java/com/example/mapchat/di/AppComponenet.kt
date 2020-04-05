package com.example.mapchat.di

import org.koin.core.module.Module

val AppComponent: List<Module> = listOf(
    FireModule, mapVewModelModule,
    chatViewModelModule, userViewModelModule, RepositoryModule
)