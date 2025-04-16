package edu.metrostate.dealership.domain.models

import edu.metrostate.dealership.application.exceptions.ValidationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class VehicleTest {

    @Test
    fun createVehicle() {
        val vehicle = getVehicleExample()
        assertNotNull(vehicle)
    }

    @Test
    fun setVehicleId() {
        val vehicle = getVehicleExample()
        assertEquals("123", vehicle.vehicleId)
        vehicle.vehicleId = "456"
        assertEquals("456", vehicle.vehicleId)
    }

    @Test
    fun getVehicleId() {
        assertEquals("123", getVehicleExample().vehicleId)
    }

    @Test
    fun getManufacturer() {
        assertEquals("Ford", getVehicleExample().manufacturer)
    }

    @Test
    fun setManufacturer() {
        val vehicle = getVehicleExample()
        assertEquals("Ford", vehicle.manufacturer)
        vehicle.manufacturer = "Chevy"
        assertEquals("Chevy", vehicle.manufacturer)
    }

    @Test
    fun getModel() {
        assertEquals("F-150", getVehicleExample().model)
    }

    @Test
    fun setModel() {
        val vehicle = getVehicleExample()
        assertEquals("F-150", vehicle.model)
        vehicle.model = "F-550"
        assertEquals("F-550", vehicle.model)
    }

    @Test
    fun getAcquisitionDate() {
        assertEquals("01/01/2025 00:00", getVehicleExample().formattedAcquisitionDate)
        assertEquals(1735711200000L, getVehicleExample().acquisitionDate)
    }

    @Test
    fun setAcquisitionDate() {
        val vehicle = getVehicleExample()
        assertEquals("01/01/2025 00:00", vehicle.formattedAcquisitionDate)
        vehicle.acquisitionDate = 1715354694451L
        assertEquals("05/10/2024 10:24", vehicle.formattedAcquisitionDate)
        vehicle.acquisitionDate = null
        assertEquals("Unknown", vehicle.formattedAcquisitionDate)
    }

    @Test
    fun getDealershipId() {
        assertEquals("456", getVehicleExample().dealershipId)
    }

    @Test
    fun setDealershipId() {
        val vehicle = getVehicleExample()
        vehicle.dealershipId = "890"
        assertEquals("890", vehicle.dealershipId)
    }

    @Test
    fun getType() {
        assertEquals(VehicleType.PICKUP, getVehicleExample().type)
    }

    @Test
    fun setType() {
        val vehicle = getVehicleExample()
        vehicle.type = VehicleType.SUV
        assertEquals(VehicleType.SUV, vehicle.type)
    }

    @Test
    fun rentTest() {
        val vehicle = getVehicleExample()
        assertFalse(vehicle.isRented)
        assertEquals("No", vehicle.isRentedAsString)

        vehicle.toggleIsRented()
        assertTrue(vehicle.isRented)
        assertEquals("Yes", vehicle.isRentedAsString)
    }

    @Test
    fun sportsCarCantBeRented() {
        val vehicle = getVehicleExample()
        assertFalse(vehicle.isRented)

        vehicle.type = VehicleType.SPORTS_CAR
        vehicle.toggleIsRented()
        assertFalse(vehicle.isRented)
    }

    @Test
    fun createVehicleTest() {
        // Missing ID
        assertThrows(ValidationException::class.java) {
            Vehicle.create(null, "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup")
        }
        assertThrows(ValidationException::class.java) {
            Vehicle.create("", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        // Other validation test cases (same logic applies) ...
        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", null, "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", null, "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "", "2213SSS", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", null, LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "-2", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "invalid", LocalDate.of(2025, 1, 1), "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "2213SSS", null, "456", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), null, "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "", "pickup")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", null)
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "")
        }

        assertThrows(ValidationException::class.java) {
            Vehicle.create("123", "Ford", "F-150", "2213SSS", LocalDate.of(2025, 1, 1), "456", "I don't even exist")
        }

        val ex = assertThrows(ValidationException::class.java) {
            Vehicle.create(null, null, null, "2213SSS", null, null, null)
        }
        assertEquals(8, ex.validationErrors.size)
    }

    @Test
    fun getPrice() {
        val vehicle = getVehicleExample()
        assertEquals(20000.0, vehicle.price.price)
        assertEquals("dollars", vehicle.price.currency)
        assertEquals("$20000.00 dollars", vehicle.priceAsString)
    }

    @Test
    fun setPrice() {
        val vehicle = getVehicleExample()
        vehicle.price = Price(50000.0, "pounds")
        assertEquals(50000.0, vehicle.price.price)
        assertEquals("pounds", vehicle.price.currency)
        assertEquals("£50000.00 pounds", vehicle.priceAsString)
    }

    private fun getVehicleExample(): Vehicle {
        return try {
            Vehicle.create(
                "123",
                "Ford",
                "F-150",
                "20000",
                LocalDate.of(2025, 1, 1),
                "456",
                "pickup"
            )
        } catch (e: ValidationException) {
            throw RuntimeException(e)
        }
    }
}
