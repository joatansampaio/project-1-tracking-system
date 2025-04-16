package edu.metrostate.dealership.domain.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DealerTest {

    @Test
    fun isEnabledForAcquisition() {
        assertTrue(dealerExample.enabledForAcquisition)
    }

    @Test
    fun setEnabledForAcquisition() {
        val dealer = dealerExample
        dealer.enabledForAcquisition = false
        assertFalse(dealer.enabledForAcquisition)
        dealer.enabledForAcquisition = true
        assertTrue(dealer.enabledForAcquisition)
    }

    @Test
    fun getDealershipId() {
        assertEquals("456", dealerExample.dealershipId)
    }

    @Test
    fun setDealershipId() {
        val dealer = dealerExample
        dealer.dealershipId = "1111"
        assertEquals("1111", dealer.dealershipId)
    }

    @Test
    fun getName() {
        assertEquals("Bob Auto Mall", dealerExample.name)
        assertEquals("Unknown", Dealer("111").name)
    }

    @Test
    fun setName() {
        val dealer = dealerExample
        dealer.name = "Lucas Auto"
        assertEquals("Lucas Auto", dealer.name)
    }

    private val dealerExample: Dealer
        get() = Dealer("456", "Bob Auto Mall", true)
}
