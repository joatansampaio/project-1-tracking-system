package com.metrostate.projectone.utils;


//Maven json-simple dependency imports
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//Java API imports
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

// TODO: To be reviewed and tested
// This class should be able to Import a file into the system
// as well as given a JsonString, export a JSON File.

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
    @Override
    public JSONObject read(String fileName) throws IOException {


        try {
            JSONParser parser = new JSONParser();

            //Path for file might be wrong - suggest trying src/main/resources/inventory.json
            reader = new FileReader(fileName);
            Object jsonObj = parser.parse(reader);
            reader.close();
            return (JSONObject) jsonObj;
        } catch (IOException | ParseException e) {
            reader.close();
            return null;
        }

    }

    // Write the json file to main>resources
    @Override
    public void write(JSONObject jsonObject) {

    }
}