package edu.metrostate.dealership

import edu.metrostate.dealership.domain.models.Vehicle
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class IntegrationTest {
    private val scene = Scene()

    @Test
    fun smokeTest() {
        setupSceneExample1()

        Assertions.assertEquals(2, scene.dealers.size)
        // TODO: evaluate vehicles for dealer #1
    }

    @Test
    fun addVehicle_DisabledVehicleAcquisition_Fail() {
        scene.setup()
            .dealer("dealer 1", true)
            .dealer("dealer 2", false)
            .vehicle("dealer#1 ford f-150 pickup 50000")

        Assertions.assertFalse(scene.addVehicle("dealer#2 hyundai tucson suv 35000"))
        Assertions.assertTrue(scene.addVehicle("dealer#1 hyundai tucson suv 35000")) // dealer 1 is enabled
    }

    @Test
    fun addVehicle_DuplicationIsPrevented() {
        scene.setup().dealer("dealer 1", true)

        Assertions.assertTrue(scene.addVehicle("dealer#1 hyundai tucson suv 35000"))
        // 0 means that I am overriding the last vehicle index, essentially setting the same ID again
        // The vehicle info doesn't really matter, as the duplication check runs on the ID.
        Assertions.assertFalse(scene.addVehicle("dealer#1 hyundai tucson suv 35000", 0))
    }

    @Test
    fun doNotDuplicateIds_OnImport() {
        // importing jsonTest.json (contains duplication)
        // out decision was to de-duplicate the ids by appending the dealerID.
        Assertions.assertTrue(scene.setup().importJson())
        val allVehicles = scene.vehicles

        Assertions.assertEquals(8, allVehicles.size)
        allVehicles.sortedBy { v -> v.vehicleId }

        Assertions.assertAll(
            "Ensuring that there's no duplicated vehicle IDS...",
            Executable { Assertions.assertEquals("1", allVehicles[0].vehicleId) },
            Executable { Assertions.assertEquals("2", allVehicles[1].vehicleId) },
            Executable { Assertions.assertEquals("3", allVehicles[2].vehicleId) },
            Executable { Assertions.assertEquals("4", allVehicles[3].vehicleId) },
            Executable { Assertions.assertEquals("1-100", allVehicles[4].vehicleId) },  //de-duplicated '4'
            Executable { Assertions.assertEquals("2-200", allVehicles[5].vehicleId) },  //de-duplicated '1'
            Executable { Assertions.assertEquals("3-300", allVehicles[6].vehicleId) },  //de-duplicated '2'
            Executable { Assertions.assertEquals("4-400", allVehicles[7].vehicleId) } //de-duplicated '3'
        )
    }

    @Test
    fun importXML_SmokeTest() {
        // importing xmlTest.xml
        Assertions.assertTrue(scene.setup().importXml())
        Assertions.assertAll(
            Executable { Assertions.assertEquals(1, scene.dealers.size) },
            Executable { Assertions.assertEquals("Wacky Bob’s Automall", scene.dealers[0].name) },
            Executable { Assertions.assertEquals("485", scene.dealers[0].dealershipId) },
            Executable { Assertions.assertEquals(4, scene.vehicles.size) }
        )
    }

    @Test
    fun deleteVehicleTest() {
        setupSceneExample1()
        Assertions.assertTrue(scene.deleteVehicle("vehicle#1"))
        Assertions.assertTrue(scene.deleteVehicle("vehicle#2"))
        Assertions.assertTrue(scene.deleteVehicle("vehicle#3"))
        //Non-existent
        Assertions.assertFalse(scene.deleteVehicle("vehicle#4"))
    }

    private fun setupSceneExample1() {
        scene.setup()
            .dealer("dealer 1", true)
            .dealer("dealer 2", true)
            .vehicle("dealer#1 ford f-150 pickup 50000")
            .vehicle("dealer#1 ford f-250 pickup 80000")
            .vehicle("dealer#2 ford f-350 pickup 100000")
    }
}