package com.bike.computer

import btools.router.NavHint

data class RouteResult(
    val points: List<DoubleArray>,
    val distanceM: Int,
    val ascendM: Int,
    val steps: List<NavHint>,
)
