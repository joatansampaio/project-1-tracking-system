package edu.metrostate.dealership.domain.models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DealerTest {
    @get:Test
    val enabledForAcquisition: Unit
        get() {
            Assertions.assertTrue(dealerExample.enabledForAcquisition)
        }

    @Test
    fun setEnabledForAcquisition() {
        val dealer = dealerExample
        dealer.enabledForAcquisition = false
        Assertions.assertFalse(dealer.enabledForAcquisition)
        dealer.enabledForAcquisition = true
        Assertions.assertTrue(dealer.enabledForAcquisition)
    }

    @get:Test
    val dealershipId: Unit
        get() {
            Assertions.assertEquals("456", dealerExample.dealershipId)
        }

    @Test
    fun setDealershipId() {
        val dealer = dealerExample
        dealer.dealershipId = "1111"
        Assertions.assertEquals("1111", dealer.dealershipId)
    }

    @get:Test
    val name: Unit
        get() {
            Assertions.assertEquals("Bob Auto Mall", dealerExample.getName())
            Assertions.assertEquals("Not Configured", Dealer("111", null, true).getName())
        }

    @Test
    fun setName() {
        val dealer = dealerExample
        dealer.setName("Lucas Auto")
        Assertions.assertEquals("Lucas Auto", dealer.getName())
    }

    private val dealerExample: Dealer
        get() = Dealer("456", "Bob Auto Mall", true)
}