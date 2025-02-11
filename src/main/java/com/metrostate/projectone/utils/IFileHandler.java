package com.metrostate.projectone.utils;

//Maven json-simple dependency imports
//https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

//Java API imports
import java.io.IOException;

public interface IFileHandler {
    /**
     * Reads a json formatted file to a JSONObject using the json-simple Maven dependency
     *
     * @param fileName The name of the json formatted file to be loaded.
     * @return JSONObject of the file if it loaded properly, null if the json file did not load properly.
     */
    JSONObject read(String fileName) throws IOException, ParseException;



    //Write a single vehicle to file (Used for exporting vehicles from the dealer to json) - not part of grading
    //See exportVehiclesToJson() in DealershipController
    /**
     * Not needed, is overcoding, we could potentially flood folder with files for a large company. That is bad!
     * Write a single vehicle to json file in src/main/resources/
     * Filename output is in format of dealerShipId-vehicleId.json
     *
     * @param jsonFormatVehicle A vehicle object formatted as JSON and stored as a JSONObject
     * @param dealerShipId A dealership ID to be used for the filename
     * @param vehicleId
     * @return true if the operation was completed, false if some error occurred
     */
    boolean writeVehicleToJson(JSONObject jsonFormatVehicle, String dealerShipId, String vehicleId);

    /**
     * Write the inventory from a single dealership to json file in src/main/resources/
     *
     * @param jsonObj Formatted JSON information about dealer
     * @param dealershipId The dealer's ID
     * @return True if successfully exported, false if unsuccessful
     */
    boolean writeDealerToJson(JSONObject jsonObj, String dealershipId);
}
