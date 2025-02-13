package com.metrostate.projectone.controllers;

import com.metrostate.projectone.models.Vehicle;

import java.util.Scanner;

public interface IDealershipController {
    void printAllVehicles();
    boolean printAllDealers();
    boolean isValidDealershipID(String dealershipID);
    void printVehiclesForDealershipId(String dealershipId);
    void addVehicle(Scanner scan);
    void setDealerAcquisition(boolean isEnabled, String dealershipId);
    void importJsonFile(Scanner scan);
    void exportVehiclesToJson(Scanner scan);
    void exportDealerToJson(Scanner scan);
}
