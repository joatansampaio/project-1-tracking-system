package edu.metrostate.trackingsystem;

import edu.metrostate.trackingsystem.domain.models.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private final Scene scene = new Scene();

    @Test
    public void smokeTest() {
        setupSceneExample1();

        assertEquals(2, scene.getDealers().size());
        assertEquals(2, scene.getDealer("#1").getVehicles().size());
    }

    @Test
    public void addVehicle_DisabledVehicleAcquisition_Fail() {
        scene.setup()
             .dealer("dealer 1", true)
             .dealer("dealer 2", false)
             .vehicle("dealer#1 ford f-150 pickup 50000");

        assertFalse(scene.addVehicle("dealer#2 hyundai tucson suv 35000"));
        assertTrue(scene.addVehicle("dealer#1 hyundai tucson suv 35000")); // dealer 1 is enabled
    }

    @Test
    public void addVehicle_DuplicationIsPrevented() {
        scene.setup().dealer("dealer 1", true);

        assertTrue(scene.addVehicle("dealer#1 hyundai tucson suv 35000"));
        // 0 means that I am overriding the last vehicle index, essentially setting the same ID again
        // The vehicle info doesn't really matter, as the duplication check runs on the ID.
        assertFalse(scene.addVehicle("dealer#1 hyundai tucson suv 35000", 0));
    }

    @Test
    public void doNotDuplicateIds_OnImport() {
        // importing jsonTest.json (contains duplication)
        // out decision was to de-duplicate the ids by appending the dealerID.
        assertTrue(scene.setup().importJson());
        var allVehicles = new java.util.ArrayList<>(scene.getDealers()
                .stream()
                .flatMap(d -> d.getVehicles().stream())
                .toList());

        assertEquals(8, allVehicles.size());
        allVehicles.sort(Comparator.comparing(Vehicle::getVehicleId));

        assertAll("Ensuring that there's no duplicated vehicle IDS...",
                () -> assertEquals("1", allVehicles.get(0).getVehicleId()),
                () -> assertEquals("2", allVehicles.get(1).getVehicleId()),
                () -> assertEquals("3", allVehicles.get(2).getVehicleId()),
                () -> assertEquals("4", allVehicles.get(3).getVehicleId()),
                () -> assertEquals("800_4", allVehicles.get(4).getVehicleId()), //de-duplicated '4'
                () -> assertEquals("900_1", allVehicles.get(5).getVehicleId()), //de-duplicated '1'
                () -> assertEquals("900_2", allVehicles.get(6).getVehicleId()), //de-duplicated '2'
                () -> assertEquals("900_3", allVehicles.get(7).getVehicleId()) //de-duplicated '3'
        );
    }

    @Test
    public void importXML_SmokeTest() {
        // importing xmlTest.xml
        assertTrue(scene.setup().importXml());
        assertAll(
                () -> assertEquals(1, scene.getDealers().size()),
                () -> assertEquals("Wacky Bob’s Automall", scene.getDealers().get(0).getName()),
                () -> assertEquals("485", scene.getDealers().get(0).getDealershipId()),
                () -> assertEquals(4, scene.getDealers().get(0).getVehicles().size())
        );
    }

    @Test
    public void deleteVehicleTest() {
        setupSceneExample1();
        assertTrue(scene.deleteVehicle("vehicle#1"));
        assertTrue(scene.deleteVehicle("vehicle#2"));
        assertTrue(scene.deleteVehicle("vehicle#3"));
        //Non-existent
        assertFalse(scene.deleteVehicle("vehicle#4"));
    }

    private void setupSceneExample1() {
        scene.setup()
            .dealer("dealer 1", true)
            .dealer("dealer 2", true)
            .vehicle("dealer#1 ford f-150 pickup 50000")
            .vehicle("dealer#1 ford f-250 pickup 80000")
            .vehicle("dealer#2 ford f-350 pickup 100000");
    }
}
