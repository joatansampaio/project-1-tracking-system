package com.metrostate.projectone.utils;

//Maven json-simple dependency imports
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

//Java API imports
import java.io.IOException;

public interface IFileHandler {
    /**
     * @param fileName The name of the json formatted file to be loaded.
     * @return JSONObject of the file if it loaded properly, null if the json file did not load properly.
     */
    JSONObject read(String fileName) throws IOException, ParseException;

    //Write a single vehicle to file (Used for exporting vehicles from the dealer to json) - not part of grading
    //See exportVehiclesToJson() in DealershipController
    boolean writeVehicleToJson(JSONObject jsonFormatVehicle, String dealerShipId, String vehicleId);

    boolean writeDealerToJson(JSONObject jsonObj, String dealershipId);
}
