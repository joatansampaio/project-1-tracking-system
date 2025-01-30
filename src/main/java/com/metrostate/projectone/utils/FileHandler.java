package com.metrostate.projectone.utils;

import org.json.simple.JSONObject;

// TODO: To be implemented
// This class should be able to Import a file into the system
// as well as given a JsonString, export a JSON File.
public class FileHandler implements IFileHandler {

    // We'll use the resources folder, and the fileName will always be a valid
    // json filename in main>resources
    @Override
    public JSONObject read(String fileName) {
        return null;
    }

    // Write the json file to main>resources
    @Override
    public void write(JSONObject jsonObject) {

    }
}