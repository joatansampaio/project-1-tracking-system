package com.metrostate.projectone.utils;

//Maven json-simple dependency imports
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

//Java API imports
import java.io.IOException;

public interface IFileHandler {
    JSONObject read(String fileName) throws IOException, ParseException;
    void write(JSONObject jsonObject);
}
