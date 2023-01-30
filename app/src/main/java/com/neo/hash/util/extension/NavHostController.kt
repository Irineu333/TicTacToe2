@file:Suppress("unused")

package com.neo.hash.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController

@Composable
fun NavHostController.currentDestinationAsState() =
    produceState<NavDestination?>(null) {
        currentBackStackEntryFlow.collect {
            value = it.destination
        }
    }

@Composable
fun NavHostController.currentRouteAsState() =
    produceState<String?>(null) {
        currentBackStackEntryFlow.collect {
            value = it.destination.route
        }
    }