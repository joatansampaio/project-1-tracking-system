package edu.metrostate.trackingsystem.domain.models;

import edu.metrostate.trackingsystem.application.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void createVehicle() {
        var vehicle = new Vehicle();
        assertNotNull(vehicle);
    }

    @Test
    void setVehicleId() {
        var vehicle = new Vehicle();
        assertNull(vehicle.getVehicleId());
        vehicle.setVehicleId("123");
        assertEquals("123", vehicle.getVehicleId());
    }

    @Test
    void getVehicleId() {
        assertEquals("123", getVehicleExample().getVehicleId());
    }

    @Test
    void getManufacturer() {
        assertEquals("Ford", getVehicleExample().getManufacturer());
    }

    @Test
    void setManufacturer() {
        var vehicle = getVehicleExample();
        assertEquals("Ford", vehicle.getManufacturer());
        vehicle.setManufacturer("Chevy");
        assertEquals("Chevy", vehicle.getManufacturer());
    }

    @Test
    void getModel() {
        assertEquals("F-150", getVehicleExample().getModel());
    }

    @Test
    void setModel() {
        var vehicle = getVehicleExample();
        assertEquals("F-150", vehicle.getModel());
        vehicle.setModel("F-550");
        assertEquals("F-550", vehicle.getModel());
    }

    @Test
    void getAcquisitionDate() {
        assertEquals("01/01/2025 00:00", getVehicleExample().getFormattedAcquisitionDate());
        assertEquals(1735711200000L, getVehicleExample().getAcquisitionDate());
    }

    @Test
    void setAcquisitionDate() {
        var vehicle = getVehicleExample();
        assertEquals("01/01/2025 00:00", vehicle.getFormattedAcquisitionDate());
        vehicle.setAcquisitionDate(1715354694451L);
        assertEquals("05/10/2024 10:24", vehicle.getFormattedAcquisitionDate());
        vehicle.setAcquisitionDate(null);
        assertEquals("Unknown", vehicle.getFormattedAcquisitionDate());
    }

    @Test
    void getDealershipId() {
        assertEquals("456", getVehicleExample().getDealershipId());
    }

    @Test
    void setDealershipId() {
        var vehicle = getVehicleExample();
        vehicle.setDealershipId("890");
        assertEquals("890", vehicle.getDealershipId());
    }

    @Test
    void getType() {
        assertEquals("PICKUP", getVehicleExample().getType());
    }

    @Test
    void setType() {
        var vehicle = getVehicleExample();
        vehicle.setType("suv");
        vehicle.normalize();
        // we maintain constant upper case.
        assertEquals("SUV", vehicle.getType());
    }

    @Test
    void testToString() {

        var expected = """
                ID: 123
                Dealership ID: 456
                Manufacturer: Ford Model: F-150\tType: PICKUP
                Price: $ 20000.00 dollars
                Acquisition Date: 01/01/2025 00:00
                """;

        assertEquals(expected, getVehicleExample().toString());
    }

    @Test
    public void rentTest() {
        var vehicle = getVehicleExample();
        assertFalse(vehicle.getIsRented());
        assertEquals("No", vehicle.getIsRentedAsString());

        vehicle.toggleIsRented();
        assertTrue(vehicle.getIsRented());
        assertEquals("Yes", vehicle.getIsRentedAsString());

        vehicle.setIsRented(false);
        assertFalse(vehicle.getIsRented());
    }

    @Test
    public void sportsCarCantBeRented() {
        var vehicle = getVehicleExample();
        assertFalse(vehicle.getIsRented());

        vehicle.setType("sports car");
        vehicle.toggleIsRented();
        vehicle.setIsRented(true);
        assertFalse(vehicle.getIsRented());

        vehicle.setIsRented(false);
        assertFalse(vehicle.getIsRented());

    }

    @Test
    public void createVehicleTest() {
        // Missing ID
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle(null, "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        // Missing Manufacturer
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", null, "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        // Missing Model
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", null, "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        // Missing Price
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", null, LocalDate.of(2025, 1, 1), "456", "pickup"));
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "", LocalDate.of(2025, 1, 1), "456", "pickup"));
        // Negative Price
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "-2", LocalDate.of(2025, 1, 1), "456", "pickup"));
        // Invalid Price
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup"));
        // Missing Acquisition Date
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", null, "456", "pickup"));
        // Missing Dealership ID
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), null, "pickup"));
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "", "pickup"));
        // Missing Type
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", null));
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", ""));
        // Invalid Type
        assertThrows(ValidationException.class, () -> Vehicle.createVehicle("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "I don't even exist"));

        try {
            Vehicle.createVehicle(null, null, null, "2213SSS", null, null, null);
        } catch (ValidationException e) {
            assertEquals(7, e.getValidationErrors().size());
        }
    }

    @Test
    public void getPrice() {
        var vehicle = getVehicleExample();
        assertEquals(20000, vehicle.getPrice().getPrice());
        assertEquals("dollars", vehicle.getPrice().getCurrency());
        assertEquals("$ 20000.00 dollars", vehicle.getPriceAsString());
    }

    @Test
    public void setPrice() {
        var vehicle = getVehicleExample();
        vehicle.setPrice(new Price(50000, "pounds"));
        assertEquals(50000, vehicle.getPrice().getPrice());
        assertEquals("pounds", vehicle.getPrice().getCurrency());
        assertEquals("£ 50000.00 pounds", vehicle.getPriceAsString());
    }

    @Test
    public void dealershipNameTest() {
        var vehicle = getVehicleExample();
        assertNull(vehicle.getDealershipName());
        vehicle.setDealershipName("test");
        assertEquals("test", vehicle.getDealershipName());
    }

    private Vehicle getVehicleExample() {
        try {
            return Vehicle.createVehicle(
                    "123",
                    "Ford",
                    "F-150",
                    "20000",
                    LocalDate.of(2025, 1, 1),
                    "456",
                    "pickup");
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
}