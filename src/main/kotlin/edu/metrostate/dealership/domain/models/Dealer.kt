package edu.metrostate.dealership.domain.models

class Dealer(
    var dealershipId: String,
    var name: String = "Unknown",
    var enabledForAcquisition: Boolean = true
)