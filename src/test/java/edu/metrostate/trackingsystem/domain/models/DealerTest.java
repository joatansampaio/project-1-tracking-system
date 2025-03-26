package edu.metrostate.trackingsystem.domain.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    private final Vehicle vehicle = new Vehicle("666", "Ford", "F-550",
        123123123123L, new Price(50000, "dollars"),
        "456", "pickup");

    @Test
    void addVehicle() {
        var dealer = getDealerExample();
        assertTrue(dealer.addVehicle(vehicle));
        assertEquals(2, dealer.getVehicles().size());
        // ID already added, should not add again.
        assertFalse(dealer.addVehicle(vehicle));
    }

    @Test
    void getEnabledForAcquisition() {
        assertTrue(getDealerExample().getEnabledForAcquisition());
    }

    @Test
    void setEnabledForAcquisition() {
        var dealer = getDealerExample();
        dealer.setEnabledForAcquisition(false);
        assertFalse(dealer.getEnabledForAcquisition());
        dealer.setEnabledForAcquisition(true);
        assertTrue(dealer.getEnabledForAcquisition());
    }

    @Test
    void getDealershipId() {
        assertEquals("456", getDealerExample().getDealershipId());
    }

    @Test
    void setDealershipId() {
        var dealer = getDealerExample();
        dealer.setDealershipId("1111");
        assertEquals("1111", dealer.getDealershipId());
    }

    @Test
    void getVehicles() {
        assertEquals(1, getDealerExample().getVehicles().size());
        assertEquals(1, getDealerExample().getObservableVehicles().size());
    }

    @Test
    void setVehicles() {
        var dealer = getDealerExample();
        dealer.setVehicles(List.of());
        assertEquals(0, dealer.getVehicles().size());
    }

    @Test
    void getName() {
        assertEquals("Bob Auto Mall", getDealerExample().getName());
        assertEquals("Not Configured", new Dealer("111").getName());
    }

    @Test
    void setName() {
        var dealer = getDealerExample();
        dealer.setName("Lucas Auto");
        assertEquals("Lucas Auto", dealer.getName());
    }

    private Dealer getDealerExample() {
        var vehicles = new ArrayList<Vehicle>();
        vehicles.add(new Vehicle("123", "Ford", "F-150",
                123123123123L, new Price(20000, "dollars"),
                "456", "pickup"));
        return new Dealer("456", vehicles, "Bob Auto Mall");
    }
}