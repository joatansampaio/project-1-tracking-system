package com.metrostate.projectone.utils;


//Maven json-simple dependency imports
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//Java API imports
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.FileWriter;

// TODO: To be reviewed
// This class should be able to Import a file into the system
// as well as given a JsonString, export a JSON File.


//Successfully tested in main with this code
//FileHandler handler = new FileHandler();
//JSONObject jsonObject = handler.read("src/main/resources/inventory.json");
//System.out.println(jsonObject.toJSONString());

//Note: If ParseException catching is added,
// when you throw ParseException it requires an int for error type
//if (parser == null) {
//    throw new ParseException();

//}
public class FileHandler implements IFileHandler {
    //Initialized here to be able to use .close() if an exception occurs
    Reader reader = null;

    // We'll use the resources folder, and the fileName will always be a valid
    // json filename in main>resources

    /**
     * @param fileName The name of the json formatted file to be loaded.
     * @return JSONObject of the file if it loaded properly, null if the json file did not load properly.
     */
    @Override
    public JSONObject read(String fileName) throws IOException {
        try {
            JSONParser parser = new JSONParser();

            // Correct path for the main inventory  file should be src/main/resources/inventory.json
            reader = new FileReader(fileName);
            Object jsonObj = parser.parse(reader);
            reader.close();
            return (JSONObject) jsonObj;
        } catch (IOException | ParseException e) {
            reader.close();
            return null;
        }
    }
    
    // Write the inventory from a single dealership to json file in main>resources
    @Override
    public boolean writeDealerToJson(JSONObject jsonObj, String dealershipId) {
        try {
            String outputFile = "src/main/resources/" + dealershipId + ".json";
            FileWriter file = new FileWriter(outputFile);
            file.write(JsonHelper.prettify(jsonObj.toJSONString()));
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
  
    // Write a single vehicle to json file in main>resources
    //Not needed, is overcoding, could flood folder with files for a large company. That is bad!
    public boolean writeVehicleToJson(JSONObject jsonFormatVehicle, String dealerShipId, String vehicleId) {
        try {
            String outputFile = "src/main/resources/" + dealerShipId + "-" + vehicleId + ".json";
            FileWriter file = new FileWriter(outputFile);
            file.write(jsonFormatVehicle.toJSONString());
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}