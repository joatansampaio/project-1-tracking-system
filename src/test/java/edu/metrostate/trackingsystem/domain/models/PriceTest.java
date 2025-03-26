package edu.metrostate.trackingsystem.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class PriceTest {

    @Test
    public void createPrice() {
        var price = new Price();
        assertNotNull(price);
    }

    @Test
    void getPrice() {
        var price = new Price(500, "dollars");
        assertEquals(500, price.getPrice());
    }

    @Test
    void setPrice() {
        var price = new Price(0, "dollars");
        assertEquals(0, price.getPrice());
        price.setPrice(5000);
        assertEquals(5000, price.getPrice());
    }

    @Test
    void getCurrency() {
        var price = new Price(0, "dollars");
        assertEquals("dollars", price.getCurrency());
    }

    @Test
    void setCurrency() {
        var price = new Price(0, "dollars");
        assertEquals("dollars", price.getCurrency());
        price.setCurrency("pounds");
        assertEquals("pounds", price.getCurrency());
    }

    @Test
    void testToString() {
        assertPrice("dollars", "$ 50.00 dollars");
        assertPrice("pounds", "£ 50.00 pounds");
        assertPrice("euros", "€ 50.00 euros");
        assertPrice("yen", "¥ 50.00 yen");
        assertPrice("rupees", "₹ 50.00 rupees");
        assertPrice("whatever", "50.00 whatever");
    }

    void assertPrice(String currency, String expected) {
        var price = new Price(50, currency);
        assertEquals(expected, price.toString());
    }
}