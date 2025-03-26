package edu.metrostate.trackingsystem;

import org.junit.jupiter.api.Test;
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
