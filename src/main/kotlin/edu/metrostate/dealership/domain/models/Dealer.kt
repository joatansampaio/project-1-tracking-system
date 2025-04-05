package edu.metrostate.dealership.domain.models

class Dealer(
    val dealershipId: String,
    name: String? = null,
    var enabledForAcquisition: Boolean = true
) {
    private var name: String = name ?: "Not Configured"

    fun getName(): String = name

    fun setName(newName: String) {
        name = newName
    }
}